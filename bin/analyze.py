#!/usr/bin/env python3

import os
import subprocess
import sys

from common import HOME, SMALI_FILE_EXT, LINE_PREFIX, ObjContext, PkgMap, SmaliFormatter, ClassMeta


class Analyzer:
    def __init__(self):
        self.release = None
        self.obj = None
        self.pkg = None

    def analyze(self, release):
        self.release = release
        self.obj = ObjContext()

        self.load()
        self.prepare_entry_object()
        self.auto_analyze()
        self.check_if_found()
        self.warn_about_unknown()
        self.save()
        self.generate_proguard_map()
        self.generate_dex2jar_map()

    def load(self):
        self.obj.load_obj('%s/res/analyzer.yaml' % HOME)
        for cls in self.obj.list_classes():
            raw = cls.raw
            if 'find' not in raw:
                raw['find'] = find = []
            for k, v in raw.items():
                if not k.startswith('find_'):
                    continue
                k = k[5:]
                not_ = False
                if k.startswith('not_'):
                    not_ = True
                    k = k[4:]
                multi = False
                if k.startswith('multi_'):
                    multi = True
                    k = k[6:]
                find.append([k, not_, multi] + (v if isinstance(v, list) else [v]))

    def prepare_entry_object(self):
        cls = self.obj.get_cls_for_name('Entry')
        if self.release:
            cls.obf_pkg = []
            cls.obf_cls = 'a'
            for i, o in enumerate(cls.methods.values()):
                if not o[0]:
                    n1, n2 = divmod(i, 26)
                    o[0] = ('' if not n1 else chr(96 + n1)) + chr(97 + n2)
        else:
            for name, o in cls.fields.items():
                if not o[0]:
                    o[0] = name
            for name, o in cls.methods.items():
                if not o[0]:
                    o[0] = name

    def auto_analyze(self):
        self.pkg = PkgMap()
        for cls in filter(lambda cls: cls.is_identified() and cls.orig_pkg[0] != 'broot', self.obj.list_classes()):
            self.pkg.add_identified_class(cls)

        changed = True
        not_identified = set(filter(lambda cls: not cls.is_identified() and cls.raw['find'], self.obj.list_classes()))
        req_max_unknown_pkg = None
        while True:
            if changed:
                max_unknown_pkg = 0
            elif req_max_unknown_pkg != 1000:
                max_unknown_pkg = req_max_unknown_pkg
            else:
                break

            changed = False
            req_max_unknown_pkg = 1000
            identified = set()
            for cls in not_identified:
                found, unknown_pkg = self.auto_identify_class(cls, max_unknown_pkg)
                if found:
                    changed = True
                    identified.add(cls)
                    self.pkg.add_identified_class(cls)
                elif unknown_pkg != -1 and unknown_pkg < req_max_unknown_pkg:
                    req_max_unknown_pkg = unknown_pkg

            not_identified.difference_update(identified)

        self.analyze_enums()

    def auto_identify_class(self, cls, max_unknown_pkg):
        if cls.is_identified() or not cls.raw['find']:
            return False, -1

        obf_pkg, unknown = self.pkg.get_obf_pkg_for_class(cls)
        if unknown > max_unknown_pkg:
            return False, unknown

        for q in cls.raw['find']:
            if not all([self.obj.parse_expr(o).are_deps_identified() for o in q[3:]]):
                return False, -1
            can_method = getattr(self, 'can_find_class_' + q[0], None)
            if can_method and not can_method(*([cls] + q[3:])):
                return False, -1

        def find_class(q):
            ret = set(getattr(self, 'find_class_' + q[0])(*([cls, (obf_pkg, unknown)] + q[3:])))
            if not q[2] and len(ret) > 1:
                raise Exception('Found multiple classes for %s and query %s: %s' % (cls.get_orig_name(), q, ret))
            return ret

        obf_names = None
        for q in cls.raw['find']:
            if q[1]:
                continue
            ret = find_class(q)
            if obf_names:
                obf_names.intersection_update(ret)
            else:
                obf_names = ret

            if not obf_names:
                raise Exception('Can\'t find class: ' + cls.get_orig_name())

        for q in cls.raw['find']:
            if not q[1]:
                continue
            obf_names.difference_update(find_class(q))

            if not obf_names:
                raise Exception('Can\'t find class: ' + cls.get_orig_name())

        obf_names = list(obf_names)
        if len(obf_names) != 1:
            raise Exception('Found multiple classes for %s: %s' % (cls.get_orig_name(), obf_names))

        parts = obf_names[0][1:-1].split('/')
        cls.obf_pkg = parts[:-1]
        cls.obf_cls = parts[-1]
        return True, -1

    def analyze_enums(self):
        for cls in self.obj.list_classes():
            if not cls.is_enum:
                continue

            edit = self.obj.edit_cls(cls)
            edit.find_method_def('clinit')
            obf_name = cls.get_obf_name(True)
            while edit.find_line(r' invoke-direct(|/range) \{(.+)\}, %s-><init>.+' % obf_name, where='down,in_method',
                                 error_if_missing=False):
                args = edit.vars[1]
                reg = args.split(', ')[1] if not edit.vars[0] else 'v' + str(int(args.split(' ')[0][1:]) + 1)
                edit.find_line(r' const-string(|/jumbo) %s, "(.+)"' % reg, where='up,in_method')
                name = edit.vars[1]
                edit.find_line(r' sput-object [pv]\d+, %s->(.+):%s' % (obf_name, obf_name), where='down,in_method')
                field = edit.vars[0]

                cls.fields[name] = [field, '$' + cls.orig_cls]

    def check_if_found(self):
        for cls in self.obj.list_classes():
            if cls.raw['find'] and not cls.is_identified():
                raise Exception('Failed to identify object: ' + cls.get_orig_name())

    def find_class_class(self, cls, pkg, expr):
        return [self.obj.expr_type(expr)]

    def find_class_by_string(self, cls, pkg, s):
        return self.find_classes_by_string(self.obj.expr(s, regex=True), pkg)

    def can_find_class_by_field(self, cls, field_name, field_modif='.+'):
        return self.obj.parse_expr(cls.fields[field_name][1]).are_deps_identified()

    def find_class_by_field(self, cls, pkg, field_name, field_modif='.+'):
        field_name, field_type = cls.fields[field_name]
        return self.find_classes_by_string(r'^%s\.field %s %s:%s$' % (
            LINE_PREFIX, field_modif, field_name, self.obj.expr_type(field_type)), pkg)

    def can_find_class_by_method(self, cls, name, modif='.+'):
        return all([self.obj.parse_expr(o).are_deps_identified() for o in cls.methods[name][1:]])

    def find_class_by_method(self, cls, pkg, method_name, method_modif='.+'):
        m = cls.methods[method_name]
        return self.find_classes_by_string(r'^%s\.method %s %s\(%s\)%s$' % (
            LINE_PREFIX, method_modif, m[0], ''.join(self.obj.expr_type_multi(*m[2:])), self.obj.expr_type(m[1])), pkg)

    def find_class_by_super(self, cls, pkg, other):
        return self.find_classes_by_string(r'^%s\.super %s$' % (LINE_PREFIX, self.obj.expr_type(other)), pkg)

    def find_class_by_implements(self, cls, pkg, other):
        return self.find_classes_by_string(r'^%s\.implements %s$' % (LINE_PREFIX, self.obj.expr_type(other)), pkg)

    def find_class_super_of(self, cls, pkg, obj_name):
        return [self.obj.edit_cls(obj_name[1:]).get_super()]

    def find_class_interface_of(self, cls, pkg, obj_name):
        return self.obj.edit_cls(obj_name[1:]).find_implemented()

    def warn_about_unknown(self):
        for cls in self.obj.list_classes():
            if not cls.is_identified():
                print('W: still unknown: ' + cls.get_orig_name())

    def save(self):
        os.makedirs('%s/build' % HOME, exist_ok=True)
        self.obj.save_obj('%s/build/obj.yaml' % HOME)

    def generate_proguard_map(self):
        def s2j(s):
            if s.startswith('$'):
                return self.obj.expr(s, type_only=True, obf=False, smali=False)
            return SmaliFormatter.s2j_type(s)

        os.makedirs('%s/build' % HOME, exist_ok=True)
        out = open('%s/build/obj.map' % HOME, 'w')

        for cls in self.obj.list_classes():
            out.write('%s -> %s:\n' % (cls.get_orig_name(), cls.get_obf_name()))
            for field_name, field_meta in cls.fields.items():
                out.write('    %s %s -> %s\n' % (s2j(field_meta[1]), field_name, field_meta[0]))
            for method_name, method_meta in filter(lambda x: x[0] not in ClassMeta.STD_METHODS, cls.methods.items()):
                out.write('    %s %s(%s) -> %s\n' % (
                    s2j(method_meta[1]),
                    method_name.partition('__')[0],
                    ','.join([s2j(o) for o in method_meta[2:]]),
                    method_meta[0]
                ))

    def generate_dex2jar_map(self):
        def s2j(s):
            if s.startswith('$'):
                return self.obj.expr(s, type_only=True, obf=False, smali=False)
            return SmaliFormatter.s2j_type(s)

        os.makedirs('%s/build' % HOME, exist_ok=True)
        out = open('%s/build/obj.d2j-map' % HOME, 'w')

        for orig, obf in self.pkg.map.items():
            if orig[-1] != obf[-1]:
                out.write('p %s=%s\n' % ('/'.join(obf), orig[-1]))

        for cls in self.obj.list_classes():
            obf_cls = '/'.join(cls.obf_pkg + [cls.obf_cls])
            if cls.orig_cls != cls.obf_cls:
                out.write('c %s=%s\n' % (obf_cls, cls.orig_cls))
            for name, f in cls.fields.items():
                if name != f[0]:
                    out.write('m %s.%s=%s\n' % (obf_cls, f[0], name))
            for name, m in cls.methods.items():
                if name != m[0] and name not in ClassMeta.STD_METHODS:
                    out.write('m %s.%s(%s)=%s\n' % (
                        obf_cls, m[0], ''.join([self.obj.expr_type(x) for x in m[2:]]), name.partition('__')[0]))

    @staticmethod
    def find_classes_by_string(s, pkg):
        pkg, depth = pkg
        smali_dir = HOME + '/app/smali/'
        grep = 'grep -P -l "%s" %s%s/*.' + SMALI_FILE_EXT if depth == 0 else 'grep -P -l -r "%s" %s%s'
        out = subprocess.check_output(grep % (s.replace('"', r'\"'), smali_dir, '/'.join(pkg)), shell=True).decode()
        return ['L' + f[len(smali_dir): -len(SMALI_FILE_EXT) - 1] + ';' for f in out.splitlines()]


if __name__ == '__main__':
    Analyzer().analyze(len(sys.argv) > 1 and sys.argv[1] == 'release')

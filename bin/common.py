import itertools
import os
import random
import re
import yaml


HOME = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
os.environ['MOD_HOME'] = HOME

SMALI_FILE_EXT = 'java'
LINE_PREFIX = ';// '
LINE_PREFIX_LEN = len(LINE_PREFIX)

PAT_SUPER_OPCODE = re.compile(r'^%s\.super (L.+;)$' % LINE_PREFIX, re.MULTILINE)
PAT_IMPLEMENTS_OPCODE = re.compile(r'^%s\.implements (L.+;)$' % LINE_PREFIX, re.MULTILINE)

config = yaml.load(open('%s/config.yaml' % HOME))


def load_obj():
    obj.load_obj('%s/build/obj.yaml' % HOME, True, True)


class ObjContext:
    def __init__(self):
        self.obj = None
        self.formatter = SmaliFormatter(self)

    def list_classes(self):
        return self.obj.values()

    def get_cls_for_name(self, name):
        cls = self.obj.get(name)
        if cls:
            return cls
        classes = list(filter(lambda cls: cls.orig_cls == name, self.obj.values()))
        if len(classes) < 1:
            raise Exception('Can\'t find class for simple name: ' + name)
        if len(classes) > 1:
            raise Exception('Found multiple classes for simple name: ' + name)
        return classes[0]

    def expr(self, expr, type_only=False, obf=True, smali=True, regex=False):
        return self.parse_expr(expr, type_only).output(obf, smali, regex)

    def expr_type(self, expr, *args, **kwargs):
        return self.expr(expr, True, *args, **kwargs)

    def expr_multi(self, *exprs, **kwargs):
        return [self.expr(expr, **kwargs) for expr in exprs]

    def expr_type_multi(self, *exprs, **kwargs):
        return [self.expr(expr, True, **kwargs) for expr in exprs]

    def parse_expr(self, *args, **kwargs):
        expr = Expression(self)
        expr.parse(*args, **kwargs)
        return expr

    def edit_cls(self, cls):
        if isinstance(cls, str):
            cls = self.get_cls_for_name(cls)
        return ClassEditor(self, cls)

    def load_obj(self, yaml_file, resolve_refs=False, load_supers=False):
        self.obj = {}

        for orig_name, d in yaml.load(open(yaml_file)).items():
            self.obj[orig_name] = ClassMeta.deserialize(orig_name, d)

        if resolve_refs:
            for cls in self.obj.values():
                for field in cls.fields.values():
                    field[1] = self.expr_type(field[1])
                for method in cls.methods.values():
                    method[1:] = self.expr_type_multi(*method[1:])

        if load_supers:
            loaded = set()
            for cls in self.obj.values():
                self._load_super(cls, loaded)

    def save_obj(self, yaml_file):
        d = {}
        for orig_name, cls in self.obj.items():
            d[orig_name] = cls.serialize()
        open(yaml_file, 'w').write(yaml.dump(d))

    def _load_super(self, cls, loaded):
        if not cls.super or cls in loaded:
            return
        for super_ in cls.super:
            super_ = self.get_cls_for_name(super_[1:])
            self._load_super(super_, loaded)
            cls.fields.update(super_.fields)
            cls.methods.update(super_.methods)
        loaded.add(cls)


class ClassNotIdentified(Exception):
    pass


class ClassMeta:
    STD_METHODS = {
        'toString': ['toString', 'Ljava/lang/String;'],
        'clinit': ['<clinit>', 'V'],
    }

    def __init__(self):
        self.orig_pkg = None
        self.orig_cls = None
        self.obf_pkg = None
        self.obf_cls = None
        self.super = None
        self.fields = None
        self.methods = None
        self.is_enum = None
        self.raw = None

    def is_identified(self):
        return bool(self.obf_cls)

    def get_orig_name(self, smali=False):
        return self.get_name(False, smali)

    def get_obf_name(self, smali=False):
        return self.get_name(True, smali)

    def get_name(self, obf=False, smali=False):
        if obf and not self.obf_cls:
            raise ClassNotIdentified(self.get_orig_name())
        names = self.obf_pkg + [self.obf_cls] if obf else self.orig_pkg + [self.orig_cls]
        return 'L' + '/'.join(names) + ';' if smali else '.'.join(names)

    def get_obf_file_name(self):
        if not self.obf_cls:
            raise ClassNotIdentified(self.get_orig_name())
        return '/'.join(self.obf_pkg + [self.obf_cls]) + '.' + SMALI_FILE_EXT

    def serialize(self):
        d = {}
        if self.is_identified():
            d['obf_name'] = self.get_obf_name()
        if self.fields:
            d['fields'] = self.fields

        methods = dict(filter(lambda x: x[0] not in self.STD_METHODS, self.methods.items()))
        if methods:
            d['methods'] = methods

        if self.super:
            d['super'] = self.super

        return d

    @classmethod
    def deserialize(clazz, orig_name, d):
        cls = ClassMeta()
        cls.raw = d

        parts = orig_name.split('.')
        cls.orig_pkg = parts[:-1]
        cls.orig_cls = parts[-1]

        obf_name = d.get('obf_name')
        if obf_name:
            parts = obf_name.split('.')
            cls.obf_pkg = parts[:-1]
            cls.obf_cls = parts[-1]
        elif d.get('is_real_name'):
            cls.obf_pkg = cls.orig_pkg
            cls.obf_cls = cls.orig_cls

        cls.fields = d.get('fields', {})
        cls.methods = d.get('methods', {})
        super_ = d.get('super')
        cls.super = [super_] if isinstance(super_, str) else super_
        cls.is_enum = d.get('is_enum', False)

        cls.methods.update(clazz.STD_METHODS)

        return cls


class Expression:
    def __init__(self, ctx):
        self.ctx = ctx
        self.parts = None

    def parse(self, expr, type_only=False):
        if expr.startswith('$') and not expr.startswith('${'):
            self.parts = [self._parse_part(expr[1:], type_only)]
            return

        parts = []
        pos = 0
        while True:
            pos2 = expr.find('${', pos)
            if pos2 == -1:
                if pos != len(expr):
                    parts.append(_ExpressionPart.literal(self.ctx, expr[pos:]))
                break
            if pos2 != pos:
                parts.append(_ExpressionPart.literal(self.ctx, expr[pos:pos2]))

            pos3 = expr.index('}', pos2 + 2)
            parts.append(self._parse_part(expr[pos2 + 2: pos3], type_only))

            pos = pos3 + 1

        self.parts = parts

    def obf_smali(self, *args, **kwargs):
        return self.output(True, True, *args, **kwargs)

    def orig_java(self, *args, **kwargs):
        return self.output(False, False, *args, **kwargs)

    def output(self, obf=False, smali=False, regex=False):
        return ''.join([part.output(obf, smali, regex) for part in self.parts])

    def are_deps_identified(self):
        return all([cls.is_identified() for cls in self.get_deps()])

    def get_deps(self):
        return list(itertools.chain(*[part.get_deps() for part in self.parts]))

    def _parse_part(self, expr, type_only):
        if type_only:
            return self._parse_type(expr)

        cls, act, member = expr.partition('->')
        if not act:
            cls, act, member = expr.partition('=>')
        if not act:
            return self._parse_type(expr)

        part = _ExpressionPart(self.ctx)
        part.cls = self.ctx.get_cls_for_name(cls)
        part.def_only = act == '=>'
        if member.endswith('()'):
            part.type = 'method'
            part.member = member[:-2]
        else:
            part.type = 'field'
            part.member = member

        return part

    def _parse_type(self, expr):
        pos = expr.find('[]')
        arr = 0
        if pos != -1:
            arr = (len(expr) - pos) // 2
            if not expr.endswith('[]' * arr):
                raise Exception()
            expr = expr[:pos]

        part = _ExpressionPart(self.ctx)
        part.type = 'class'
        part.cls = self.ctx.get_cls_for_name(expr)
        part.arr = arr
        return part


class _ExpressionPart:
    def __init__(self, ctx):
        self.ctx = ctx
        self.type = self.cls = self.literal = self.arr = self.member = self.def_only = None

    def output(self, obf=False, smali=False, regex=False):
        if self.type == 'literal':
            return self.literal

        ret = self._output(obf, smali)
        return ret if not regex else re.escape(ret)

    def get_deps(self):
        deps = []
        if self.type == 'literal':
            return deps
        if not self.def_only:
            deps.append(self.cls)
        if self.type == 'field':
            deps += self.ctx.parse_expr(self.cls.fields[self.member][1], type_only=True).get_deps()
        if self.type == 'method':
            for t in self.cls.methods[self.member][1:]:
                deps += self.ctx.parse_expr(t, type_only=True).get_deps()

        return deps

    def _output(self, obf=False, smali=False):
        if self.type == 'class':
            n = self.cls.get_name(obf, smali)
            return '[' * self.arr + n if smali else n + '[]' * self.arr
        if not smali or not obf:
            raise Exception('Not supported')
        if self.type == 'field':
            return self.ctx.formatter.field(self.cls, self.member, def_only=self.def_only)
        if self.type == 'method':
            return self.ctx.formatter.method(self.cls, self.member, def_only=self.def_only)

    @classmethod
    def literal(clazz, ctx, literal):
        part = _ExpressionPart(ctx)
        part.type = 'literal'
        part.literal = literal
        return part


class SmaliFormatter:
    PRIMITIVES_MAP = {'V': 'void', 'Z': 'boolean', 'I': 'int', 'B': 'byte', 'C': 'char', 'S': 'short', 'J': 'long',
                      'F': 'float', 'D': 'double'}

    def __init__(self, ctx):
        self.ctx = ctx

    def field(self, cls, name, def_only=False):
        cls, prefix = self._member(cls, def_only)
        f = cls.fields[name]
        return '%s%s:%s' % (prefix, f[0], self.ctx.expr_type(f[1]))

    def method(self, cls, name, def_only=False):
        cls, prefix = self._member(cls, def_only)
        m = cls.methods[name]
        return '%s%s(%s)%s' % (prefix, m[0], ''.join(self.ctx.expr_type_multi(*m[2:])), self.ctx.expr_type(m[1]))

    def _member(self, cls, def_only):
        if isinstance(cls, str):
            cls = self.ctx.get_cls_for_name(cls)
        if def_only:
            return cls, ''
        return cls, cls.get_obf_name(True) + '->'

    @classmethod
    def s2j_type(clazz, s):
        pos = 0
        arr = 0
        while s[pos] == '[':
            arr += 1
            pos += 1
        type_ = clazz.PRIMITIVES_MAP.get(s[pos])
        if not type_:
            if s[pos] != 'L':
                raise Exception(s)
            type_ = s[pos + 1: -1].replace('/', '.')
        return type_ + ('[]' * arr)


class ClassEditor:
    def __init__(self, ctx, cls, open=True):
        self.ctx = ctx
        self.cls = cls
        self.fmt = ctx.formatter
        self.enums_to_add = []

        self.parts = None
        self.curr = None
        self.vars = None

        self.last_result_reg = None
        self.last_result_postfix = None

        if open:
            self.open()

    def open(self):
        self.parts = [open('%s/app/smali/%s' % (HOME, self.cls.get_obf_file_name())).read()]
        self.curr = 0

    def get_super(self):
        return PAT_SUPER_OPCODE.search(self.get_contents()).group(1)

    def find_implemented(self):
        return PAT_IMPLEMENTS_OPCODE.findall(self.get_contents())

    def find_line(self, regex, where='', in_method=False, error_if_missing=True):
        if regex and regex[0] == ' ':
            regex = '   ' + regex

        where = where.split(',')
        dir = 'up' if 'up' in where else ('down' if 'down' in where else 'all')
        in_method = 'in_method' in where
        if in_method and dir == 'all':
            raise Exception()

        curr = 0
        if dir == "all":
            parts = [self.get_contents()]
        elif dir == "up":
            parts = [''.join(self.parts[:self.curr]), ''.join(self.parts[self.curr:])]
        elif dir == "down":
            parts = [''.join(self.parts[:self.curr + 1]), ''.join(self.parts[self.curr + 1:])]
            curr = 1
        else:
            assert False
        part = parts[curr]

        search = _re_search_last if dir == 'up' else re.search

        if in_method:
            m = search('^%s%s$' % (LINE_PREFIX, r'\.end method' if dir == 'down' else r'\.method .*'), part,
                       re.MULTILINE)
            if not m:
                raise Exception()
            parts[curr:curr + 1] = [part[:m.start()], part[m.start(): m.end() + 1], part[m.end() + 1:]]
            if dir == 'up':
                curr += 2
            part = parts[curr]

        m = search('^%s%s$' % (LINE_PREFIX, regex), part, re.MULTILINE)
        if not m:
            if error_if_missing:
                raise Exception('Can\'t find: ' + regex)
            return False

        parts[curr:curr + 1] = [part[:m.start()], part[m.start(): m.end() + 1], part[m.end() + 1:]]
        self.parts = parts
        self.curr = curr + 1
        self.vars = m.groups()

        return True

    def find_prologue(self, **kwargs):
        self.find_line(r" \.prologue", **kwargs)

    def prepare_after_prologue(self, method_name):
        self.find_method_def(method_name)
        self.find_prologue(where='down')
        self.prepare_to_insert(2)

    def prepare_after_invoke_init(self, name, **kwargs):
        self.find_line(r' invoke-direct[^ ]* \{([vp]\d+)([^}]*)\}, %s-><init>.*' %
                       self.ctx.get_cls_for_name(name).get_obf_name(True), **kwargs)
        self.prepare_to_insert()

    def prepare_to_insert(self, after=1, before=2):
        self.add_empty_line(before)
        self.add_empty_line(after)
        self.curr -= 1

    def prepare_to_insert_before(self, extra_line_before=False):
        self.curr -= 1
        self.prepare_to_insert(2, 2 if extra_line_before else 1)

    def find_class_def(self, **kwargs):
        self.find_line(r'\.class .*', **kwargs)

    def find_field_def(self, field_name, **kwargs):
        self.find_line(r'\.field .* %s' % re.escape(self.fmt.field(self.cls, field_name, True)), **kwargs)

    def find_method_def(self, method_name, **kwargs):
        self.find_line(r'\.method .* %s' % re.escape(self.fmt.method(self.cls, method_name, True)), **kwargs)

    def mod_class_def(self, access=None, unfinalize=False):
        self.find_class_def()
        line = self.parts[self.curr][LINE_PREFIX_LEN:].split(' ')
        if access:
            if line[1] in ('private', 'protected', 'public'):
                line[1] = access
            else:
                line.insert(1, access)
        if unfinalize:
            line.remove('final')
        self.comment_line()
        self.add_line(' '.join(line))

    def mod_field_def(self, field_name, access=None, unfinalize=False):
        self.find_field_def(field_name)
        line = self.parts[self.curr][LINE_PREFIX_LEN:].split(' ')
        if access:
            if line[1] in ('private', 'protected', 'public'):
                line[1] = access
            else:
                line.insert(1, access)
        if unfinalize:
            line.remove('final')
        self.comment_line()
        self.add_line(' '.join(line))

    def mod_method_def(self, method_name, access=None, unfinalize=False):
        self.find_method_def(method_name)
        line = self.parts[self.curr][LINE_PREFIX_LEN:].split(' ')
        if access:
            if line[1] in ('private', 'protected', 'public'):
                line[1] = access
            else:
                line.insert(1, access)
        if unfinalize:
            line.remove('final')
        self.comment_line()
        self.add_line(' '.join(line))

    def split_lines(self):
        self.parts[self.curr: self.curr + 1] = self.parts[self.curr].splitlines(True)

    def comment_line(self):
        part = self.parts[self.curr]
        self.parts[self.curr] = part[:LINE_PREFIX_LEN] + '# ' + part[LINE_PREFIX_LEN:]

    def add_invoke_entry(self, method_name, args='', ret=None):
        self.add_line(r' invoke-static {%s}, %s' % (args, self.fmt.method('Entry', method_name)))

        if not ret:
            return
        postfix = '' if self.ctx.get_cls_for_name('Entry').methods[method_name][1][0] not in ('L', '[') else '-object'
        self.add_line(r' move-result%s %s' % (postfix, ret))

        self.last_result_reg = ret
        self.last_result_postfix = postfix

    def add_ret_if_result(self, nez, ret=None, postfix=None):
        label = '%04x' % random.randrange(0, 65536)
        self.add_line(' if-%s %s, :mod_%s' % ('eqz' if nez else 'nez', self.last_result_reg, label))

        if ret is None:
            self.add_line(' return-void')
        else:
            if ret == 'result':
                ret = self.last_result_reg
                postfix = self.last_result_postfix
            self.add_line(' return%s %s' % (postfix, ret))
        self.add_line(' :mod_%s' % label)

    def add_empty_line(self, num=1):
        self.curr += 1
        self.parts.insert(self.curr, (LINE_PREFIX.strip() + '\n') * num)

    def add_line(self, line='', dont_prefix=False):
        if line and line[0] == ' ':
            line = '   ' + line
        if not dont_prefix:
            line = LINE_PREFIX + line

        self.curr += 1
        self.parts.insert(self.curr, line.rstrip() + '\n')

    def add_enum(self, *args):
        self.enums_to_add.append(args)

    def replace_in_line(self, *args):
        line = self.parts[self.curr].replace(*args)
        self.comment_line()
        self.add_line(line, dont_prefix=True)

    def save(self):
        self.apply_enums()
        open('%s/app/smali/%s' % (HOME, self.cls.get_obf_file_name()), 'w').write(self.get_contents())

    def apply_enums(self):
        if not self.enums_to_add:
            return
        obf_name = self.cls.get_obf_name(True)

        self.find_method_def('clinit')
        self.find_line(r' new-array v0, v0, \[%s' % obf_name, where='down')
        self.find_line(r' const/4 v0, 0x(\d+)', where='up')
        old_size = int(self.vars[0], 16)
        new_size = old_size + len(self.enums_to_add)
        self.replace_in_line('const/4 v0, 0x%x' % old_size, 'const v0, 0x%x' % new_size)
        self.find_line(' return-void', where='down')
        self.curr -= 1
        self.prepare_to_insert(2, 1)

        idx = old_size
        for name, in self.enums_to_add:
            self.add_line(r' new-instance v1, %s' % obf_name)
            self.add_line(r' const-string/jumbo v2, "%s"' % name)
            self.add_line(r' const v3, %d' % idx)
            self.add_line(r' invoke-direct {v1, v2, v3}, %s-><init>(Ljava/lang/String;I)V' % obf_name)
            self.add_line(r' sput-object v1, %s->%s:%s' % (obf_name, name, obf_name))
            self.add_line(r' aput-object v1, v0, v3')
            idx += 1
        self.find_line(r'# static fields')
        for name, in self.enums_to_add:
            self.add_line(r'.field public static final enum %s:%s' % (name, obf_name))

    def get_contents(self):
        return ''.join(self.parts)


class PkgMap:
    def __init__(self):
        self.map = {}

    def add_identified_class(self, cls):
        tmp, unknown = self.get_obf_pkg_for_class(cls)
        if not unknown:
            return
        orig_pkg = cls.orig_pkg
        obf_pkg = cls.obf_pkg
        l = len(orig_pkg)
        if l != len(obf_pkg):
            raise Exception(cls.get_orig_name())

        for i in range(l - unknown + 1, l + 1):
            self.map[self._to_key(orig_pkg[:i])] = obf_pkg[:i]

    def get_obf_pkg_for_class(self, cls):
        orig_pkg = cls.orig_pkg

        obf_pkg = self.map.get(self._to_key(orig_pkg))
        if obf_pkg:
            return obf_pkg, 0

        unknown = 1
        while unknown < len(orig_pkg):
            obf_pkg = self.map.get(self._to_key(orig_pkg[:-unknown]))
            if obf_pkg:
                return obf_pkg, unknown
            unknown += 1

        return [], unknown

    @staticmethod
    def _to_key(pkg):
        return tuple(pkg)


def _re_search_last(*args):
    return list(re.finditer(*args))[-1]


obj = ObjContext()
expr = obj.expr
expr_multi = obj.expr_multi
expr_type = obj.expr_type
expr_type_multi = obj.expr_type_multi
edit_cls = obj.edit_cls

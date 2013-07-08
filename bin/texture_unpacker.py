#!/usr/bin/env python3

import os
import sys
from PIL import Image


NINEPATCH_BODY_SCALING_LIMIT = 4

class Unpacker:
    def __init__(self, atlas_file, texture_dir=None):
        self.atlas_file = atlas_file
        self.texture_dir = texture_dir if texture_dir else os.path.dirname(atlas_file)
        self.atlas = None

    def unpack(self, out, scale=1.0):
        if not self.atlas:
            self.parse_atlas()

        if not os.path.exists(out):
            os.makedirs(out)
        for page in self.atlas.pages:
            im = Image.open(os.path.join(self.texture_dir, page.name))
            for image in page.images:
                p = image.params
                xy_x, xy_y = p['xy']
                size_x, size_y = p['size']
                orig_x, orig_y = p['orig']
                offset_x, offset_y = p['offset']
                split = p.get('split')

                margin = 1 if split and scale == 1.0 else 0
                im2 = Image.new('RGBA', (orig_x + 2 * margin, orig_y + 2 * margin))
                im2.paste(im.crop((xy_x, xy_y, xy_x + size_x, xy_y + size_y)), (offset_x + margin, offset_y + margin))

                if not split:
                    if scale != 1.0:
                        im2 = im2.resize((max(1, round(orig_x * scale)), max(1, round(orig_y * scale))),
                                         Image.ANTIALIAS)
                    im2.save('%s/%s.png' % (out, image.name))
                else:
                    if scale == 1.0:
                        im2.paste((0, 0, 0, 255), (split[0] + 1, 0, orig_x - split[1] + 1, 1))
                        im2.paste((0, 0, 0, 255), (0, split[2] + 1, 1, orig_y - split[3] + 1))
                    else:
                        h = split[0], orig_x - split[0] - split[1], split[1]
                        v = split[2], orig_y - split[2] - split[3], split[3]
                        h2 = (
                            max(1, round(h[0] * scale)),
                            h[1] if h[1] < NINEPATCH_BODY_SCALING_LIMIT else max(1, round(h[1] * scale)),
                            max(1, round(h[2] * scale))
                        )
                        v2 = (
                            max(1, round(v[0] * scale)),
                            v[1] if v[1] < NINEPATCH_BODY_SCALING_LIMIT else max(1, round(v[1] * scale)),
                            max(1, round(v[2] * scale))
                        )

                        im3 = Image.new('RGBA', (sum(h2) + 2, sum(v2) + 2))
                        for y in range(3):
                            for x in range(3):
                                im3.paste(im2.crop((sum(h[:x]), sum(v[:y]), sum(h[:x + 1]), sum(v[:y + 1]))).resize(
                                    (h2[x], v2[y]), Image.ANTIALIAS), (sum(h2[:x]) + 1, sum(v2[:y]) + 1))
                        im3.paste((0, 0, 0, 255), (h2[0] + 1, 0, h2[0] + h2[1] + 1, 1))
                        im3.paste((0, 0, 0, 255), (0, v2[0] + 1, 1, v2[0] + v2[1] + 1))
                        im2 = im3
                    im2.save('%s/%s.9.png' % (out, image.name))

        open(os.path.join(out, 'pack.json'), 'w').write('''
        {
            filterMin: %s,
            filterMag: %s,
        }
        ''' % (page.params['filter'][0], page.params['filter'][1]))

    def parse_atlas(self):
        self.atlas = Atlas()
        f = open(self.atlas_file)

        page = None
        image = None
        for l in f:
            l = l.strip()
            if l == '':
                page = None
                image = None
                continue

            p = l.find(': ')
            if p != -1:
                key = l[:p]
                val = l[p + 2:]
                if not image:
                    if key == 'filter':
                        val = [x.strip() for x in val.split(',')]
                    page.params[key] = val
                else:
                    if key == 'rotate':
                        val = val == 'true'
                    elif key in ('xy', 'size', 'orig', 'offset', 'split'):
                        val = [int(x.strip()) for x in val.split(',')]
                    elif key == 'index':
                        val = int(val)
                    image.params[key] = val
                continue

            if not page:
                page = AtlasPage(l)
                image = None
                self.atlas.pages.append(page)
            else:
                image = AtlasImage(l)
                page.images.append(image)

        self.verify_atlas()

    def verify_atlas(self):
        for page in self.atlas.pages:
            for k, v in page.params.items():
                if k == 'format':
                    if v != 'RGBA8888':
                        raise Exception('Unsupported format: ' + v)
                elif k == 'filter':
                    pass
                elif k == 'repeat':
                    if v != 'none':
                        raise Exception('Unsupported repeat: ' + v)
                else:
                    raise Exception('Unknown param name: ' + k)

            for image in page.images:
                for k, v in image.params.items():
                    if k in ('xy', 'size', 'orig', 'offset', 'split'):
                        pass
                    elif k == 'rotate':
                        if v:
                            raise Exception('Rotate param isn\'t supported')
                    elif k == 'index':
                        if v != -1:
                            raise Exception('Index param isn\'t supported')
                    else:
                        raise Exception('Unknown param name: ' + k)

    def save_atlas(self, out):
        out = open(out, 'w')
        for page in self.atlas.pages:
            out.write(page.name + '\n')
            self.write_params(out, page.params, ['format', 'filter', 'repeat'])
            for image in page.images:
                out.write(image.name + '\n')
                self.write_params(out, image.params, ['rotate', 'xy', 'size', 'split', 'orig', 'offset', 'index'], '  ')
        out.close()

    def write_params(self, out, params, names, indent=''):
        for k in names:
            v = params.get(k)
            if v is None:
                continue
            if isinstance(v, bool):
                v = 'true' if v else 'false'
            elif isinstance(v, (tuple, list)):
                v = ', '.join([str(x) for x in v])
            else:
                v = str(v)
            out.write(indent + k + ': ' + v + '\n')


class Atlas:
    def __init__(self):
        self.pages = []


class AtlasPage:
    def __init__(self, name=None):
        self.name = name
        self.params = {}
        self.images = []


class AtlasImage:
    def __init__(self, name=None):
        self.name = name
        self.params = {}


if __name__ == '__main__':
    argv = sys.argv
    Unpacker(argv[1]).unpack(argv[2], 1 if len(argv) < 4 else float(argv[3]))

#!/usr/bin/env python3

import os
import re
import shutil
import subprocess
import tempfile
from itertools import tee, filterfalse
from PIL import Image

from common import HOME
import texture_unpacker


PAT_NEMESIS_COMPASS = re.compile(
'''^    compass: \{
      height: ([\d.]+),
      width: ([\d.]+),
      x: ([\d.]+),
      y: ([\d.]+)
    }$''', re.MULTILINE)


def main():
    cwd = os.getcwd()
    os.chdir(HOME)
    try:
        resize('hvga', .6, (24, 30, 36))
        resize('qvga', .4, (16, 20, 24))
    finally:
        os.chdir(cwd)


def resize(name, scale, coda_sizes):
    shutil.rmtree('build/assets/data-%s' % name, ignore_errors=True)

    # Create dirs
    for f in 'common', 'packed', 'portal_info', 'upgrade':
        os.makedirs('build/assets/data-%s/%s' % (name, f))

    # Copy some files
    for f in 'inconsolata-14.fnt', 'inconsolata-14.png', 'inconsolata-28.fnt', 'inconsolata-28.png', 'coda-x-small.fnt':
        shutil.copy('app/assets/common/data/%s' % f, 'build/assets/data-%s/common' % name)
    shutil.copy('app/assets/portal_info/data/portal_ui.json', 'build/assets/data-%s/portal_info' % name)

    # nemesis.json
    def _repl(m):
        return \
'''    compass: {
      height: %d.0,
      width: %d.0,
      x: %d.0,
      y: %d.0
    }''' % tuple([round(float(m.group(i)) * scale) for i in range(1, 5)])

    s = open('app/assets/common/data/nemesis.json').read()
    s = PAT_NEMESIS_COMPASS.sub(_repl, s, 1)
    open('build/assets/data-%s/common/nemesis.json' % name, 'w').write(s)

    # Resize upgrade/data*/* images
    for f in os.listdir('app/assets/upgrade/data'):
        im = Image.open('app/assets/upgrade/data/%s' % f)
        im.resize((round(im.size[0] * scale), round(im.size[1] * scale)), Image.ANTIALIAS).save(
            'build/assets/data-%s/upgrade/%s' % (name, f))

    # Resize atlases
    for f1, f2 in ('packed', 'common'), ('packed', 'upgrade'):
        d = tempfile.mkdtemp()
        texture_unpacker.Unpacker('app/assets/%s/data/%s.atlas' % (f1, f2)).unpack(d, scale)

        # For common.atlas copy fonts
        if f2 == 'common':
            for size, font_name in zip(coda_sizes, ('sm', 'med', 'lg')):
                shutil.copy('res/fonts/coda-%d.fnt' % size,
                            'build/assets/data-%s/common/coda-%s.fnt' % (name, font_name))
                shutil.copy('res/fonts/coda-%d_0.png' % size, '%s/coda-%s.png' % (d, font_name))

        shutil.copy('res/lowres/%s-pack.json' % f2, '%s/pack.json' % d)
        texture_pack(d, 'build/assets/data-%s/%s' % (name, f1), f2)
        shutil.rmtree(d)

    # Resize "magic" portal_ui.atlas
    # Repack portal, energy-alien and energy-resistance images only, then readd additional "images" to the atlas file
    u = texture_unpacker.Unpacker('app/assets/portal_info/data/portal_ui.atlas')
    u.parse_atlas()
    page = u.atlas.pages[0]
    page.images, images2 = partition(lambda im: im.name in ('portal', 'energy-alien', 'energy-resistance'), page.images)
    page.images = list(page.images)
    d = tempfile.mkdtemp()
    u.unpack(d, scale)
    shutil.copy('res/lowres/portal_ui-pack.json', '%s/pack.json' % d)
    texture_pack(d, 'build/assets/data-%s/portal_info' % name, 'portal_ui')

    u = texture_unpacker.Unpacker('build/assets/data-%s/portal_info/portal_ui.atlas' % name)
    u.parse_atlas()
    page = u.atlas.pages[0]
    for im in images2:
        p = im.params
        p['xy'] = round(p['xy'][0] * scale), round(p['xy'][1] * scale)
        p['size'] = round(p['size'][0] * scale), round(p['size'][1] * scale)
        p['orig'] = round(p['orig'][0] * scale), round(p['orig'][1] * scale)
        page.images.append(im)
    u.save_atlas('build/assets/data-%s/portal_info/portal_ui.atlas' % name)


def texture_pack(in_dir, out_dir, name):
    subprocess.check_call(
        'java -cp lib/gdx.jar:lib/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 %s %s %s' % (
            in_dir, out_dir, name), shell=True)


def partition(pred, iterable):
    t1, t2 = tee(iterable)
    return filter(pred, t2), filterfalse(pred, t1)


if __name__ == '__main__':
    main()

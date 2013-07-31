#!/usr/bin/env python3

import shutil
import sys
import os

from common import HOME


def copy_assets(names=None):
    dir1 = HOME + '/build/assets'
    if names is None:
        if not os.path.exists(dir1):
            return
        names = os.listdir(dir1)

    for n1 in names:
        dir2 = dir1 + '/' + n1
        for n2 in os.listdir(dir2):
            if os.path.isdir(dir2 + '/' + n2):
                shutil.copytree(dir2 + '/' + n2, '%s/app/assets/%s/%s' % (HOME, n2, n1))


if __name__ == '__main__':
    copy_assets(None if len(sys.argv) < 2 else sys.argv[1:])

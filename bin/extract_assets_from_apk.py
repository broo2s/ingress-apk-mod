#!/usr/bin/env python3

import os
import shutil
import sys
import zipfile

from common import HOME


def extract(apk, name):
    z = zipfile.ZipFile(apk)
    out = '%s/build/assets/%s' % (HOME, name)
    os.makedirs(out)

    for n in z.namelist():
        n2 = n.split(os.sep)
        if len(n2) <= 3 or n2[-2] != 'data' or n2[-3] not in ('common', 'packed', 'portal_info', 'upgrade'):
            continue
        os.makedirs('%s/%s' % (out, n2[-3]), exist_ok=True)
        f = open('%s/%s/%s' % (out, n2[-3], n2[-1]), 'wb')
        shutil.copyfileobj(z.open(n), f)


if __name__ == '__main__':
    extract(sys.argv[1], sys.argv[2])

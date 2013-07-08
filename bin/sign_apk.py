#!/usr/bin/env python3

import subprocess
import sys

from common import config


def main(apk, release):
    p = config['signing']['release' if release else 'debug']
    subprocess.check_call(r'''
        jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore "%s" -storepass "%s" %s "%s"
    ''' % (p['store'], p['pass'], apk, p['alias']), shell=True)


if __name__ == '__main__':
    main(sys.argv[1], len(sys.argv) > 2 and sys.argv[2] == 'release')

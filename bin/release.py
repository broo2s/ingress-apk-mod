#!/usr/bin/env python3

import subprocess

from common import config


def main():
    subprocess.check_call(r'''
        version=%s

        f=`grep apkFileName $MOD_HOME/app/apktool.yml`
        f=${f:13:${#f}-17}

        $MOD_HOME/bin/clean_app.py
        $MOD_HOME/bin/analyze.py release

        $MOD_HOME/bin/main.py
        $MOD_HOME/bin/copy_assets.py
        $MOD_HOME/bin/build.py release
        rm -f $MOD_HOME/$f-broot-$version.apk
        zipalign 4 $MOD_HOME/app/dist/$f.apk $MOD_HOME/$f-broot-$version.apk


        $MOD_HOME/bin/clean_app.py

        $MOD_HOME/bin/main.py
        $MOD_HOME/bin/copy_assets.py
        $MOD_HOME/bin/mute.py
        $MOD_HOME/bin/build.py release
        rm -f $MOD_HOME/$f-broot-$version-mute.apk
        zipalign 4 $MOD_HOME/app/dist/$f.apk $MOD_HOME/$f-broot-$version-mute.apk
    ''' % config['version'], shell=True, executable='/bin/bash')


if __name__ == '__main__':
    main()

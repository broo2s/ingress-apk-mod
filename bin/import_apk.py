#!/usr/bin/env python3

import sys
import subprocess

from common import LINE_PREFIX


def main(apk):
    subprocess.check_call(r'''
        rm -rf $MOD_HOME/app
        java -jar $MOD_HOME/lib/apktool.jar d -d -o $MOD_HOME/app --debug-line-prefix '%s' "%s"
        mkdir -p $MOD_HOME/app  # to not mess up everything if apktool fail
        cp $MOD_HOME/res/app.gitignore $MOD_HOME/app/.gitignore

        pushd $MOD_HOME/app
        git init
        git add -f .
        git commit -m "Import."
        git gc
        popd

        $MOD_HOME/bin/analyze.py
    ''' % (LINE_PREFIX, apk), shell=True, executable='/bin/bash')


if __name__ == '__main__':
    main(sys.argv[1])

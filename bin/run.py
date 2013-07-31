#!/usr/bin/env python3

import subprocess

from common import HOME


def main():
    subprocess.check_call(r'''
        $MOD_HOME/bin/build.py
        adb install -r $MOD_HOME/app/dist/*.apk

        pkg_name=`head -n 2 $MOD_HOME/app/AndroidManifest.xml | tail -n 1`; pkg_name=${pkg_name%\"*}; pkg_name=${pkg_name##*\"}
        adb shell am start -n $pkg_name/com.nianticproject.ingress.NemesisActivity
    ''', shell=True)


if __name__ == '__main__':
    main()

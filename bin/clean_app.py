#!/usr/bin/env python3

import subprocess

from common import HOME


def main():
    subprocess.check_call(r'''
        pushd $MOD_HOME/app
        git reset --hard
        git clean -f -d -x -e /*.iml
        popd
    ''', shell=True, executable='/bin/bash')


if __name__ == '__main__':
    main()

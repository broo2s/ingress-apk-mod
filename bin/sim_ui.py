#!/usr/bin/env python3

import subprocess
import sys

from common import config
from build import generate_build_config


def main(*args):
    generate_build_config(False)

    subprocess.check_call(r'''
        rm -rf $MOD_HOME/build/sim $MOD_HOME/build/sim-classes
        mkdir -p $MOD_HOME/build/sim $MOD_HOME/build/sim-classes
        cp -r $MOD_HOME/ifc/* $MOD_HOME/src/* $MOD_HOME/sim/* $MOD_HOME/build/sim
        cp $MOD_HOME/build/BuildConfig.java $MOD_HOME/build/sim/broot/ingress/mod

        javac -Xlint:-options -g -source 7 -target 7 -cp "$MOD_HOME/lib/*" -d $MOD_HOME/build/sim-classes `find $MOD_HOME/build/sim -type f -iname "*.java"`

        pushd $MOD_HOME
        java -cp "$MOD_HOME/build/sim-classes:$MOD_HOME/lib/*" broot.ingress.sim.SimUi %s
        popd
    ''' % ' '.join(args), shell=True, executable='/bin/bash')


if __name__ == '__main__':
    main(*sys.argv[1:])

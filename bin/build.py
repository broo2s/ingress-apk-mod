#!/usr/bin/env python3

import os
import re
import subprocess
import sys

from common import HOME, config


PAT_MAPS_KEY = re.compile(r'"com\.google\.android\.maps\.v2\.API_KEY" android:value=".*"')


def main(release):
    generate_build_config(release)

    subprocess.check_call(r'''
        builddir="$MOD_HOME/build"

        rm -rf $builddir/ifc $builddir/src $builddir/proguard $builddir/dex.apk $builddir/smali $MOD_HOME/app/smali/broot $MOD_HOME/app/smali/a/*.smali
        mkdir -p $builddir/ifc $builddir/src

        javac -Xlint:-options -g:none -source 6 -target 6 -cp $MOD_HOME/lib/android.jar:$MOD_HOME/lib/gdx.jar -d $builddir/ifc `find -H $MOD_HOME/ifc -type f -iname "*.java"`
        javac -Xlint:-options -g -source 6 -target 6 -cp $builddir/ifc:$MOD_HOME/lib/android.jar:$MOD_HOME/lib/gdx.jar -d $builddir/src `find -H $MOD_HOME/src -type f -iname "*.java" -not -name BuildConfig.java` $MOD_HOME/build/BuildConfig.java

        proguard.sh @$MOD_HOME/res/%s.pg

        dx --dex --output=$builddir/dex.apk $builddir/proguard.zip
        java -jar $MOD_HOME/lib/baksmali.jar $builddir/dex.apk -o $builddir/smali

        cp -r $builddir/smali/* $MOD_HOME/app/smali/
        java -jar $MOD_HOME/lib/apktool.jar b%s $MOD_HOME/app
        $MOD_HOME/bin/sign_apk.py `ls $MOD_HOME/app/dist/*ingress-*.apk` %s
    ''' % (('release', '', 'release') if release else ('debug', ' -d', 'debug')), shell=True)


def generate_build_config(release):
    maps_key = config['maps_key']['release' if release else 'debug']
    if maps_key:
        s = open('%s/app/AndroidManifest.xml' % HOME).read()
        s = PAT_MAPS_KEY.sub('"com.google.android.maps.v2.API_KEY" android:value="%s"' % maps_key, s)
        open('%s/app/AndroidManifest.xml' % HOME, 'w').write(s)

    assets_dir = HOME + '/app/assets/common'
    assets = [x for x in os.listdir(assets_dir) if os.path.isdir(assets_dir + '/' + x)]
    bc = {
        'MOD_VERSION': '"' + config['version']
                       + ('' if os.path.exists(HOME + '/app/assets/sounds') else '-mute')
                       + ('' if release else '-dev')
                       + '"',
        'AVAILABLE_ASSETS': 'new String[] {' + ', '.join(['"' + x + '"' for x in assets]) + '}',
    }
    bc_str = open(HOME + '/src/broot/ingress/mod/BuildConfig.java').read()
    for k, v in bc.items():
        bc_str = bc_str.replace(k + ' = null', k + ' = ' + v)
    open(HOME + '/build/BuildConfig.java', 'w').write(bc_str)


if __name__ == '__main__':
    main(len(sys.argv) > 1 and sys.argv[1] == 'release')

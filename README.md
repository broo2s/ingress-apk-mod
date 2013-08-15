Ingress "broot" mod
========================

It's a modification of official Ingress app for Android. It works by patching apk file, project sources don't contain any prioprietary bits of NianticLabs.

Unfortunately it's not that easy to build it from sources, because it was developed as a set of helper scripts and it depends highly on an environment and many additional tools. For this reason at this moment it's not recommended for regular users. It's for developers. If you aren't one then search for already built apks on my site: http://ingress-apk-mod.o4kapuk.info/

Features
--------

- new items screen: clean design, quick access, items counters
- support for HVGA and QVGA screens
- muted version of an app - it's a lot smaller
- UI tweaks:
    - disable scanner zoom in anim - it's GPU consuming and delays portal dialog opening
    - disable vectors to portals
    - disable portal particles (XM "fountain" above portals)
    - disable inventory items rotation and rendering
    - revert to old hack anim
    - revert to old deploy behavior (auto highest to lowest and lowest to highest)
    - disable recycle animation
    - change time format in chat
- real-time distance to portal on portal info screen - it's good for deploying resonators close to 40m distance
- keys number on portal info screen
- option to hide unwanted menu tabs
- fullscreen mode
- screen-always-on mode

Other features:

- automatically identifies obfuscated names using declarative-style configuration
- easily deploy multiple UI variants (e.g. different themes) within one apk file
- simulator of UI components
- texture unpacker - separate individual images from *.atlas and related png files

News
----

15.08.2013 – Migrated to 1.33.0

1.08.2013 – **v1.32.1-broot-1.0.2 has been released!**

Changes from 1.0.1:

- Migrated to 1.32.1
- Added option to keep screen always on. Thank you, @dege88!
- Added option to disable ingame vibration. Thank you, @onovy and @dege88!
- Resurrected low-res skins. Thank you, @ZsBT!
- Added option to revert to old deployment process
- Added hack type with new transparent window but without slow animation
- Some other minor improvements

18.07.2013 – **v1.31.1-broot-1.0.1 has been released!**

Changes from 1.0.0:

- Migrated to 1.31.1
- Added option to disable recycle animation
- Added option to disable items rotation and rendering
- Added option to choose time format in chat
- Some bugs fixed

07.07.2013 – **v1.30.2-broot-1.0.0 has been released!**

- Initial version


Quick build instructions
------------------------

1. Import Ingress apk using import_apk.py script. Names analyzer will be called automatically.
1. (Optional) Run prepare_lowres.py if you want to add support for HVGA and QVGA.
1. Run release.py to create two optimized apk files: regular and muted.

Files and directories
---------------------

Dirs:

- bin - Python scripts to do various tasks
- src - Java sources of this mod
- ifc - Java API of some 3rd party classes to use them in src - they're in classpath of src, but they aren't included in app's code
- lib - dependent tools and libraries
- res - mod resources
- sim - java sources of UI simulator
- app - decoded application
- build - tmp dir

Scripts:

- import_apk.py - decode Ingress apk file into "app" directory, then run analyze.py
- analyze.py - run names analyzer using res/analyzer.yaml config file and store results in build/obj.* files
- prepare_lowres.py - create HVGA and QVGA assets from "app" and store them in build/assets
- extract_assets_from_apk.py - extract "data" assets from given apk file and store them in build/assets
- clean_app.py - clean up any changes to "app" directory
- copy_assets.py - copy assets from build/assets to "app"
- main.py - patch with most changes
- debug.py - patch enabling debug/development mode
- mute.py - patch removing all sounds to get smaller apk file
- build.py - build and sign apk
- sign_apk.py - sign built apk
- run.py - build, sign and run app
- release.py - build two apk files: regular and muted
- sim_ui.py - run UI simulator
- texture_unpacker.py - unpack image atlases

Patching scripts won't work with already patched sources, so usual use case is to run clean_app.py followed by a set of main.py, copy_assets.py, debug.py, mute.py to get what you want.

For now there is no usage help for scripts - look into sources.

Dependencies
------------

It depends on multiple jars and command line tools:

- standard UNIX commands/tools: cp, mv, rm, mkdir, find, grep, etc.
- bash
- Python3
- JDK7, java and javac in PATH
- apktool.jar v2.x (must be build from sources, see https://github.com/brutall/brut.apktool/tree/wip-2.0)
- baksmali.jar v1.4.2
- libgdx v0.9.7: gdx.jar, gdx-natives.jar, gdx-tools.jar, gdx-backend-lwjgl.jar, gdx-backend-lwjgl-natives.jar
- android.jar from Android SDK
- proguard.sh in PATH
- git in PATH
- dx, adb, zipalign, jarsigner in PATH

Of course some of these deps are needed for some actions only.

I want to create a "deps pack" to simply unpack it into lib directory, but it's not high on my todo list.

License
-------

This mod is licensed under Apache License, Version 2.0 .

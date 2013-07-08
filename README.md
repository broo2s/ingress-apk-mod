Ingress "broot" mod
========================

It's a modification of official Ingress app for Android. It works by patching apk file, project sources don't contain any prioprietary bits of NianticLabs.

Unfortunately it's not that easy to build it from sources, because it was developed as a set of helper scripts and it depends highly on an environment and many additional tools. For this reason at this moment it's not recommended for regular users. It's for developers. If you aren't one then search for already built apks in the internet.

Features
--------

- New items screen: Clean Design, Quick Access, Items Counters
- Support for HVGA and QVGA screens
- Muted version of the app - it's a lot smaller
- UI Tweaks:
    - Skip globe introduction animimation
    - Disable scanner zoom in animination - it's GPU consuming and delays portal dialog opening
    - Disable vectors to portals
    - Disable portal particles (XM "fountain" above portals)
    - Revert to old hack animation
- Real-time distance to portal on portal info and portal upgrade screens - it's good for deploying resonators close to 40m distance
- Number of keys for selected portal on info screen
- Option to hide unwanted menu tabs
- Option to deploy resonators from highest to lowest
- Fullscreen mode

Other features:

- Automatically identifies obfuscated names using declarative-style configuration
- Easily deploy multiple UI variants (e.g. different themes) within one apk file
- Simulator of UI components
- Texture unpacker - separate individual images from *.atlas and related png files

News
----

07.07.2013 - **v1.30.2-broot-1.0.0 has been released!**

- Initial Version


Quick Build Instructions
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
- apktool.jar v2.x
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

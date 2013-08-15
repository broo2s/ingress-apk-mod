#!/usr/bin/env python3

import shutil

from common import HOME, load_obj, edit_cls


def main():
    load_obj()
    shutil.rmtree('%s/app/assets/sounds' % HOME)

    edit = edit_cls('AudioSystemManager')
    edit.find_line(r' invoke-virtual \{[pv]\d+, [pv]\d+, [pv]\d+\}, Ljava/util/concurrent/atomic/AtomicBoolean;->compareAndSet\(ZZ\)Z')
    edit.find_prologue(where='up')
    edit.prepare_to_insert(2)
    edit.add_line(' return-void')
    edit.save()

    edit = edit_cls('Spectrograph')
    edit.prepare_after_prologue('draw')
    edit.add_line(' return-void')
    edit.save()
    
    edit = edit_cls('SpectrographSoundFinder')
    edit.find_line(r' invoke-direct \{p0\}, Ljava/lang/Object;->\<init\>\(\)V')
    edit.prepare_to_insert()
    edit.add_line(' return-void')
    edit.save()
    

if __name__ == '__main__':
    main()

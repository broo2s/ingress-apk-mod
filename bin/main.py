#!/usr/bin/env python3

from common import load_obj, edit_cls, expr, expr_type, expr_type_multi


def main():
    load_obj()

    edit = edit_cls('NemesisApplication')
    edit.prepare_after_prologue('onCreate')
    edit.add_invoke_entry('NemesisApp_onOnCreate', 'p0')

    edit.prepare_after_invoke_init('AssetFinder')
    edit.add_invoke_entry('AssetFinder_onInit', edit.vars[0])
    edit.save()

    edit = edit_cls('NemesisActivity')
    edit.prepare_after_prologue('onCreate')
    edit.add_invoke_entry('NemesisActivity_onOnCreate', 'p0')
    edit.prepare_after_prologue('onPause')
    edit.add_invoke_entry('NemesisActivity_onOnPause', 'p0')
    edit.prepare_after_prologue('onResume')
    edit.add_invoke_entry('NemesisActivity_onOnResume', 'p0')
    edit.save()

    edit = edit_cls('NemesisWorld')
    edit.find_line(r' new-array ([vp]\d+), ([vp]\d+), \[%s' % expr_type('$BaseSubActivity'))
    edit.find_line(r' return-void', where='down')
    edit.prepare_to_insert_before(True)
    edit.add_line(' move-object/from16 v0, p0')
    edit.add_invoke_entry('NemesisWorld_onInit', 'v0')

    edit.find_line(r' const-class (v\d+), %s' % expr_type('$Space2FaceActivity'))
    edit.prepare_to_insert_before()
    edit.add_invoke_entry('shouldSkipIntro')
    edit.add_line(' move-result %s' % edit.vars[0])
    edit.add_line(' if-nez %s, :mod_0' % edit.vars[0])
    edit.curr += 3
    edit.split_lines()
    edit.curr += 1
    edit.prepare_to_insert()
    edit.add_line(' :mod_0')

    edit.prepare_after_invoke_init('MenuControllerImpl')
    edit.add_invoke_entry('MenuController_onInit', edit.vars[0])
    edit.save()

    edit = edit_cls('SubActivityManager')
    edit.mod_field_def('skin', 'public')

    edit.prepare_after_prologue('init')
    edit.add_invoke_entry('SubActivityManager_onInit', 'p2')
    edit.save()

    edit = edit_cls('SubActivityApplicationListener')
    edit.find_method_def('create')
    edit.find_line(r' return-void', where='down')
    edit.prepare_to_insert_before(True)
    edit.add_invoke_entry('SubActiityApplicationLisener_onCreated')
    edit.save()

    edit = edit_cls('MenuTabId')
    edit.add_enum('MOD_ABOUT')
    edit.add_enum('MOD_ITEMS')

    edit.prepare_after_prologue('toString')
    edit.add_invoke_entry('MenuTabId_onToString', 'p0', 'v0')
    edit.add_ret_if_result(True, 'result')
    edit.save()

    edit = edit_cls('MenuTopWidget')
    edit.find_line(r' invoke-static {}, %s->values\(\)\[%s' % tuple(expr_type_multi('$MenuTabId', '$MenuTabId')))
    edit.comment_line()
    edit.add_invoke_entry('MenuTopWidget_getTabs')
    edit.save()

    edit = edit_cls('MenuControllerImpl')
    edit.prepare_after_prologue('selectTab')
    edit.add_invoke_entry('MenuControllerImpl_onSelectTab', 'p1')
    edit.add_line(' return-void')
    #edit.add_ret_if_result(True)
    edit.save()

    edit = edit_cls('MenuShowBtn')
    edit.find_line(' const-class (v\d+), %s' % expr_type('$ItemsActivity'))
    edit.comment_line()
    edit.add_invoke_entry('MenuShowBtn_onClick')
    edit.add_line(' move-result-object %s' % edit.vars[0])
    edit.save()

    edit = edit_cls('AssetFinder')
    edit.find_line(r' const-string/jumbo v\d+, "\{"')
    edit.find_prologue(where="up")
    edit.prepare_to_insert(2)
    edit.add_invoke_entry('AssetFinder_onGetAssetPath', 'p1', 'v0')
    edit.add_ret_if_result(True, 'result')

    edit.mod_field_def('screenDensity', 'public')
    edit.save()

    edit = edit_cls('GroupDrawer')
    edit.find_line(r' invoke-interface \{.+?\}, %s->a\(Lcom/badlogic/gdx/math/Matrix4;.+' % expr_type('$Drawer'))
    edit.find_prologue(where='up')
    edit.prepare_to_insert(2)
    edit.add_invoke_entry('shouldDrawScannerObject', '', 'v0')
    edit.add_ret_if_result(False)
    edit.save()

    edit = edit_cls('PortalInfoDialog')
    edit.mod_field_def('portalComponent', 'public')

    edit.find_line(r' .+"Owner: "')
    edit.find_line(
        r' invoke-virtual \{([pv]\d+), ([pv]\d+)\}, Lcom/a/a/c;->b\(Ljava/lang/Integer;\)L.+;',
        where='down')
    edit.comment_line()
    edit.find_line(
        r' invoke-virtual \{([pv]\d+)\}, Lcom/badlogic/gdx/scenes/scene2d/ui/Table;->row\(\)L.+;',
        where='down')
    edit.comment_line()
    edit.find_line(
        r' return-object ([pv]\d+)',
        where='down')
    tab = edit.vars[0]
    edit.prepare_to_insert_before()
    edit.add_invoke_entry('PortalInfoDialog_onStatsTableCreated', 'p0, %s' % tab)

    edit.prepare_after_prologue('onPlayerLocationChanged')
    edit.add_invoke_entry('PortalInfoDialog_onPlayerLocationChanged')

    edit.find_line(r' const.*? ([pv]\d+), 0x3f40')
    edit.prepare_to_insert()
    edit.add_invoke_entry('PortalInfoDialog_getOpenDelay', edit.vars[0], edit.vars[0])
    edit.save()


    edit = edit_cls('ScannerStateManager')
    edit.prepare_after_prologue('enablePortalVectors')
    edit.add_invoke_entry('ScannerStateManager_onEnablePortalVectors', '', 'v0')
    edit.add_ret_if_result(False)
    edit.save()


    edit = edit_cls('PlayerModelUtils')
    edit.find_method_def('getDefaultResonatorToDeploy')
    edit.find_line(' invoke-interface {(.+)}, Ljava/util/Map;->keySet\(\)Ljava/util/Set;', where='down')
    edit.prepare_to_insert_before(True)
    edit.add_invoke_entry('PlayerModelUtils_onGetDefaultResonatorToDeploy', edit.vars[0])
    edit.add_line(' move-result-object %s' % edit.vars[0])
    edit.save()


    edit = edit_cls('ZoomInMode')
    edit.find_method_def('onEnter')
    edit.find_line(r' iput-object [pv]\d+, p0, %s->g.+' % expr_type('$ZoomInMode'))
    edit.prepare_to_insert()
    edit.add_invoke_entry('ZoomInMode_shouldZoomIn', '', 'v0')
    edit.add_ret_if_result(False)
    edit.save()


    edit = edit_cls('PortalUpgradeActivity')
    edit.mod_field_def('portalEntity', 'public')
    edit.save()


    edit = edit_cls('PortalUpgradeUi')
    edit.mod_class_def('public')
    edit.mod_field_def('activity', 'public')

    edit.find_line(r' const-string.*, "PORTAL"')
    edit.find_line(r' invoke-virtual \{([pv]\d+), .*\}, Lcom/badlogic/gdx/scenes/scene2d/ui/Table;->add\(.*', where='down')
    tableReg = edit.vars[0]
    edit.find_line(r' invoke-virtual {.*, %s}, Lcom/badlogic/gdx/scenes/scene2d/ui/Table;->add\(.*' % tableReg, where='down')
    edit.prepare_to_insert_before()
    edit.add_invoke_entry('PortalUpgrade_onStatsTableCreated', 'p0, ' + tableReg)

    edit.prepare_after_prologue('dispose')
    edit.add_invoke_entry('PortalUpgrade_onDispose')
    edit.save()


    edit = edit_cls('ResonatorBrowser')
    edit.find_line(r' add-int/lit8 ([pv]\d+), ([pv]\d+), 0x1e')
    edit.comment_line()
    edit.prepare_to_insert()
    edit.add_invoke_entry('PortalUpgrade_getResonatorBrowserHeight', edit.vars[1], edit.vars[0])
    edit.save()


    edit = edit_cls('ClientFeatureKnobBundle')
    edit.find_line(r' iget-boolean v0, p0, %s' % expr('$ClientFeatureKnobBundle->enableNewHackAnimations'))
    edit.prepare_to_insert()
    edit.add_invoke_entry('ClientFeatureKnobBundle_getEnableNewHackAnimations', 'v0', 'v0')
    edit.save()

    #stop inventory item rotation
    edit = edit_cls('InventoryItemRenderer')
    edit.prepare_after_prologue('rotate')
    edit.add_invoke_entry('InventoryItemRenderer_shouldRotate', ret='v0')
    edit.add_ret_if_result(False, 'result')
    edit.save()

    #simplify inventory item rendering
    edit = edit_cls('InventoryItemRenderer')
    edit.find_line('.*Lcom/badlogic/gdx/graphics/glutils/ShaderProgram;->end.*')
    edit.prepare_to_insert()
    edit.add_invoke_entry('InventoryItemRenderer_simplifyItems', ret='v0')
    edit.add_line(' if-nez v0, :skip_item_shader')
    edit.find_line('.*Lcom/badlogic/gdx/graphics/glutils/ShaderProgram;->end.*', where='down')
    edit.prepare_to_insert()
    edit.add_line(' :skip_item_shader')
    edit.save()

    #modify shader code before compiling it
    edit = edit_cls('ShaderUtils')
    edit.find_line(r' new-instance ([pv]\d+), Lcom/badlogic/gdx/graphics/glutils/ShaderProgram;')
    shaderReg = edit.vars[0]
    edit.comment_line()
    edit.find_line(r' invoke-direct \{.*\}, Lcom/badlogic/gdx/graphics/glutils/ShaderProgram;-><init>\(Ljava/lang/String;Ljava/lang/String;\)V', where='down')
    edit.comment_line()
    edit.prepare_to_insert()
    edit.add_invoke_entry('ShaderUtils_compileShader', 'p0, p1, p2', shaderReg)
    edit.save()

    edit = edit_cls('CommsAdapter')
    edit.prepare_after_prologue('bindView')
    edit.find_line(r' iget-object v3, p0, %s->l:%s' % (expr('$CommsAdapter'), expr('$SimpleDateFormat')))
    edit.comment_line()
    edit.add_invoke_entry('CommsAdapter_getDateFormat', '', 'v3')
    edit.save()

    #remove recycle animation
    edit = edit_cls('ItemActionHandler')
    edit.find_method_def('recycle')
    edit.find_line(' \.locals 4', where='down')
    edit.replace_in_line('4', '5')
    edit.find_line(' const-wide/16 v2, 0x4b0', where='down')
    edit.prepare_to_insert()
    edit.add_invoke_entry('ItemActionHandler_recycleAnimationsEnabled', ret='v4')
    edit.add_line(' if-nez v4, :lbl_recycle_delay');
    edit.add_line(' const-wide/16 v2, 0x0')
    edit.add_line(' :lbl_recycle_delay')
    edit.save()

    # disable vibration
    edit = edit_cls('AndroidInput')
    edit.find_method_def('vibrateInt')
    edit.find_line(' \.locals 3', where='down')
    edit.replace_in_line('3', '4')
    edit.find_line('.*invoke-virtual \{.*\}, Landroid/os/Vibrator;->vibrate\(J\)V.*', where='down')
    edit.prepare_to_insert_before()
    edit.add_invoke_entry('vibrationEnabled', ret='v3')
    edit.add_line(' if-eqz v3, :lbl_vibration_disabled')
    edit.curr += 2;
    edit.add_line(' :lbl_vibration_disabled')
    edit.save()

    #change order of buttons in round menu
    edit = edit_cls('ScannerTouchHandler')
    edit.find_line(' invoke-direct/range \{v0 \.\. v7\}, (.+)$')
    edit.prepare_to_insert_before()

    edit.add_invoke_entry('ScannerTouchHandler_shouldSwapTouchMenuButtons', ret='v11')
    edit.add_line(' if-eqz v11, :noswap')
    
    edit.add_line(' move-object v11, v3')
    edit.add_line(' move-object v3, v6')
    edit.add_line(' move-object v6, v11')

    edit.add_line(' :noswap')
    edit.save()

if __name__ == '__main__':
    main()

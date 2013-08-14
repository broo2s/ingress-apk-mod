package broot.ingress.mod.util;

import broot.ingress.mod.AboutModActivity;
import broot.ingress.mod.ModItemsActivity;
import com.nianticproject.ingress.common.device.DeviceActivity;
import com.nianticproject.ingress.common.intel.IntelActivity;
import com.nianticproject.ingress.common.inventory.ItemsActivity;
import com.nianticproject.ingress.common.agent.AgentActivity;
import com.nianticproject.ingress.common.mission.MissionListActivity;
import com.nianticproject.ingress.common.recruit.RecruitActivity;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;

import java.util.ArrayList;
import java.util.List;

public class MenuUtils {

    public static MenuTabId[] getTabs() {
        List<MenuTabId> tabs = new ArrayList<MenuTabId>();
        if (Config.itemsTab == Config.ItemsTab.AT_START) {
            tabs.add(MenuTabId.MOD_ITEMS);
        }
        if (Config.showOrigItemsTab) {
            tabs.add(MenuTabId.INVENTORY);
        }

            tabs.add(MenuTabId.AGENT);

        if (Config.showIntelTab) {
            tabs.add(MenuTabId.INTEL);
        }
        if (Config.showMissionTab) {
            tabs.add(MenuTabId.MISSIONS);
        }
        if (Config.showRecruitTab) {
            tabs.add(MenuTabId.RECRUIT);
        }
        if (Config.showDeviceTab) {
            tabs.add(MenuTabId.DEVICE);
        }
        if (Config.itemsTab == Config.ItemsTab.AT_END) {
            tabs.add(MenuTabId.MOD_ITEMS);
        }
        tabs.add(MenuTabId.MOD_ABOUT);
        return tabs.toArray(new MenuTabId[tabs.size()]);
    }

    public static Class<?> getActivityClassForMenuTabId(MenuTabId tab) {
        switch (tab) {
            case MOD_ITEMS:
                return ModItemsActivity.class;
            case MOD_ABOUT:
                return AboutModActivity.class;
            case INVENTORY:
                return ItemsActivity.class;
            case AGENT:
                return AgentActivity.class;
            case INTEL:
                return IntelActivity.class;
            case MISSIONS:
                return MissionListActivity.class;
            case RECRUIT:
                return RecruitActivity.class;
            case DEVICE:
                return DeviceActivity.class;
            default:
                throw new RuntimeException();
        }
    }
}

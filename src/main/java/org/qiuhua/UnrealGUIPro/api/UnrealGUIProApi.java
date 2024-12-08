package org.qiuhua.UnrealGUIPro.api;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import org.bukkit.entity.Player;
import org.qiuhua.UnrealGUIPro.gui.GUIController;
import org.qiuhua.UnrealGUIPro.gui.UnrealGUIContainer;

public class UnrealGUIProApi {
    //打开一个界面
    public static void open(Player player, String guiName){
        GUIController.open(player, guiName);
    }

    public static UnrealGUIContainer getUnrealGUIContainer(Player player, String guiName){
        if(GUIController.getUnrealCoreGUIMap().containsKey(guiName)){
            return new UnrealGUIContainer(guiName, GUIController.getUnrealCoreGUIMap().get(guiName), player);
        }
        return null;
    }

}

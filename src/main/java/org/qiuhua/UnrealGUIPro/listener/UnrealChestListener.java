package org.qiuhua.UnrealGUIPro.listener;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.communication.event.PlayerConnectionSuccessfulEvent;
import com.daxton.unrealcore.display.content.gui.UnrealCoreGUI;
import com.daxton.unrealcore.display.event.gui.PlayerGUICloseEvent;
import com.daxton.unrealcore.display.event.gui.PlayerOpenChestEvent;
import me.arasple.mc.trmenu.module.display.MenuSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.qiuhua.UnrealGUIPro.Main;
import org.qiuhua.UnrealGUIPro.Tool;
import org.qiuhua.UnrealGUIPro.gui.GUIController;
import org.qiuhua.UnrealGUIPro.tooltip.ToolTipController;
import org.qiuhua.UnrealGUIPro.trmenu.TrMenuUtil;
import org.qiuhua.UnrealGUIPro.trmenu.TrPlayer;

public class UnrealChestListener implements Listener {

    @EventHandler
    public void onChestOpen(PlayerOpenChestEvent event){
        Player player = event.getPlayer();
        String uuidString = player.getUniqueId().toString();
        String title = event.getTitle();
        if(Main.getIsTrmenu()){
            boolean trMenu = false;
            if(TrMenuUtil.trMenuOpen.containsKey(uuidString)){
                TrPlayer trPlayer = TrMenuUtil.trMenuOpen.get(uuidString);
                if(trPlayer.getFirst()){
                    trPlayer.setFirst(false);
                    trPlayer.setTrMenu(true);
                    return;
                }
                if(trPlayer.getTrMenu()){
                    trMenu = true;
                    trPlayer.setTrMenu(false);
                }
            }
            GUIController.openChest(player, title, trMenu);
            return;
        }
        GUIController.openChest(player, title);
    }

    @EventHandler//當玩家關閉GUI
    public void onGUIClose(PlayerGUICloseEvent event) {
        Player player = event.getPlayer();
        String uuidString = player.getUniqueId().toString();
        String guiName = event.getGuiName();
        UnrealCoreGUI unrealCoreGUI = UnrealCoreAPI.inst(player).getGUIHelper().getUnrealCoreGUI();
        if(unrealCoreGUI != null){
            String dataGUIName = unrealCoreGUI.getGUIName();
            if(!guiName.equals(dataGUIName)){
                return;
            }
            if(Main.getIsTrmenu()){
                if(TrMenuUtil.trMenuOpen.containsKey(uuidString)){
                    TrPlayer trPlayer = TrMenuUtil.trMenuOpen.get(uuidString);
                    if(!trPlayer.getFirst()){
                        MenuSession.Companion.getSession(event.getPlayer()).close(true,true);
                        TrMenuUtil.trMenuOpen.remove(uuidString);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerConnectionSuccessfulEvent event){
        Player player = event.getPlayer();
        Tool.setChest(player);
        ToolTipController.setTopModuleCache(player);
    }
}

package org.qiuhua.UnrealGUIPro.listener;

import me.arasple.mc.trmenu.api.event.MenuOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.qiuhua.UnrealGUIPro.trmenu.TrMenuUtil;
import org.qiuhua.UnrealGUIPro.trmenu.TrPlayer;

public class TrMenuListener implements Listener {

    @EventHandler //當玩家打開TrMenuGUI
    public void openGUI(MenuOpenEvent event){
        Player player = event.getSession().getAgent();
        String uuidString = player.getUniqueId().toString();
        TrPlayer trPlayer = new TrPlayer();
        TrMenuUtil.trMenuOpen.put(uuidString, trPlayer);
    }


}

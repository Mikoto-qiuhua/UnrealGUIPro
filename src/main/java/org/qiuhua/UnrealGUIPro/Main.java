package org.qiuhua.UnrealGUIPro;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.qiuhua.UnrealGUIPro.command.UnrealGuiProCommand;
import org.qiuhua.UnrealGUIPro.gui.GUIController;
import org.qiuhua.UnrealGUIPro.gui.UnrealGUIContainer;
import org.qiuhua.UnrealGUIPro.listener.TrMenuListener;
import org.qiuhua.UnrealGUIPro.listener.UnrealChestListener;
import org.qiuhua.UnrealGUIPro.tooltip.ToolTipController;

public class Main extends JavaPlugin {
    private static Main mainPlugin;
    public static Main getMainPlugin(){
        return mainPlugin;
    }

    public static Boolean getIsTrmenu(){
        return isTrmenu;
    }

    public static Boolean getIsKetherFactory(){
        return isKetherFactory;
    }
    private static Boolean isTrmenu = false;

    private static Boolean isKetherFactory = false;
    //启动时运行
    @Override
    public void onEnable(){
        mainPlugin = this;
        new UnrealGuiProCommand().register();
        Tool.saveAllConfig();
        Tool.reload();
        GUIController.loadGUIFiles(GUIController.getFolder());
        ToolTipController.loadToolTipFiles(ToolTipController.getFolder());
        Bukkit.getPluginManager().registerEvents(new UnrealChestListener(), this);
        if(Bukkit.getServer().getPluginManager().getPlugin("TrMenu") != null){
            Bukkit.getPluginManager().registerEvents(new TrMenuListener(), this);
            isTrmenu = true;
        }
        if(Bukkit.getServer().getPluginManager().getPlugin("KetherFactory") != null){
            this.getLogger().info("您已安装[KetherFactory]解锁Kether功能,并且条件修改为Kether语法");
            isKetherFactory = true;
        }
    }



    //关闭时运行
    @Override
    public void onDisable(){
    }

    //执行重载命令时运行
    @Override
    public void reloadConfig(){
        Tool.saveAllConfig();
        Tool.reload();
        GUIController.getUnrealCoreGUIMap().clear();
        GUIController.loadGUIFiles(GUIController.getFolder());
        ToolTipController.getModuleDataMap().clear();
        ToolTipController.loadToolTipFiles(ToolTipController.getFolder());
    }


}
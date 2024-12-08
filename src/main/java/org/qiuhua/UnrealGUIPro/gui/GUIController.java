package org.qiuhua.UnrealGUIPro.gui;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.been.display.type.SystemGUIType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.qiuhua.UnrealGUIPro.Main;
import org.qiuhua.UnrealGUIPro.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class GUIController {
    //GUI列表
    public static final Map<String, FileConfiguration> unrealCoreGUIMap = new HashMap<>();
    private static final File folder = new File(Main.getMainPlugin().getDataFolder(), "gui");

    //打开了界面的玩家
    private static final ConcurrentHashMap<UUID, UnrealGUIContainer> allOpenPlayer = new ConcurrentHashMap<>();

    public static void removeUnrealGUIContainer(Player player){
        allOpenPlayer.remove(player.getUniqueId());
    }
    public static UnrealGUIContainer getUnrealGUIContainer(Player player){
        UUID uuid = player.getUniqueId();
        if(allOpenPlayer.containsKey(uuid)){
            return allOpenPlayer.get(uuid);
        }
        return null;
    }

    public static File getFolder(){
        return folder;
    }

    //加载gui的全部配置文件
    public static void loadGUIFiles(File folder){
        // 检查文件夹是否存在且是一个文件夹
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            // 遍历文件夹中的所有文件和文件夹
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 如果是文件夹，则递归调用该方法，传入文件夹作为参数
                        loadGUIFiles(file);
                    } else if (file.isFile() && file.getName().endsWith(".yml")) {
                        // 检查文件是否是一个文件且以 .yml 结尾
                        // 加载文件的配置
                        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                        String fileName = file.getName().replace(".yml", "");
                        // 将加载的配置存入 Map 中，键为文件名（去除 .yml 后缀），值为配置对象
                        unrealCoreGUIMap.put(fileName, config);
                    }
                }
            }
        }
    }


    //返回unrealCoreGUIMap
    public static Map<String, FileConfiguration> getUnrealCoreGUIMap() {
        return unrealCoreGUIMap;
    }


    //打开一个界面
    public static void open(Player player, String guiName){
        SystemGUIType systemGUIType = SystemGUIType.convert(guiName);
        if(systemGUIType != SystemGUIType.None){
            UnrealCoreAPI.inst(player).getGUIHelper().openSystemGUI(systemGUIType);
            return;
        }
        if(unrealCoreGUIMap.containsKey(guiName)){
            UnrealGUIContainer unrealGUIContainer = new UnrealGUIContainer(guiName, unrealCoreGUIMap.get(guiName), player);
            UnrealCoreAPI.inst(player).getGUIHelper().openCoreGUI(unrealGUIContainer);
            allOpenPlayer.put(player.getUniqueId(), unrealGUIContainer);
        }
    }


    //打開GUI
    public static void open(Player player, String guiName, Boolean trMenu){
        if(unrealCoreGUIMap.containsKey(guiName)){
            UnrealGUIContainer unrealGUIContainer = new UnrealGUIContainer(guiName, unrealCoreGUIMap.get(guiName), player);
            unrealGUIContainer.setTrMenu(trMenu);
            UnrealCoreAPI.inst(player).getGUIHelper().openCoreGUI(unrealGUIContainer);
            allOpenPlayer.put(player.getUniqueId(), unrealGUIContainer);
        }
    }



    //以箱子名稱打開GUI
    public static void openChest(Player player, String title, boolean trMenu){
        title = Tool.toPatternName(new ArrayList<>(Tool.chestMap.keySet()), title);
        if(Tool.chestMap.containsKey(title)){
            String guiName = Tool.chestMap.get(title);
            open(player, guiName, trMenu);
        }
    }


    public static void openChest(Player player, String title){
        title = Tool.toPatternName(new ArrayList<>(Tool.chestMap.keySet()), title);
        if(Tool.chestMap.containsKey(title)){
            String guiName = Tool.chestMap.get(title);
            open(player, guiName);
        }
    }


}

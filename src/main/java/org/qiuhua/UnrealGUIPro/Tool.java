package org.qiuhua.UnrealGUIPro;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.display.been.module.ModuleData;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool {

    //加载默认config
    private static FileConfiguration config;
    //重新加载
    public static void reload() {
        removeChest();
        config = load(new File(Main.getMainPlugin().getDataFolder (),"config.yml"));
        //获取chest节点
        chestMap.clear();
        ConfigurationSection chestKey = config.getConfigurationSection("Chest");
        if(chestKey != null){
            for(String key : chestKey.getKeys(false)){
                String value = config.getString("Chest." + key);
                chestMap.put(key,value);
            }
        }
        setChest();
    }

    public static FileConfiguration getConfig(){
        return config;
    }


    //箱子覆盖的列表
    public static Map<String, String> chestMap = new ConcurrentHashMap<>();


    //创建配置文件
    public static void saveAllConfig(){
        //创建一个插件文件夹路径为基础的 并追加下一层。所以此时的文件应该是Config.yml
        //exists 代表是否存在
        if (!(new File(Main.getMainPlugin().getDataFolder() ,"config.yml").exists())){
            Main.getMainPlugin().saveResource("config.yml", false);
        }
        if (!(new File (Main.getMainPlugin().getDataFolder() ,"gui").exists())){
            Main.getMainPlugin().saveResource("gui/Backpack.yml", false);
            Main.getMainPlugin().saveResource("gui/ChatDefault.yml", false);
            Main.getMainPlugin().saveResource("gui/ChatTalk.yml", false);
            Main.getMainPlugin().saveResource("gui/EBackpack.yml", false);
            Main.getMainPlugin().saveResource("gui/Esc.yml", false);
            Main.getMainPlugin().saveResource("gui/GUIExample.yml", false);
            Main.getMainPlugin().saveResource("gui/自定义目录/界面额外配置.yml", false);
            Main.getMainPlugin().saveResource("gui/自定义目录/组件功能.yml", false);
            Main.getMainPlugin().saveResource("gui/自定义目录/额外占位符.yml", false);
        }
        if (!(new File (Main.getMainPlugin().getDataFolder() ,"tooltip").exists())){
            Main.getMainPlugin().saveResource("tooltip/自定义目录/item.yml", false);
            Main.getMainPlugin().saveResource("tooltip/自定义目录/组件临时tip.yml", false);
            Main.getMainPlugin().saveResource("tooltip/text.yml", false);
        }
    }


    public static YamlConfiguration load (File file)
    {
        return YamlConfiguration.loadConfiguration(file);
    }

    // 使用 PAPI 替换占位符
    public static String getPapiString(Player player, String string){
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            return PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

    public static List<String> getPapiList(Player player, List<String> list){
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            List<String>  newList = new ArrayList<>();
            list.forEach((e) -> {
                newList.add(PlaceholderAPI.setPlaceholders(player, e));
            });
            return newList;
        }
        return list;
    }


    //去除颜色代码
    public static String removeColorCode(String str){
        Pattern pattern = Pattern.compile("§[0-9a-fklmnor]");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }


    //把模塊列表轉成ID列表
    public static List<String> moduleIDList(List<ModuleData> moduleDataList){
        List<String> stringList = new ArrayList<>();
        moduleDataList.forEach(moduleData -> stringList.add(moduleData.getModuleID()));
        return stringList;
    }




    //他这里 不知道为什么给玩家发送了这些箱子设置  可能全部都是客户端判断
    //設置要改變的箱子(指定玩家)
    public static void setChest(Player player){
        chestMap.forEach((title, guiName) -> {
            UnrealCoreAPI.setChest(player, title);
        });
    }

    //設置要改變的箱子(所有線上玩家)
    public static void setChest(){
        Bukkit.getOnlinePlayers().forEach(Tool::setChest);
    }

    //移除所有箱子設置(所有線上玩家)
    public static void removeChest(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            chestMap.forEach((title, guiName) -> {
                UnrealCoreAPI.removeChest(player, title);
            });
        });
    }

    //正则对比
    public static String toPatternName(List<String> list ,String text){
        for(String regex : list){
            Pattern pattern = Pattern.compile(regex);
            if(pattern.matcher(text).matches()){
                return regex;
            }
        }
        return text;
    }

}

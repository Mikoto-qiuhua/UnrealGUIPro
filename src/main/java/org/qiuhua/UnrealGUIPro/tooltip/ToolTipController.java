package org.qiuhua.UnrealGUIPro.tooltip;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.display.been.module.ModuleData;
import com.daxton.unrealcore.display.content.module.ModuleComponents;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.qiuhua.UnrealGUIPro.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolTipController {
    //Top模塊
    public static final Map<String, List<ModuleData>> moduleDataMap = new HashMap<>();

    private static final File folder = new File(Main.getMainPlugin().getDataFolder(), "tooltip");

    public static File getFolder(){
        return folder;
    }

    //加载ToolTip的全部配置文件
    public static void loadToolTipFiles(File folder){
        // 检查文件夹是否存在且是一个文件夹
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            // 遍历文件夹中的所有文件
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 如果是文件夹，则递归调用该方法，传入文件夹作为参数
                        loadToolTipFiles(file);
                    }else if (file.isFile() && file.getName().endsWith(".yml")) {
                        // 检查文件是否是一个文件且以 .yml 结尾
                        // 加载文件的配置
                        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                        String fileName = file.getName().replace(".yml", "");
                        List<ModuleData> moduleDataList = UnrealCoreAPI.inst().getGUIHelper().getTopModuleList("", config);
                        moduleDataMap.put(fileName, moduleDataList);
//                        Main.getMainPlugin().getLogger().info("加载tip " + fileName);
                    }
                }
            }
        }
        if(!moduleDataMap.isEmpty()){
            Bukkit.getOnlinePlayers().forEach(ToolTipController::setTopModuleCache);
        }
    }

    public static Map<String, List<ModuleData>> getModuleDataMap(){
        return moduleDataMap;
    }

    //設置置頂模塊緩存
    public static void setTopModuleCache(Player player){
        moduleDataMap.forEach((key, moduleDataList) -> {
            UnrealCoreAPI.inst(player).getGUIHelper().addTopModuleCache(key, moduleDataList);

        });
    }


    public static List<ModuleData> getModuleTipModule(FileConfiguration fileConfig, ModuleComponents moduleComponents,String tipName){
        String url = moduleComponents.getFilePath() + ".Event.Select.tip.add";
        List<ModuleData> addModuleDataList = UnrealCoreAPI.inst().getGUIHelper().getTopModuleList(url, fileConfig);
        List<ModuleData> tipData = new ArrayList<>();
        //如果有就拿出来用
        if(moduleDataMap.containsKey(tipName)){
            tipData.addAll(moduleDataMap.get(tipName));
        }
        tipData.addAll(addModuleDataList);
        return tipData;
    }






}

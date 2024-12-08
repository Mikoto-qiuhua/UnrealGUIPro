package org.qiuhua.UnrealGUIPro.application;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import com.daxton.unrealcore.been.common.been.SlotClickBeen;
import com.daxton.unrealcore.been.display.type.ClickSlotType;
import com.daxton.unrealcore.been.placeholder.been.TransitionBeen;
import com.daxton.unrealcore.display.been.module.ModuleData;
import com.daxton.unrealcore.display.been.module.control.ContainerModuleData;
import com.daxton.unrealcore.display.been.module.control.RangeModuleData;
import com.daxton.unrealcore.display.been.module.control.SelectModuleData;
import com.daxton.unrealcore.display.been.module.display.VideoModuleData;
import com.daxton.unrealcore.display.been.module.input.AreaInputModuleData;
import com.daxton.unrealcore.display.been.module.input.InputModuleData;
import me.arasple.mc.trmenu.module.display.MenuSession;
import me.mkbaka.ketherfactory.KetherFactory;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.qiuhua.UnrealGUIPro.Main;
import org.qiuhua.UnrealGUIPro.Tool;
import org.qiuhua.UnrealGUIPro.gui.GUIController;
import org.qiuhua.UnrealGUIPro.gui.UnrealGUIContainer;
import org.qiuhua.UnrealGUIPro.videoPlay.VideoPlay;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerFunction {

    public static CompletableFuture<?> parseKether(String script, Player player, Map<String , Objects> map){
        return KetherFactory.eval(script, player, map);
    }


    public static void action(Player player, ConfigurationSection actionSection, Map<String, ModuleData> moduleDataMap){
        //如果装了那个Kether的扩展 那就执行kt动作
        if(Main.getIsKetherFactory()){
           String script = actionSection.getString("ktAction");
           if(script != null && !script.equals("")){
               script = PlayerFunction.parsePlaceholder(moduleDataMap, script);
               parseKether(script, player, new HashMap<>());
           }
        }
        List<String> list = actionSection.getStringList("action");
        for(String action: list) {
            action = Tool.getPapiString(player, action);
            String[] a = action.split("]:");
            if (a.length >= 2) {
                String type = a[0];
                String content = a[1].trim();
                String finalContent = PlayerFunction.parsePlaceholder(moduleDataMap, content);
                switch (type) {
                    case "[console":
                        SchedulerFunction.run(Main.getMainPlugin(), ()->{
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalContent);
                        });
                        break;
                    case "[op":
                        SchedulerFunction.run(Main.getMainPlugin(), ()->{
                            boolean originalOpStatus = player.isOp();
                            player.setOp(true);
                            player.performCommand(finalContent);
                            player.setOp(originalOpStatus);
                        });
                        break;
                    case "[tell":
                        player.sendMessage(finalContent);
                        break;
                    case "[player":
                        SchedulerFunction.run(Main.getMainPlugin(), ()->{
                            player.performCommand(finalContent);
                        });
                        break;
                    case "[chat":
                        SchedulerFunction.run(Main.getMainPlugin(), ()->{
                            player.chat(finalContent);
                        });
                        break;
                    case "[effects":
                        String[] effects = finalContent.split(",");
                        Particle particleType = Particle.valueOf(effects[0]);
                        int particleCount = Integer.parseInt(effects[1]);
                        double particleSpeed = Double.parseDouble(effects[2]);
                        double x = Double.parseDouble(effects[3]);
                        double y = Double.parseDouble(effects[4]);
                        double z = Double.parseDouble(effects[5]);
                        player.spawnParticle(particleType, player.getLocation().clone().add(0, 0.5, 0), particleCount, x, y, z, particleSpeed);
                        break;
                    case "[sound":
                        String[] sound = finalContent.split(",");
                        Sound soundType = Sound.valueOf(sound[0]);
                        float volume = Float.parseFloat(sound[2]); // 音量
                        float pitch = Float.parseFloat(sound[1]); // 音调
                        player.playSound(player.getLocation(), soundType, volume, pitch);
                        break;
                    case "[transitionValue":
                        setTransition(player, finalContent);
                        break;
                    case "[transitionValueRemove":
                        removeTransition(player, finalContent);
                    case "[toGui":
                        GUIController.open(player, finalContent);
                        break;
                    case "[close":
                        if(finalContent.equals("self")){
                            UnrealCoreAPI.inst(player).getGUIHelper().closeCoreGUI();
                        }else{
                            UnrealGUIContainer unrealGUIContainer = GUIController.getUnrealGUIContainer(player);
                            if(unrealGUIContainer != null){
                                unrealGUIContainer.delGui(finalContent);
                            }
                        }
                        break;
                    case "[openUrl":
                        UnrealCoreAPI.inst(player).getCommonHelper().openURL(finalContent);
                        break;
                    case "[video":
                        String[] video = content.split(",");
                        String videoId = video[0]; //组件id
                        String playType = video[1]; //播放类型
                        VideoModuleData videoModuleData = VideoPlay.parseVideoPlay(moduleDataMap, videoId);//组件对象
                        if (videoModuleData != null) {
                            VideoPlay.builder(player, videoModuleData, playType);
                        }
                    case "[slotClick":
                        int slot;
                        try {
                            slot = Integer.parseInt(finalContent);
                            // 如果转换成功，继续执行需要的逻辑
                        } catch (NumberFormatException e) {
                            // 处理转换失败的情况
                            break;
                            // 或者执行其他备用逻辑
                        }

                        UnrealGUIContainer unrealGUIContainer = GUIController.getUnrealGUIContainer(player);
                        int containerId = player.getOpenInventory().getTopInventory().hashCode();
                        if(unrealGUIContainer != null){
                            containerId = unrealGUIContainer.getTrContainerId();
                        }
                        SlotClickBeen slotClickBeen = new SlotClickBeen(containerId, slot, 0, ClickSlotType.PICKUP);
                        UnrealCoreAPI.inst(player).getCommonHelper().slotClick(slotClickBeen);




                }

            }

        }
    }


    //事件模块读取
    public static void eventOpen(Player player, ConfigurationSection section, Map<String, ModuleData> moduleDataMap){
        ConfigurationSection actionSection = section.getConfigurationSection("Event.OpenAction");
        if(actionSection != null){
            SchedulerFunction.runAsync(Main.getMainPlugin(),()->{
                PlayerFunction.action(player, actionSection, moduleDataMap);
            });
        }
    }
    public static void eventClose(Player player, ConfigurationSection section, Map<String, ModuleData> moduleDataMap){
        ConfigurationSection actionSection = section.getConfigurationSection("Event.CloseAction");
        if(actionSection != null){
            SchedulerFunction.runAsync(Main.getMainPlugin(),()->{
                PlayerFunction.action(player, actionSection, moduleDataMap);
            });
        }
    }







    //设置动画占位符
    public static void setTransition(Player player, String str){
        String[] parts = str.split(",");
        String key = parts[0];
        //是否是递减的
        boolean decreasing = false;
        //初始值
        double minValue = Double.parseDouble(parts[1]);
        //最大值
        double maxValue = Double.parseDouble(parts[2]);
        //过度时间
        long timeInterval = Long.parseLong(parts[3]);
        if (minValue > maxValue){
            decreasing = true;
        }
        //设置动画占位符
        TransitionBeen transitionBeen = new TransitionBeen(key, minValue, maxValue, timeInterval, decreasing);
        UnrealCoreAPI.inst(player).getPlaceholderHelper().transitionValueSet(key, transitionBeen);

    }

    //移除动画占位符
    public static void removeTransition(Player player, String str){
        String[] parts = str.split(",");
        for(String id : parts){
            UnrealCoreAPI.inst(player).getPlaceholderHelper().transitionValueRemove(id);
        }
    }

    //占位符解析
    public static String parsePlaceholder(Map<String, ModuleData> allModuleData, String str){
        // 使用正则表达式匹配<>中的内容
        Pattern pattern = Pattern.compile("<([^<>]*)>");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()) {
            String content = matcher.group(1); // 获取<>中的内容
            String[] parts = content.split("\\."); // 使用"."进行分割
            if (parts.length >= 3) {
                String type = parts[0]; //占位符类型
                String id = parts[1]; //组件id
                String defaultValue;
                int index;
                switch (type){
                    case "Input":
                        defaultValue = parts[2];//默认值
                        String inputStr = parseInput(allModuleData, id);
                        if(inputStr == null || inputStr.equals("")){
                            str = str.replace("<" + content + ">", defaultValue);
                        }else {
                            str = str.replace("<" + content + ">", inputStr);
                        }
                        break;
                    case "AreaInput":
                        index = Integer.parseInt(parts[2]);
                        String areaInputStr = parseAreaInput(allModuleData, id, index);
                        defaultValue = parts[3];//默认值
                        if(areaInputStr == null || areaInputStr.equals("")){
                            str = str.replace("<" + content + ">", defaultValue);
                        }else {
                            str = str.replace("<" + content + ">", areaInputStr);
                        }
                        break;
                    case "Select":
                        defaultValue = parts[2];//默认值
                        String selectStr = parseSelect(allModuleData, id);
                        if(selectStr == null || selectStr.equals("")){
                            str = str.replace("<" + content + ">", defaultValue);
                        }else {
                            str = str.replace("<" + content + ">", selectStr);
                        }
                        break;
                    case "Range":
                        defaultValue = parts[2];//默认值
                        String rangeStr = parseRange(allModuleData, id);
                        if(rangeStr == null || rangeStr.equals("")){
                            str = str.replace("<" + content + ">", defaultValue);
                        }else {
                            str = str.replace("<" + content + ">", rangeStr);
                        }
                        break;
                }
            }
        }
        return str;
    }







    //范围选择器捕获
    public static String parseRange(Map<String, ModuleData> allModuleData, String afterDot){
        //包含-符号时进行分割
        if (afterDot.contains("-")){
            String[] subParts = afterDot.split("-"); // 使用"-"进行分割
            ContainerModuleData containerModule = null;
            ModuleData moduleData = null;
            for(String modelId : subParts){
                //这里肯定是第一次循环执行的在主要的data内拿
                if(containerModule == null){
                    containerModule = (ContainerModuleData) allModuleData.get(modelId);
                    continue;
                }
                //这里开始是第二次循环后的操作
                if(moduleData == null){
                    moduleData = containerModule.getModuleDataMap().get(modelId);
                }else{
                    ContainerModuleData a =  (ContainerModuleData) moduleData;
                    moduleData = a.getModuleDataMap().get(modelId);
                }
                //如果他是一个选项的话
                if(moduleData instanceof RangeModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
                    return module.getOptionChoose();
                }
            }
        }

        ModuleData moduleData = allModuleData.get(afterDot);
        //如果他是一个选项的话
        if(moduleData instanceof RangeModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
            return module.getOptionChoose();
        }
        return null;

    }


    //选项捕获
    public static String parseSelect(Map<String, ModuleData> allModuleData, String afterDot){
        //包含-符号时进行分割
        if (afterDot.contains("-")){
            String[] subParts = afterDot.split("-"); // 使用"-"进行分割
            ContainerModuleData containerModule = null;
            ModuleData moduleData = null;
            for(String modelId : subParts){
                //这里肯定是第一次循环执行的在主要的data内拿
                if(containerModule == null){
                    containerModule = (ContainerModuleData) allModuleData.get(modelId);
                    continue;
                }
                //这里开始是第二次循环后的操作
                if(moduleData == null){
                    moduleData = containerModule.getModuleDataMap().get(modelId);
                }else{
                    ContainerModuleData a =  (ContainerModuleData) moduleData;
                    moduleData = a.getModuleDataMap().get(modelId);
                }
                //如果他是一个选项的话
                if(moduleData instanceof SelectModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
                    return module.getOptionChoose();
                }
            }
        }

        ModuleData moduleData = allModuleData.get(afterDot);
        //如果他是一个选项的话
        if(moduleData instanceof SelectModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
            return module.getOptionChoose();
        }
        return null;

    }



    //解析多行输入
    public static String parseAreaInput(Map<String, ModuleData> allModuleData, String afterDot, Integer index){
        //包含-符号时进行分割
        if (afterDot.contains("-")){
            String[] subParts = afterDot.split("-"); // 使用"-"进行分割
            ContainerModuleData containerModule = null;
            ModuleData moduleData = null;
            for(String modelId : subParts){
                //这里肯定是第一次循环执行的在主要的data内拿
                if(containerModule == null){
                    containerModule = (ContainerModuleData) allModuleData.get(modelId);
                    continue;
                }
                //这里开始是第二次循环后的操作
                if(moduleData == null){
                    moduleData = containerModule.getModuleDataMap().get(modelId);
                }else{
                    ContainerModuleData a =  (ContainerModuleData) moduleData;
                    moduleData = a.getModuleDataMap().get(modelId);
                }
                //如果他是一个多行输入框的话
                if(moduleData instanceof AreaInputModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
                    List<String> list = module.getText();
                    if(list.size() > index){
                        return list.get(index);
                    }else {
                        return null;
                    }
                }
            }
        }
        ModuleData moduleData = allModuleData.get(afterDot);
        if(moduleData instanceof AreaInputModuleData module){
            List<String> list = module.getText();
            if(list.size() > index){
//                    Main.getMainPlugin().getLogger().info(list.get(index));
                return list.get(index);
            }
        }
        return null;
    }




    //解析输入框内容
    public static String parseInput(Map<String, ModuleData> allModuleData, String afterDot){
        //包含-符号时进行分割
        if (afterDot.contains("-")){
            String[] subParts = afterDot.split("-"); // 使用"-"进行分割
            ContainerModuleData containerModule = null;
            ModuleData moduleData = null;
            for(String modelId : subParts){
                //这里肯定是第一次循环执行的在主要的data内拿
                if(containerModule == null){
                    containerModule = (ContainerModuleData) allModuleData.get(modelId);
                    continue;
                }
                //这里开始是第二次循环后的操作
                if(moduleData == null){
                    moduleData = containerModule.getModuleDataMap().get(modelId);
                }else{
                    ContainerModuleData a =  (ContainerModuleData) moduleData;
                    moduleData = a.getModuleDataMap().get(modelId);
                }
                //如果他是一个输入框的话
                if(moduleData instanceof InputModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
                    return module.getText();
                }
            }
        }

        ModuleData moduleData = allModuleData.get(afterDot);
        //如果他是一个输入框的话
        if(moduleData instanceof InputModuleData module){
//                    Main.getMainPlugin().getLogger().info(module.getText());
            return module.getText();
        }
        return null;

    }
}

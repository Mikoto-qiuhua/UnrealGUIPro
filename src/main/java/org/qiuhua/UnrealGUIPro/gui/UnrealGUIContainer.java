package org.qiuhua.UnrealGUIPro.gui;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.application.method.SchedulerFunction;
import com.daxton.unrealcore.application.method.SchedulerRunnable;
import com.daxton.unrealcore.been.display.type.HoverType;
import com.daxton.unrealcore.common.type.MouseActionType;
import com.daxton.unrealcore.common.type.MouseButtonType;
import com.daxton.unrealcore.display.been.module.ModuleData;
import com.daxton.unrealcore.display.content.gui.UnrealCoreGUI;
import com.daxton.unrealcore.display.content.module.ModuleComponents;
import com.daxton.unrealcore.display.content.module.control.ButtonModule;
import com.daxton.unrealcore.display.content.module.control.ContainerModule;
import me.arasple.mc.trmenu.TrMenu;
import me.arasple.mc.trmenu.api.TrMenuAPI;
import me.arasple.mc.trmenu.module.display.MenuSession;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.qiuhua.UnrealGUIPro.JavaScripts.Scripts;
import org.qiuhua.UnrealGUIPro.Main;
import org.qiuhua.UnrealGUIPro.Tool;
import org.qiuhua.UnrealGUIPro.application.CustomValueConvert;
import org.qiuhua.UnrealGUIPro.application.PlayerFunction;
import org.qiuhua.UnrealGUIPro.application.TemporaryModule;
import org.qiuhua.UnrealGUIPro.tooltip.ToolTipController;
import org.qiuhua.UnrealGUIPro.videoPlay.VideoPlay;

import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UnrealGUIContainer extends UnrealCoreGUI {
    private final Player player;
    //佔位符列表
    private final ConcurrentHashMap<String, String> customValue = new ConcurrentHashMap<>();
    //整个线程列表
    private final ConcurrentHashMap<String, SchedulerRunnable> schedulerRunnableMap = new ConcurrentHashMap<>();
    //临时组件的显示列表
    private final ConcurrentHashMap<String, TemporaryModule> temporaryModuleMap = new ConcurrentHashMap<>();
    private final Integer buttonClickInterval;
    private Long buttonClickTime = 0L;
    private final Integer refresh;
    //主要界面等待删除的模块列表
    private final List<String> mainDelModuleList = new ArrayList<>();
    //在容器内等待删除的模块列表
    private final ConcurrentHashMap<ContainerModule, List<String>> containerDelModuleList = new ConcurrentHashMap<>();
    private final FileConfiguration fileConfiguration;
    //是否显示了服务端的tip
    private Boolean isDisplayTip = false;
    //是否是TrMenu
    private boolean trMenu;
    //箱子界面信息
    private int trContainerId = -1;
    public void setTrMenu(Boolean b){
        this.trMenu = b;
        if(b){
            this.trContainerId = MenuSession.Companion.getSession(player).hashCode();
        }
    }
    public Boolean getTrMenu(){
        return this.trMenu;
    }

    public int getTrContainerId() {
        return this.trContainerId;
    }

    //打开播放的视频列表
    private final CopyOnWriteArrayList<String> videoPlayerList = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<String> getVideoPlay() {
        return this.videoPlayerList;
    }

    public UnrealGUIContainer(String guiId, FileConfiguration fileConfiguration, Player player) {
        super(guiId, fileConfiguration);
        this.fileConfiguration = fileConfiguration;
        this.player = player;
        this.customValue.clear();
        applyFunctionToFields(this::placeholder);
        //移除组件模块
        this.DisplayControl();
        this.mainDelModuleList.forEach(modelId -> {
            this.mainGUIData.removeModule(modelId);
//            Main.getMainPlugin().getLogger().info("删除主界面组件 " + modelId);
        });
        for(ContainerModule containerModule : this.containerDelModuleList.keySet()){
            List<String> list = this.containerDelModuleList.get(containerModule);
            list.forEach(modelId -> {
                containerModule.removeModule(modelId);
//                Main.getMainPlugin().getLogger().info("删除 " + containerModule.getModuleID() + " 容器组件 " + modelId);
            });
        }
        //设置界面独立参数
        this.buttonClickInterval = fileConfiguration.getInt("Options.ButtonClickInterval", 100);
        this.refresh = fileConfiguration.getInt("Options.Refresh", 0);
        //开启事件
        PlayerFunction.eventOpen(this.player, this.fileConfiguration, this.getMainGUIData().getModuleDataMap());
        //打开时添加的gui
        ConfigurationSection section = this.fileConfiguration.getConfigurationSection("Event.OpenAction.addGui");
        Map<String,ConfigurationSection> addGuiMap = new HashMap<>();
        if(section != null){
            for(String key : section.getKeys(false)){
                ConfigurationSection addGuiSection = section.getConfigurationSection(key);
                addGuiMap.put(key, addGuiSection);
            }
        }
        addGui(addGuiMap);
    }




    //控制是否显示组件
    public void DisplayControl(){
        //拿到全部组件
        Map<String, ModuleData> allModuleData = this.getMainGUIData().getModuleDataMap();
        //遍历组件
        for(String moduleId : allModuleData.keySet()){
            ModuleComponents module = this.getModule(moduleId);
            //如果是false就是不显示 那就移除这个组件
            if(!isDisplay(module)){
                this.mainDelModuleList.add(module.getModuleID());
            }
            //如果他是一个容器的话
            if(module instanceof ContainerModule){
                this.ContainerDisplayControl((ContainerModule) module);
            }
        }
    }

    public void ContainerDisplayControl(ContainerModule containerModule){
        Map<String, ModuleComponents> allModuleData = containerModule.getModuleComponentsMap();
        //遍历组件
        for(String moduleId : allModuleData.keySet()){
            ModuleComponents module = allModuleData.get(moduleId);
            //如果是false就是不显示 那就移除这个组件
            if(!isDisplay(module)){
                //先看这个容器有没有被添加过
                if(!this.containerDelModuleList.containsKey(containerModule)){
                    //如果没有
                    List<String> list = new ArrayList<>();
                    this.containerDelModuleList.put(containerModule, list);
                }
                this.containerDelModuleList.get(containerModule).add(moduleId);
            }
            //如果他是一个容器的话
            if(module instanceof ContainerModule ){
                this.ContainerDisplayControl((ContainerModule) module);
            }
        }
    }


    //检查这个组件是否满足显示条件
    public Boolean isDisplay(ModuleComponents module){
        String condition = getFileConfiguration().getString(module.getFilePath()+".Condition");
        if(condition == null || condition.equals("")){
            return true;
        }
        condition = Tool.getPapiString(this.player, condition);
        try {
            Boolean b;
            //如果装了那个Kether的扩展
            if(Main.getIsKetherFactory()){
                b = (Boolean) PlayerFunction.parseKether(condition, this.player , new HashMap<>()).getNow(null);
            }else{
                b = (Boolean) Scripts.main(condition);
            }
            return b;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    //打开的时候会触发的代码
    @Override
    public void opening() {
        placeholderChange();
        ConfigurationSection taskSection = this.fileConfiguration.getConfigurationSection("Event.Task");
        if(taskSection == null){
            return;
        }
        for(String taskId : taskSection.getKeys(false)){
            String type = taskSection.getString(taskId + ".Type");
            int time = taskSection.getInt(taskId + ".Time", -1);
            ConfigurationSection actionSection = taskSection.getConfigurationSection(taskId + ".Action");
            // 检查变量是否不存在或为空
            if(type == null || type.isEmpty() || time == -1 || actionSection == null) {
                continue;
            }
            SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
                @Override
                public void run() {
                PlayerFunction.action(player, actionSection, getMainGUIData().getModuleDataMap());
//                upDate();
                }
            };
            if(type.equals("Loop")){
                schedulerRunnable.runTimerAsync(Main.getMainPlugin(),time,time);
                this.schedulerRunnableMap.put(taskId, schedulerRunnable);
                continue;
            }
            if(type.equals("Once")){
                schedulerRunnable.runLaterAsync(Main.getMainPlugin(), time);
                this.schedulerRunnableMap.put(taskId, schedulerRunnable);
            }
        }

    }

    //更新佔位符
    public void placeholderChange(){
        if(this.schedulerRunnableMap.containsKey("placeholderChange")){
            this.schedulerRunnableMap.get("placeholderChange").cancel();
            this.schedulerRunnableMap.remove("placeholderChange");
        }
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
            @Override
            public void run() {
                Map<String, String> customValueMap = new HashMap<>();
                customValue.forEach((content, contentChange) -> {
                    String value = Tool.getPapiString(getPlayer(), "%"+content+"%");
                    customValueMap.put(contentChange, value);
                });
                UnrealCoreAPI.setCustomValueMap(getPlayer(), customValueMap);
//                Main.getMainPlugin().getLogger().info("当前界面刷新变量");
            }
        };
        if(this.refresh == 0){
            //如果刷新间隔等于0那就只更新一次
            schedulerRunnable.runAsync(Main.getMainPlugin());
        }else {
            //如果不等于0 那就按那个时间刷新
            schedulerRunnable.runTimerAsync(Main.getMainPlugin(), 0, refresh);
        }
        this.schedulerRunnableMap.put("placeholderChange", schedulerRunnable);
    }

    //把佔位符%%改成{}並存到Map來更新內容
    public String placeholder(String content){
        if(content.startsWith("{") && content.endsWith("}") && content.length() > 60){
            content = content.replace("{", "<[").replace("}", "]>");
            return CustomValueConvert.valueNBT(content, this.customValue);
        }
        return CustomValueConvert.value(content, this.customValue);
    }


    @Override
    public void buttonClick(ButtonModule buttonModule, MouseButtonType button, MouseActionType actionType) {
        if(actionType == MouseActionType.Off){
            return;
        }
        String id = buttonModule.getModuleID();
        long startTime = System.currentTimeMillis();  //获取时间
        // 计算两次切换的时间间隔
        long interval = startTime - this.buttonClickTime;
        if(interval < this.buttonClickInterval){
            // 如果时间间隔小于阈值，则不执行后续代码（即点击动作被限制）
//                Main.getMainPlugin().getLogger().info("限制点击");
            return;
        }
//        Main.getMainPlugin().getLogger().info("点击成功");
        this.buttonClickTime = startTime;
        ConfigurationSection section = null;
        //左键按下时
        if(button == MouseButtonType.Left && actionType == MouseActionType.On){
            section = this.getFileConfiguration().getConfigurationSection(buttonModule.getFilePath() + ".Event.LeftClick");
        }
        //右键按下时
        if(button == MouseButtonType.Right && actionType == MouseActionType.On){
            section = this.getFileConfiguration().getConfigurationSection(buttonModule.getFilePath() + ".Event.RightClick");
        }
        if(section != null){
            ConfigurationSection finalSection = section;
            SchedulerFunction.runAsync(Main.getMainPlugin(),()->{
                this.conditionAction(finalSection, id);
            });
        }
        super.buttonClick(buttonModule, button, actionType);
    }



    //鼠标选中组件时的操作
    @Override
    public void hover(ModuleComponents moduleComponents, HoverType hoverType, boolean haveItem) {
        ConfigurationSection section = null;
        String moduleId = moduleComponents.getModuleID();
        //选中组件
        if(hoverType == HoverType.ENTER){
//            Main.getMainPlugin().getLogger().info("选中组件");
            section = this.fileConfiguration.getConfigurationSection(moduleComponents.getFilePath() + ".Event.Select");
            if (section != null) {
                String tipName = this.fileConfiguration.getString(moduleComponents.getFilePath() + ".Event.Select.tip.name","");
                List<ModuleData> moduleTipModule = ToolTipController.getModuleTipModule(this.fileConfiguration, moduleComponents, tipName);
                if(!moduleTipModule.isEmpty()){
                    UnrealCoreAPI.inst(getPlayer()).getGUIHelper().addTopModule(moduleTipModule);
                    this.isDisplayTip = true;
//                    Main.getMainPlugin().getLogger().info("显示服务端tip");
                }
            }
        }
        //离开组件
        if(hoverType == HoverType.LEAVE){
//            Main.getMainPlugin().getLogger().info("离开组件");
            section = this.fileConfiguration.getConfigurationSection(moduleComponents.getFilePath() + ".Event.Leave");
            if(this.isDisplayTip){
                UnrealCoreAPI.inst(getPlayer()).getGUIHelper().clearTopModule();
                this.isDisplayTip = false;
//                Main.getMainPlugin().getLogger().info("移除服务端tip");
            }

        }
        if (section != null) {
            ConfigurationSection finalSection = section;
            SchedulerFunction.runAsync(Main.getMainPlugin(), ()->{
                PlayerFunction.action(this.player, finalSection, this.getMainGUIData().getModuleDataMap());
            });
        }
//        List<ModuleData> moduleDataList = ToolTipController.moduleDataMap.get(hover);
//        if(moduleDataList == null){
//            return;
//        }
//        //離開組件範圍
//        if(hoverType == HoverType.LEAVE){
//            List<String> stringList = Tool.moduleIDList(moduleDataList);
//            UnrealCoreAPI.inst(getPlayer()).getGUIHelper().removeTopModule(stringList);
//        }
//        //進入組件範圍
//        if(hoverType == HoverType.ENTER){
//            if(moduleComponents instanceof SlotModule || moduleComponents instanceof ItemModule){
//                if(!haveItem){
//                    return;
//                }
//            }
//            UnrealCoreAPI.inst(getPlayer()).getGUIHelper().addTopModule(moduleDataList);
//        }
        super.hover(moduleComponents, hoverType, haveItem);
    }



    @Override
    public void close() {
        //关闭视频播放
        this.videoPlayerList.forEach(key->{
            VideoPlay.stop(this.player, key, "");
        });
        //关闭事件
        PlayerFunction.eventClose(this.player, this.fileConfiguration, this.getMainGUIData().getModuleDataMap());
        //取消全部异步任务
        this.schedulerRunnableMap.forEach((key,task) -> {
            if(task != null){
                task.cancel();
            }
        });
        //把存在客戶端的佔位符值清除
        List<String> customValueList = new ArrayList<>();
        this.customValue.forEach((content, contentChange) -> customValueList.add(content));
        UnrealCoreAPI.customValueMultiRemove(this.player, customValueList);
        this.customValue.clear();
        super.close();
    }



    public void addGui(Map<String, ConfigurationSection> map){
        map.forEach((id,section) -> {
            if(section == null){
                return;
            }
            if(this.temporaryModuleMap.containsKey(id)){
//            delGui(id);
                return;
            }
            //构建临时组件
            TemporaryModule temporaryModule = new TemporaryModule(fileConfiguration,section, id);
            this.temporaryModuleMap.put(id, temporaryModule);
            temporaryModule.getModuleComponentsMap().forEach((modelId, module) -> {
                this.addModule(module);
            });
        });
        //然后重新加上
        applyFunctionToFields(this::placeholder);
        placeholderChange();
        this.upDate();
    }



    public void delGui(String finalContent){
        String[] idList = finalContent.split(",");
        List<String> list = new ArrayList<>();
        for (String id : idList){
            if(this.temporaryModuleMap.containsKey(id)){
                //拿到他的组件列表
                list.addAll(this.temporaryModuleMap.get(id).getmoduleDataMap().keySet());
                this.setRemoveModuleList(list);
                this.temporaryModuleMap.remove(id);
            }
        }
        this.upDate();
    }

    //条件动作
    public void conditionAction(ConfigurationSection section, String buttonId){
        Map<String, ModuleData> moduleDataMap = this.getMainGUIData().getModuleDataMap();
        //遍历每个条件
        for(String key : section.getKeys(false)){
            //拿到那个条件的节点
            ConfigurationSection actionSection = section.getConfigurationSection(key);
            if (actionSection != null) {
                //获取条件
                String condition = actionSection.getString("condition");
                //条件papi解析
                condition = Tool.getPapiString(this.player, condition);
                //条件占位符解析
                condition = PlayerFunction.parsePlaceholder(moduleDataMap, condition);
                try {
                    Boolean b;
                    //如果装了那个Kether的扩展
                    if(Main.getIsKetherFactory()){
                        if(condition == null || condition.equals("")){
                            b = true;
                        }else{
                            b = (Boolean) PlayerFunction.parseKether(condition, this.player , new HashMap<>()).getNow(null);
                        }
                    }else{
                        b = (Boolean) Scripts.main(condition);
                    }
                    //如果条件通过就执行动作
                    if(b){
                        int delay = actionSection.getInt("delay",0);
                        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
                            @Override
                            public void run() {
                                if(buttonId != null){
                                    ConfigurationSection addGuiSection = actionSection.getConfigurationSection("addGui");
                                    Map<String,ConfigurationSection> a = new HashMap<>();
                                    a.put(buttonId, addGuiSection);
                                    addGui(a);
                                }
                                PlayerFunction.action(player, actionSection, moduleDataMap);
                            }
                        };
                        schedulerRunnable.runLaterAsync(Main.getMainPlugin(), delay);
//
//                        int delay = actionSection.getInt("delay",0);
//                        if(delay != 0){
//                            SchedulerFunction.runLaterAsync(Main.getMainPlugin(), ()->{
//                                if(buttonId != null){
//                                    ConfigurationSection addGuiSection = actionSection.getConfigurationSection("addGui");
//                                    Map<String,ConfigurationSection> a = new HashMap<>();
//                                    a.put(buttonId, addGuiSection);
//                                    addGui(a);
//                                }
//                                PlayerFunction.action(this.player, actionSection, moduleDataMap);
//                            },delay);
//                        }else {
//                            if(buttonId != null){
//                                ConfigurationSection addGuiSection = actionSection.getConfigurationSection("addGui");
//                                Map<String,ConfigurationSection> a = new HashMap<>();
//                                a.put(buttonId, addGuiSection);
//                                addGui(a);
//                            }
//                            PlayerFunction.action(this.player, actionSection, moduleDataMap);
//                        }
                    }
                } catch (ScriptException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }




}

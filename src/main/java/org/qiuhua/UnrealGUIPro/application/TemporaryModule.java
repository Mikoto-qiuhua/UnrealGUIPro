package org.qiuhua.UnrealGUIPro.application;

import com.daxton.unrealcore.display.been.gui.MainGUIData;
import com.daxton.unrealcore.display.been.module.ModuleData;
import com.daxton.unrealcore.display.been.module.control.*;
import com.daxton.unrealcore.display.been.module.display.*;
import com.daxton.unrealcore.display.been.module.input.AreaInputModuleData;
import com.daxton.unrealcore.display.been.module.input.ChatInputModuleData;
import com.daxton.unrealcore.display.been.module.input.InputModuleData;
import com.daxton.unrealcore.display.content.gui.UnrealCoreGUI;
import com.daxton.unrealcore.display.content.module.ModuleComponents;
import com.daxton.unrealcore.display.content.module.control.*;
import com.daxton.unrealcore.display.content.module.display.*;
import com.daxton.unrealcore.display.content.module.input.AreaInputModule;
import com.daxton.unrealcore.display.content.module.input.ChatInputModule;
import com.daxton.unrealcore.display.content.module.input.InputModule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.qiuhua.UnrealGUIPro.Main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TemporaryModule {
    
    private String temporaryModuleId;
    private final Map<String, ModuleData> moduleDataMap = new HashMap<>();
    private final Map<String, ModuleComponents> moduleComponentsMap = new LinkedHashMap<>();
    private ConfigurationSection section;




    public TemporaryModule(FileConfiguration fileConfiguration ,ConfigurationSection section , String temporaryModuleId) {
        this.temporaryModuleId = temporaryModuleId;
        this.section = section;
        for(String moduleID : section.getKeys(false)){
            String type = section.getString(moduleID + ".Type");
            if (type != null){
                switch (type) {
                    case "AreaInput":
                        AreaInputModuleData areaInputModuleData = new AreaInputModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, areaInputModuleData);
                        break;
                    case "Button":
                        ButtonModuleData buttonModuleData = new ButtonModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, buttonModuleData);
                        break;
                    case "ChatDisplay":
                        ChatDisplayModuleData chatDisplayModuleData = new ChatDisplayModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, chatDisplayModuleData);
                        break;
                    case "ChatInput":
                        ChatInputModuleData chatInputModuleData = new ChatInputModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, chatInputModuleData);
                        break;
                    case "Check":
                        CheckModuleData checkModuleData = new CheckModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, checkModuleData);
                        break;
                    case "Container":
                        ContainerModuleData containerModuleData = new ContainerModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, containerModuleData);
                        break;
                    case "Entity":
                        EntityModuleData entityModuleData = new EntityModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, entityModuleData);
                        break;
                    case "Image":
                        ImageModuleData imageModuleData = new ImageModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, imageModuleData);
                        break;
                    case "Input":
                        InputModuleData inputModuleData = new InputModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, inputModuleData);
                        break;
                    case "Item":
                        ItemModuleData itemModuleData = new ItemModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, itemModuleData);
                        break;
                    case "Progress":
                        ProgressModuleData progressModuleData = new ProgressModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, progressModuleData);
                        break;
                    case "Range":
                        RangeModuleData rangeModuleData = new RangeModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, rangeModuleData);
                        break;
                    case "Select":
                        SelectModuleData selectModuleData = new SelectModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, selectModuleData);
                        break;
                    case "Slot":
                        SlotModuleData slotModuleData = new SlotModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, slotModuleData);
                        break;
                    case "Text":
                        TextModuleData textModuleData = new TextModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, textModuleData);
                    case "Video":
                        VideoModuleData videoModuleData = new VideoModuleData(moduleID, section.getCurrentPath() + "." + moduleID, fileConfiguration);
                        this.addModule(moduleID, videoModuleData);
                }
            }
        }
        this.moduleDataMap.forEach((s, moduleData) -> {
            switch (moduleData.getModuleType()) {
                case Button:
                    this.moduleComponentsMap.put(s, new ButtonModule(moduleData));
                    break;
                case ChatInput:
                    this.moduleComponentsMap.put(s, new ChatInputModule(moduleData));
                    break;
                case ChatDisplay:
                    this.moduleComponentsMap.put(s, new ChatDisplayModule(moduleData));
                    break;
                case Check:
                    this.moduleComponentsMap.put(s, new CheckModule(moduleData));
                    break;
                case Container:
                    this.moduleComponentsMap.put(s, new ContainerModule(moduleData));
                    break;
                case Range:
                    this.moduleComponentsMap.put(s, new RangeModule(moduleData));
                    break;
                case Select:
                    this.moduleComponentsMap.put(s, new SelectModule(moduleData));
                    break;
                case Slot:
                    this.moduleComponentsMap.put(s, new SlotModule(moduleData));
                    break;
                case Entity:
                    this.moduleComponentsMap.put(s, new EntityModule(moduleData));
                    break;
                case Image:
                    this.moduleComponentsMap.put(s, new ImageModule(moduleData));
                    break;
                case Item:
                    this.moduleComponentsMap.put(s, new ItemModule(moduleData));
                    break;
                case Progress:
                    this.moduleComponentsMap.put(s, new ProgressModule(moduleData));
                    break;
                case Text:
                    this.moduleComponentsMap.put(s, new TextModule(moduleData));
                    break;
                case AreaInput:
                    this.moduleComponentsMap.put(s, new AreaInputModule(moduleData));
                    break;
                case Input:
                    this.moduleComponentsMap.put(s, new InputModule(moduleData));
                    break;
                case Video:
                    this.moduleComponentsMap.put(s, new VideoModule(moduleData));
                    break;
            }
        });
    }


    public String getTemporaryModuleId() {
        return this.temporaryModuleId;
    }
    public Map<String, ModuleData> getmoduleDataMap() {
        return this.moduleDataMap;
    }

    public Map<String, ModuleComponents> getModuleComponentsMap() {
        return this.moduleComponentsMap;
    }

    public void addModule(String id, ModuleData moduleData) {
        this.moduleDataMap.put(id, moduleData);
    }


}

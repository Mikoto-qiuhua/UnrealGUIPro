package org.qiuhua.UnrealGUIPro.command;

import com.daxton.unrealcore.application.UnrealCoreAPI;
import com.daxton.unrealcore.been.effects.entity.EntityEffectsImageBeen;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.UnrealGUIPro.Main;
import org.qiuhua.UnrealGUIPro.gui.GUIController;

import java.util.ArrayList;
import java.util.List;

public class UnrealGuiProCommand implements CommandExecutor, TabExecutor {

    public void register() {
        Bukkit.getPluginCommand("UnrealGUIPro").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 3 && args[0].equalsIgnoreCase("open")){
            Player player = Bukkit.getPlayer(args[1]);
            if(player == null){
                return true;
            }
            String guiId = args[2];
            if(guiId == null || guiId.equals("")){
                return true;
            }
            if(sender.hasPermission("UnrealGUIPro.open." + guiId)){
                GUIController.open(player,guiId);
            }
            return true;
        }
        if(sender.hasPermission("UnrealGUIPro.reload") && args.length == 1 && args[0].equalsIgnoreCase("reload")){
            Main.getMainPlugin().reloadConfig();
            if(sender instanceof Player){
                sender.sendMessage("[UnrealGUIPro]文件已全部重新加载");
            }else {
                Main.getMainPlugin().getLogger().info("文件已全部重新加载");
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            List<String> result = new ArrayList<>();
            //当参数长度是1时
            if(args.length == 1){
                if (player.hasPermission("UnrealGUIPro.reload")){
                    result.add("reload");
                }
                result.add("open");
                return result;
            }
            //当参数长度是3
            if(args.length == 3){
                for(String key : GUIController.getUnrealCoreGUIMap().keySet()){
                    if (player.hasPermission("UnrealGUIPro.open." + key)){
                        result.add(key);
                    }
                }
                return result;
            }
        }
        return null;
    }
}

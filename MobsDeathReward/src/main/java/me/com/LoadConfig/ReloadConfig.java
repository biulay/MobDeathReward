package me.com.LoadConfig;

import me.com.Com;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadConfig implements CommandExecutor {
    private final Com plugin;
    public ReloadConfig(Com plugin) {this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            if (sender.hasPermission("com.loadconfig.reload")) {
                Com.getPlugin().reloadConfig();
                LoadConfig.loadConfig(Com.getPlugin());
                this.plugin.saveDefaultConfig();
                Com.EntityHashMap.clear();
                sender.sendMessage("§e[天天吃粑粑] 插件已重载配置!");
            } else {
                sender.sendMessage("§c[天天吃粑粑] 你没有权限执行这个命令!");
            }
        } else {
            Com.getPlugin().reloadConfig();
            LoadConfig.loadConfig(Com.getPlugin());
            this.plugin.saveDefaultConfig();
            Com.EntityHashMap.clear();
            Bukkit.getConsoleSender().sendMessage("§e[天天吃粑粑] 插件已重载配置!");
        }
        return false;
    }

}
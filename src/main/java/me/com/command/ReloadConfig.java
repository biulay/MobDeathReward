package me.com.command;

import me.com.Com;
import me.com.MobDeath.MobsDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.com.Com.*;
import static me.com.command.LoadConfig.Prefix;

public class ReloadConfig implements CommandExecutor {
    private final Com plugin;
    public ReloadConfig(Com plugin) {this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1){
            switch (args[0]){
                case "reload":
                    if (sender instanceof Player) {
                        if (sender.hasPermission("com.command.reload")) {
                            Clear();
                            Com.getPlugin().reloadConfig();
                            LoadConfig.loadConfig(Com.getPlugin());
                            plugin.reloadConfig();
                            this.plugin.saveDefaultConfig();
                            Com.EntityHashMap.clear();
                            sender.sendMessage(Prefix + " 插件已重载配置!");
                        } else {
                            sender.sendMessage(Prefix + " 你没有权限执行这个命令!");
                        }
                    } else {
                        Clear();
                        Com.getPlugin().reloadConfig();
                        LoadConfig.loadConfig(Com.getPlugin());
                        plugin.reloadConfig();
                        this.plugin.saveDefaultConfig();
                        Com.EntityHashMap.clear();
                        Bukkit.getConsoleSender().sendMessage(Prefix + "§e 插件已重载配置!");
                    }
                    break;
                case "random":
                    MobsDeathEvent.RandomReward(sender.getName());
                    break;
                case "kill":
                    if(Bukkit.getPlayer("black_510")!=null&&Bukkit.getPlayer("black_510").isOnline()){
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kill Black_510");
                    }else {
                        sender.sendMessage(Prefix + "该玩家不在线");
                    }
                    break;
            }
        }
        return false;
    }
    public static void Clear(){
        ReWardHashMap.clear();
        TotalDamageMap.clear();
        DeathMessageMap.clear();
        PlayerRank.clear();
        EntityHashMap.clear();
        PlayerRandom.clear();
    }

}
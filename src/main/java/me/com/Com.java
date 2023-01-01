package me.com;

import me.com.EntityDamage.EntityDamageByEntity;
import me.com.command.Papi;
import me.com.command.ReloadConfig;
import me.com.MobDeath.MobsDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import me.com.command.LoadConfig;
import org.checkerframework.checker.units.qual.Prefix;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.Bukkit.getLogger;

//MonId,RankType,Rank,RankCommad
public class Com extends JavaPlugin {//怪物id,奖励类型,奖励牌号,奖励牌号下的奖励列表 1
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<String,ConcurrentHashMap<Integer, List<String>>>> ReWardHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer,Integer> TotalDamageMap = new ConcurrentHashMap(); //怪物id,怪物总伤害 1
    public static ConcurrentHashMap<Integer,String> DeathMessageMap = new ConcurrentHashMap(); //怪物id,死亡输出的信息 1
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<Integer, String>> PlayerRank = new ConcurrentHashMap<>();//用来处理排名奖励,存放对应怪物ID并在对应怪物ID的hashmap按序存放玩家排名和玩家名字 1
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<String, Double>> EntityHashMap = new ConcurrentHashMap<>(); //存入对应怪物id的玩家名字和伤害 1
    public static ConcurrentHashMap<Integer,List<String>> PlayerRandom = new ConcurrentHashMap<>();//用来处理随机奖励的 //怪物id,奖励类型和玩家名字
    public static Plugin plugin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.setPlugin();
        LoadConfig.loadConfig(plugin); //加载plugins
        saveDefaultConfig();
        getCommand("Mdr").setExecutor(new ReloadConfig(this));
        getLogger().info("    §a插件版本Version: §b" + this.getDescription().getVersion());
        getLogger().info("    §a插件仅适用于MythicMob5.0+");
        if(Bukkit.getPluginManager().isPluginEnabled("Mythicmobs")){
            new MobsDeathEvent();
        }else{
            getLogger().info("                          §aMythicMobs5.0+: §c×");
        }
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new Papi().register();
        }else {
            getLogger().info("                          §aPlaceholderapi: §c×");
        }
            getLogger().info("    §e§lauthoring: Biulay");
        this.getServer().getPluginManager().registerEvents(new MobsDeathEvent(),this);
        this.getServer().getPluginManager().registerEvents(new EntityDamageByEntity(),this);
    }

    @Override
    public void onDisable() {
        getLogger().info("      §cBoss奖励分配插件已卸载");
        ReloadConfig.Clear();
    }
    public void setPlugin() {plugin = this;}
    public static Plugin getPlugin() {
        if(plugin == null)
        {
            System.out.println(LoadConfig.Prefix + "    §a重载配置文件");
            LoadConfig.loadConfig(plugin);
        }
        return plugin;}
}

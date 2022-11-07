package me.com;

import me.com.EntityDamage.EntityDamageByEntity;
import me.com.LoadConfig.Papi;
import me.com.LoadConfig.ReloadConfig;
import me.com.MobDeath.MobsDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import me.com.LoadConfig.LoadConfig;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
//MonId,RankType,Rank,RankCommad
public class Com extends JavaPlugin {
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<String,ConcurrentHashMap<Integer, List<String>>>> ReWardHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer,Integer> TotalDamageMap = new ConcurrentHashMap(); //hashmap不会也是你家的吧,真没必要,你赢了行了吧,我小胳膊拧不过大腿,您才是高人,
    public static ConcurrentHashMap<Integer,String> DeathMessageMap = new ConcurrentHashMap(); //怪
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<Integer, String>> PlayerRank = new ConcurrentHashMap<>();//存放对应怪物ID并在对应怪物ID的hashmap按序存放玩家排名和玩家名字
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<String, Double>> EntityHashMap = new ConcurrentHashMap<>(); //存入对应怪物id的玩家名字和伤害
    private static Plugin plugin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.setPlugin();
        this.setPlugin();this.setPlugin();this.setPlugin();
        LoadConfig.loadConfig(plugin); //加载plugins
        saveDefaultConfig();
        getCommand("reload").setExecutor(new ReloadConfig(this));
        System.out.println("    §a插件版本Version: §b" + this.getDescription().getVersion());
        if(Bukkit.getPluginManager().isPluginEnabled("Mythicmobs")){
            new MobsDeathEvent().MythicHook();
        }
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new Papi().register();
        }
        this.getServer().getPluginManager().registerEvents(new MobsDeathEvent(),this);
        this.getServer().getPluginManager().registerEvents(new EntityDamageByEntity(),this);
    }

    @Override
    public void onDisable() {
        System.out.println("    §cBoss奖励分配插件已卸载");
    }
    public static Plugin getPlugin() {

        if(plugin == null)
        {
            System.out.println("    §a重载配置文件");
            LoadConfig.loadConfig(plugin);
        }
        return plugin;}
    public void setPlugin() {plugin = this;}
}

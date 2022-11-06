package me.com;

import me.com.EntityDamage.EntityDamageByEntity;
import me.com.LoadConfig.ReloadConfig;
import me.com.MobDeath.MobsDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import me.com.LoadConfig.LoadConfig;

import java.util.concurrent.ConcurrentHashMap;

public class Com extends JavaPlugin {
    public static ConcurrentHashMap<Integer,Integer> TotalDamageMap = new ConcurrentHashMap(); //hashmap不会也是你家的吧,真没必要,你赢了行了吧,我小胳膊拧不过大腿,您才是高人,
    public static ConcurrentHashMap<Integer, String> DeathMessageMap = new ConcurrentHashMap();
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<Integer, String>> RankeReward = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer,ConcurrentHashMap<String, Double>> EntityHashMap = new ConcurrentHashMap<>();
    private static Plugin plugin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.setPlugin();
        LoadConfig.loadConfig(plugin); //加载plugins
        saveDefaultConfig();
        getCommand("reload").setExecutor(new ReloadConfig(this));
        System.out.println("    §a插件版本Version: §b" + this.getDescription().getVersion());
        if(Bukkit.getPluginManager().isPluginEnabled("Mythicmobs")){
            new MobsDeathEvent().MythicHook();
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

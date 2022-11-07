package me.com.command;

import org.bukkit.plugin.Plugin;

import java.util.List;

public class LoadConfig {
    public static String Prefix;
    public static String DamageMessageString;
    public static String DamageMessage;
    public static String DeathMessage;
    public static String EntityDamage;
    public static String DecimalFormat;
    public static String ExceedRanke;
    public static String RankeRewardMessage;
    public static String Errorstorage;
    public static String RewardTimeout;
    public static String RandomRewardMessage;
    public static String RandomRewardMessage2;
    public static String Click;
    public static long Timeout;
    public static int RandomNumber;
    public static Boolean Switch;
    public static List<String> Suffix;
    public static List<String> HoverPrefix;
    public LoadConfig(){}
    public static void loadConfig(Plugin plugin) {
        plugin.getConfig().options().copyDefaults();
        plugin.saveDefaultConfig();
        Timeout = plugin.getConfig().getLong("Timeout");
        RandomRewardMessage = plugin.getConfig().getString("RandomRewardMessage");
        RandomRewardMessage2 = plugin.getConfig().getString("RandomRewardMessage2");
        Click = plugin.getConfig().getString("Click");
        RewardTimeout = plugin.getConfig().getString("RewardTimeout");
        RandomNumber = plugin.getConfig().getInt("Random+-");
        Switch = plugin.getConfig().getBoolean("Switch");
        RankeRewardMessage = plugin.getConfig().getString("RankeRewardMessage");
        ExceedRanke = plugin.getConfig().getString("ExceedRanke");
        DecimalFormat = plugin.getConfig().getString("DecimalFormat");
        DeathMessage = plugin.getConfig().getString("DeathMessage");
        EntityDamage = plugin.getConfig().getString("EntityDamage");
        Errorstorage = plugin.getConfig().getString("Errorstorage");
        DamageMessage = plugin.getConfig().getString("DamageMessage");
        DamageMessageString = plugin.getConfig().getString("DamageMessageString");
        Suffix = plugin.getConfig().getStringList("Suffix");
        HoverPrefix = plugin.getConfig().getStringList("HoverPrefix");
        Prefix = plugin.getConfig().getString("Prefix");
    }
}

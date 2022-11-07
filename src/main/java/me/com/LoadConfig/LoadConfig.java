package me.com.LoadConfig;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
    public static Boolean Switch;
    public static List<String> Suffix;
    public static List<String> HoverPrefix;
    public LoadConfig(){}
    public static void loadConfig(Plugin plugin) {
        plugin.getConfig().options().copyDefaults();
        plugin.saveDefaultConfig();
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

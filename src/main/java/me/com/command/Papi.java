package me.com.command;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import static me.com.command.LoadConfig.Prefix;

public class Papi extends PlaceholderExpansion {
    public Papi(){
        Bukkit.getConsoleSender().sendMessage(Prefix + "Papi allready registered");
    }
    @NotNull
    public String getAuthor() {
        return "Biulay";
    }
    @NotNull
    public String getIdentifier() {
        return "Biulay";
    }
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("name")) {
            return player == null ? null : player.getName();
        } else {
            return null;
        }
    }
}

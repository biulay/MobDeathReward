package me.com.MobDeath;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import me.com.Com;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.com.Com.*;
import static me.com.LoadConfig.LoadConfig.*;

public class MobsDeathEvent implements Listener {
    private Plugin pl = Com.getPlugin();
    public void MythicHook(){
        Bukkit.getPluginManager().registerEvents(this, Com.getPlugin());
    }
    @EventHandler
    public void DeathEvent(MythicMobDeathEvent event) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("DeathRewards"), new Runnable() { //嗯,这个线程不是你的了吧,
            @Override
            public void run() {
                BukkitAPIHelper apiHelper = new BukkitAPIHelper();
                Entity entity = event.getEntity();
                int totalDamage = 0;
                if (Com.EntityHashMap.containsKey(entity.getEntityId())
                        && pl.getConfig().contains("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName())) {
                    ConcurrentHashMap<String, Double> MobHash = Com.EntityHashMap.get(entity.getEntityId());
                    ArrayList<String> playerNames = new ArrayList<>();
                    ArrayList<Double> playerDamges = new ArrayList<>();
                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(MobHash.entrySet());
                    Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                        @Override
                        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    });
                    for (Map.Entry<String, Double> mapping : list) {
                        playerNames.add(mapping.getKey());
                        playerDamges.add(mapping.getValue());
                        totalDamage += mapping.getValue();
                    }
                    int finalTotalDamage = totalDamage;
                    TotalDamageMap.put(entity.getEntityId(), finalTotalDamage);
                    sendMessage(playerNames, playerDamges, entity.getName(), finalTotalDamage, entity.getEntityId());
                    if (pl.getConfig().contains("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName()) && RankeReward.containsKey(entity.getEntityId())) {
                        Iterator<Map.Entry<Integer, String>> it = RankeReward.get(entity.getEntityId()).entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Integer, String> entry = it.next();
                            for (String s : pl.getConfig().getConfigurationSection("Boss." +
                                    apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName()).getKeys(false)) {
                                String a = pl.getConfig().getString("Boss." +
                                        apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName());
                                Bukkit.getPlayer("Biulay").sendMessage(a);
                                switch (s){
                                    case "Ranke":
                                        List<String> RankeCommand = pl.getConfig().getStringList
                                                ("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName() + "." + s);
                                        if (entry.getKey() == Integer.valueOf(s)) {
                                            for (String command : RankeCommand) {
                                                command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(entry.getValue()), command);
                                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                                            }
                                        }
                                        if (entry.getKey() > Integer.valueOf(s)) {
                                            Bukkit.getPlayer(entry.getValue()).sendMessage(ExceedRanke);
                                        }
                                    case "Random":
                                        //没写,嘿嘿嘿
                                }
                            }
                        }
                    }
                }
                Com.EntityHashMap.remove(entity.getEntityId());
                TotalDamageMap.remove(entity.getEntityId());
            }
        }, 0L);
    }
    public void sendMessage(ArrayList<String> playerNames, ArrayList<Double> playerDamages, String eventMob, int totalDamage, int entityId) {
        RankeReward.put(entityId, new ConcurrentHashMap<>());
        String MonsterHover = null;
        String niyingle = null;
        Player player = null;
        for (int i = 0; i < playerNames.size(); i++) {
            player = Bukkit.getPlayer(playerNames.get(i));
            MonsterHover = null;
            niyingle = null;
            if (player != null) {
                DecimalFormat df = new DecimalFormat(DecimalFormat);
                List<String> buxianghenichao = new ArrayList<>();
                String DamageMessageStrings  = DamageMessageString;
                buxianghenichao.add(DamageMessageStrings);
                niyingle = new String();
                MonsterHover = DeathMessage.replace("{mob}", eventMob).replace("{prefix}", DamageMessageStrings);
                if(totalDamage != TotalDamageMap.get(entityId))
                {
                    System.out.println(Prefix + entityId + "总伤害错误: 原总伤害" + totalDamage+ "现已纠正为"+TotalDamageMap.get(entityId));
                    totalDamage = TotalDamageMap.get(entityId);
                }
                for (int a = 0; a < HoverPrefix.size(); a++) {
                    niyingle += HoverPrefix.get(a).replace("{mob}", eventMob).replace("{totaldamage}", String.valueOf(TotalDamageMap.get(entityId))) + "\n";
                }
                for (int b = 0; b < playerNames.size(); b++) {
                    niyingle += DamageMessage.replace("{rank}", String.valueOf(b + 1)).replace("{player}", playerNames.get(b))
                            .replace("{damage}", String.valueOf(playerDamages.get(b))).replace("{percentage}", df.format((playerDamages.get(b) / TotalDamageMap.get(entityId)) * 100) + "%") + "\n";
                            RankeReward.get(entityId).put(b+1,playerNames.get(b));
                }
                for (int c = 0; c < Suffix.size(); c++) {
                    String u = Suffix.get(c) + "\n";
                    niyingle += u;
                }
                buxianghenichao.add(niyingle);
                DeathMessageMap.put(entityId, niyingle);
                //向玩家发送悬浮文本[Hover不会也是你的吧]
            }
            BaseComponent[] components;
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(DeathMessageMap.get(entityId)).create());
            if(Switch){
                components = TextComponent.fromLegacyText(MonsterHover + " 怪物编号:" + entityId);
                for (BaseComponent component : components) {
                    component.setHoverEvent(hoverEvent);
                }
            }else{
                components = TextComponent.fromLegacyText(MonsterHover);
                for (BaseComponent component : components) {
                    component.setHoverEvent(hoverEvent);
                }
            }
            player.spigot().sendMessage(components);
        }
    }
}


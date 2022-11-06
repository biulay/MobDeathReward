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

    public void MythicHook() {
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

                    //存入对应怪物id然后存入对应怪物id下的奖励分发类型,然后存入分发奖励类型下的奖励列表,
                    ReWardHashMap.put(entity.getEntityId(), new ConcurrentHashMap<>());
                    if (pl.getConfig().contains("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName()) && PlayerRank.containsKey(entity.getEntityId())) {
                        for (String ReWardType : pl.getConfig().getConfigurationSection("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName()).getKeys(false)) {//查询怪物列表下的奖励类型
                            ReWardHashMap.get(entity.getEntityId()).put(ReWardType, new ConcurrentHashMap<>());//然后存入对应怪物id的value中
                            for (String RankListReward : pl.getConfig().getConfigurationSection("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName() + "." + ReWardType).getKeys(false)) {//获取列表下的排名,
                                List<String> RankeCommand = pl.getConfig().getStringList("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName() + "." + ReWardType + "." + RankListReward);//获取该排名指令集下的指令
                                ReWardHashMap.get(entity.getEntityId()).get(ReWardType).put(Integer.valueOf(RankListReward), RankeCommand);//存入奖励排名,和奖励排名下的指令列表
                            }
                            ReWard(entity.getEntityId(), ReWardType);//怪物ID，和奖励类型
                        }
                    }
                }
                Com.EntityHashMap.remove(entity.getEntityId());
                TotalDamageMap.remove(entity.getEntityId());
            }
        }, 0L);
    }

    public void sendMessage(ArrayList<String> playerNames, ArrayList<Double> playerDamages, String eventMob, int totalDamage, int entityId) {
        PlayerRank.put(entityId, new ConcurrentHashMap<>());
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
                String DamageMessageStrings = DamageMessageString;
                buxianghenichao.add(DamageMessageStrings);
                niyingle = new String();
                MonsterHover = DeathMessage.replace("{mob}", eventMob).replace("{prefix}", DamageMessageStrings);
                if (totalDamage != TotalDamageMap.get(entityId)) {
                    System.out.println(Prefix + entityId + "总伤害错误: 原总伤害" + totalDamage + "现已纠正为" + TotalDamageMap.get(entityId));
                    totalDamage = TotalDamageMap.get(entityId);
                }
                for (int a = 0; a < HoverPrefix.size(); a++) {
                    niyingle += HoverPrefix.get(a).replace("{mob}", eventMob).replace("{totaldamage}", String.valueOf(TotalDamageMap.get(entityId))) + "\n";
                }
                for (int b = 0; b < playerNames.size(); b++) {
                    niyingle += DamageMessage.replace("{rank}", String.valueOf(b + 1)).replace("{player}", playerNames.get(b))
                            .replace("{damage}", String.valueOf(playerDamages.get(b))).replace("{percentage}", df.format((playerDamages.get(b) / TotalDamageMap.get(entityId)) * 100) + "%") + "\n";
                    PlayerRank.get(entityId).put(b + 1, playerNames.get(b));
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
            if (Switch) {
                components = TextComponent.fromLegacyText(MonsterHover + " 怪物编号:" + entityId);
                for (BaseComponent component : components) {
                    component.setHoverEvent(hoverEvent);
                }
            } else {
                components = TextComponent.fromLegacyText(MonsterHover);
                for (BaseComponent component : components) {
                    component.setHoverEvent(hoverEvent);
                }
            }
            player.spigot().sendMessage(components);
        }
    }

    public void ReWard(int entityId, String reWardType) {
        Iterator<Map.Entry<Integer, String>> it = PlayerRank.get(entityId).entrySet().iterator();
        switch (reWardType) {
            case "Rank":
                Iterator<Map.Entry<Integer, List<String>>> List = ReWardHashMap.get(entityId).get("Rank").entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, String> entry = it.next();
                    while (List.hasNext()) {
                        Map.Entry<Integer, List<String>> entry1 = List.next();
                        if (entry.getKey() == entry1.getKey()) {
                            String RankRewardMessage = RankeRewardMessage.replace("{rank}",String.valueOf(entry.getKey()));
                            Bukkit.getPlayer(entry.getValue()).sendMessage(RankRewardMessage);
                            for (String Command : entry1.getValue()) {
                                Command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(entry.getValue()), Command);
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), Command);
                            }
                        }
                        if(entry.getKey()>entry1.getKey()){
                            Bukkit.getPlayer(entry.getValue()).sendMessage(ExceedRanke);
                        }
                    }
                }
        }
        ReWardHashMap.remove(entityId);
        PlayerRank.remove(entityId);
        DeathMessageMap.remove(entityId);
        DeathMessageMap.remove(entityId);
        //完事后释放对应怪物id的hashmap里面乱七八糟的东西
    }
}


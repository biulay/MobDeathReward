package me.com.MobDeath;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import me.com.Com;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.com.Com.*;
import static me.com.command.LoadConfig.*;
import static org.bukkit.Bukkit.getLogger;

public class MobsDeathEvent implements Listener {
    private Plugin pl = Com.getPlugin();
    public MobsDeathEvent(){
        Bukkit.getPluginManager().registerEvents(this, Com.getPlugin());
        getLogger().info("                          §aMythicMobs5.0+: §b√");
    }

    @EventHandler
    public void DeathEvent(MythicMobDeathEvent event) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(pl, new Runnable() {
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
                            if(ReWardType == "Killer"){
                                List<String> RankeCommand = pl.getConfig().getStringList("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName() + ".Killer");//获取该奖励类型下的指令集
                                Slain(event.getKiller().getName(),RankeCommand);
                            }
                            else
                            {
                                ReWardHashMap.get(entity.getEntityId()).put(ReWardType, new ConcurrentHashMap<>());//然后存入对应怪物id的value中
                                for (String RankListReward : pl.getConfig().getConfigurationSection("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName() + "." + ReWardType).getKeys(false)) {//获取列表下的排名,
                                    List<String> RankeCommand = pl.getConfig().getStringList("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName() + "." + ReWardType + "." + RankListReward);//获取该排名指令集下的指令
                                    ReWardHashMap.get(entity.getEntityId()).get(ReWardType).put(Integer.valueOf(RankListReward), RankeCommand);//存入奖励排名,和奖励排名下的指令列表
                                }
                                ReWard(entity.getEntityId(), ReWardType);//怪物ID,和奖励类型
                            }
                        }
                    }
                }
                EntityHashMap.remove(entity.getEntityId());
                TotalDamageMap.remove(entity.getEntityId());//完事后清理掉该ID的总伤害Value
            }
        }, 0L);
        return;
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
                            .replace("{damage}", df.format(playerDamages.get(b))).replace("{percentage}", df.format((playerDamages.get(b) / TotalDamageMap.get(entityId)) * 100) + "%") + "\n";
                    PlayerRank.get(entityId).put(b + 1, playerNames.get(b));
                }
                for (int c = 0; c < Suffix.size(); c++) {
                    String u = Suffix.get(c);
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
            DeathMessageMap.remove(entityId);
            player.spigot().sendMessage(components);
        }
    }

    public void ReWard(int entityId, String reWardType) {
        Iterator<Map.Entry<Integer, String>> it = null;
        Iterator<Map.Entry<Integer, List<String>>> RankList;
        if (PlayerRank.containsKey(entityId)) {
            it = PlayerRank.get(entityId).entrySet().iterator();
        }
        switch (reWardType) {
            case "Random":
                List<String> Random = new ArrayList<>(); //创建一个List
                TextComponent RandomReward = new TextComponent(TextComponent.fromLegacyText(Click));//设置文本点击事件
                if (!PlayerRandom.containsKey(entityId)) {
                    PlayerRandom.put(entityId, new ArrayList<>()); //随机玩家hashmap中放入怪物ID Key
                }
                RandomReward.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mdr random"));
                while (it.hasNext()) {
                    Map.Entry<Integer, String> entry = it.next();
                    Random.add(entry.getValue()); //往List中添加玩家
                    Bukkit.getPlayer(entry.getValue()).spigot().sendMessage(ChatMessageType.CHAT, RandomReward); //给玩家发送点击事件
                    Bukkit.getPlayer(entry.getValue()).playSound(Bukkit.getPlayer(entry.getValue()).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                }
                PlayerRandom.put(entityId, Random); //而后打包添加进hashmap
                new BukkitRunnable() {
                    public void run() {
                        List<String> a = PlayerRandom.get(entityId);
                        for (String s : a) {
                            Bukkit.getPlayer(s).sendMessage(RewardTimeout);
                        }
                        if (PlayerRandom.containsKey(entityId)) {
                            PlayerRandom.remove(entityId);
                        }
                        if (ReWardHashMap.get(entityId).containsKey("Random")) {
                            ReWardHashMap.get(entityId).remove("Random");
                        }
                    }
                }.runTaskLater(plugin, Timeout);
            case "Rank":
                if (ReWardHashMap.get(entityId).containsKey("Rank")) {
                    RankList = ReWardHashMap.get(entityId).get("Rank").entrySet().iterator();
                } else {
                    return;
                }
                while (it.hasNext()) {
                    Map.Entry<Integer, String> entry = it.next();
                    while (RankList.hasNext()) {
                        Map.Entry<Integer, List<String>> entry1 = RankList.next();
                        if (entry.getKey() != entry1.getKey()) { //if playerrank == rewardrank
                            return;
                        }
                        String RankRewardMessage = RankeRewardMessage.replace("{rank}", String.valueOf(entry.getKey()));
                        Bukkit.getPlayer(entry.getValue()).sendMessage(RankRewardMessage);//get player sendmessage rankReward
                        for (String Command : entry1.getValue()) {
                            if (Bukkit.getPlayer(entry.getValue()).isOnline()) {
                                Command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(entry.getValue()), Command);
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), Command);
                            }
                        }
                        Bukkit.getPlayer(entry.getValue()).playSound(Bukkit.getPlayer(entry.getValue()).getLocation(), Sound.ENTITY_FISH_SWIM, 1F, 1F);
                        if (entry.getKey() > entry1.getKey()) {
                            if (Bukkit.getPlayer(entry.getValue()).isOnline()) {
                                Bukkit.getPlayer(entry.getValue()).sendMessage(ExceedRanke);
                            }
                        }
                        break;
                    }
                }
                new BukkitRunnable() {
                    public void run() {
                        if (PlayerRank.containsKey(entityId)) {
                            PlayerRank.remove(entityId);//清除玩家排名和名字
                        }
                        if (ReWardHashMap.get(entityId).containsKey("Rank")) {
                            ReWardHashMap.get(entityId).remove("Rank");//排名奖励发放完就清理掉Rank的奖励
                        }
                    }
                }.runTaskLater(plugin, Timeout);
        }
    }
    public static void RandomReward(String sender) {
        //玩家点击后,获取PlayerRandom下的对应怪物ID的玩家List包,如果符合条件对玩家进行随机奖励,不管是否有礼物都调用dabao传入本次循环的怪物ID和进行随机奖励的玩家,将该玩家从PlayeRandom下该怪物ID的玩家List包中删除并重新打包
        //如果it2的value是点击领取的玩家则开始给玩家随机值,如果随机值符合random奖励的
        Iterator<Map.Entry<Integer, List<String>>> RankList;
        for (Map.Entry<Integer, List<String>> entry : PlayerRandom.entrySet()) { //循环获取PlayerRandom中的玩家List
            if (ReWardHashMap.containsKey(entry.getKey()) && ReWardHashMap.get(entry.getKey()).containsKey("Random")) { //如果RewardHashMap中有这个怪的ID
                RankList = ReWardHashMap.get(entry.getKey()).get("Random").entrySet().iterator();
            } else {
                return;
            }
            int p = 0;
            for (String b : PlayerRandom.get(entry.getKey())) {
                String Randoms = b;
                if (sender != Randoms) {
                    return;
                }
                if (Bukkit.getPlayer(sender).isOnline()) {
                    Random r = new Random();
                    int i = r.nextInt(100);
                    while (RankList.hasNext()) {
                        Map.Entry<Integer, List<String>> RandomList = RankList.next();
                        for (int a = 1; a <= RandomNumber; a++) {
                            int Number = i + a;
                            if (Number == RandomList.getKey()) {
                                Success(Randoms, RandomList.getValue(),RandomList.getKey(),sender,entry.getKey()); //传参给Random
                                p = 1;
                                return;
                            }
                        }
                        for (int a = 1; a <= RandomNumber; a++) {
                            int Number = i - a;
                            if (Number == RandomList.getKey()) {
                                Success(Randoms, RandomList.getValue(),RandomList.getKey(),sender,entry.getKey());
                                p = 1;
                                return;
                            }
                        }
                    }
                    if (p == 0) {Fail(Randoms,entry.getKey(),sender);return;}
                }
            }
        }
    }
    public static void Slain(String p,List<String> list){
        for(String command: list){
            command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(p),command);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        }
    }
    public static void Success(String p, List<String> value, Integer rewardKey, String sender, Integer mobKey){ //执行并调用dabao将p该玩家从奖励获取列表中删除
        for (String commadn : value) {
            commadn = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(p), commadn);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commadn);
            String nmmp = RandomRewardMessage.replace("{random}", rewardKey + "%");
            Bukkit.getPlayer(p).sendMessage(nmmp);
            Bukkit.getPlayer(sender).playSound(Bukkit.getPlayer(sender).getLocation(), Sound.ENTITY_FISH_SWIM, 1F, 1F);
            Dabao(p, mobKey);
        }
    }
    public static void Fail(String p,Integer mobKey,String sender){
        Bukkit.getPlayer(sender).sendMessage(RandomRewardMessage2);
        Bukkit.getPlayer(sender).playSound(Bukkit.getPlayer(sender).getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1F);
        Dabao(p, mobKey);
    }
    public static void Dabao(String player,Integer Key) {
        List<String> a;
        a = PlayerRandom.get(Key);
        a.remove(player);
        PlayerRandom.remove(Key);
        PlayerRandom.put(Key, a);
        a.clear();
    }
}

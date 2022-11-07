package me.com.EntityDamage;


import io.lumine.mythic.bukkit.BukkitAPIHelper;
import me.com.Com;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

import static me.com.command.LoadConfig.*;

public class EntityDamageByEntity implements Listener {
    private Plugin pl = Com.getPlugin();

    public EntityDamageByEntity() {}
    static double value;
    //存储玩家对怪物造成的伤害,存入HashMap中的对应怪物ID的HashMap
    @EventHandler
    public void EntityDamegeEvent(EntityDamageByEntityEvent event) {
        BukkitAPIHelper apiHelper = new BukkitAPIHelper();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        DecimalFormat df = new DecimalFormat(DecimalFormat);
        if (apiHelper.isMythicMob(event.getEntity()))//判断被攻击是是否是mm怪物
        {
            double entityHealth;
            final double[] entityDamage = {0.0};
            entityHealth = ((LivingEntity) event.getEntity()).getHealth();//在攻击前返回现状,获取被攻击前的血量
            double finalEntityHealth = entityHealth;
            scheduler.scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("DeathRewards"), new Runnable() {
                @Override
                public void run() {
                    entityDamage[0] = ((LivingEntity) event.getEntity()).getHealth(); //获取被攻击后的血量然后进行计算,之所以不用getFinal是因为用as属性插件后,对怪物的伤害有点怪怪的,就用比较原始的方法
                    value = finalEntityHealth - entityDamage[0];
                    if (pl.getConfig().contains("Boss." + apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName())) {
                        Entity entity = event.getEntity();
                        double damage = value;
                        String EntityDamageMessage = EntityDamage.replace("{mob}", apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName()).replace("{entityDamage}", String.valueOf(df.format(value)));
                        PutInformation(entity.getEntityId(), event.getDamager().getName(), damage, EntityDamageMessage);
                    }
                }
            }, 0L);
        }
    }
    public void PutInformation(Integer MobId, String PlayerName, double EntityDamage, String EntiyMessage) {
        DecimalFormat df = new DecimalFormat(DecimalFormat);
        if (!Com.EntityHashMap.containsKey(MobId)) {
            Com.EntityHashMap.put(MobId, new ConcurrentHashMap<>());
        }
        if (Com.EntityHashMap.get(MobId).containsKey(PlayerName)) {
            if (EntityDamage > 0) {
                EntityDamage = Com.EntityHashMap.get(MobId).get(PlayerName) + EntityDamage;
                Com.EntityHashMap.get(MobId).put(PlayerName, EntityDamage);
                Bukkit.getPlayer(PlayerName).sendMessage(EntiyMessage);
            }
        }
        if (!Com.EntityHashMap.get(MobId).containsKey(PlayerName)) {
            if (Bukkit.getPlayer(PlayerName) != null) {
                if (Bukkit.getPlayer(PlayerName).isOnline()) {
                    Com.EntityHashMap.get(MobId).put(PlayerName, Double.parseDouble(df.format(EntityDamage)));
                    Bukkit.getPlayer(PlayerName).sendMessage(EntiyMessage);
                } else {
                    System.out.println(Prefix + Errorstorage + PlayerName);
                }
            }
        }
    }
}

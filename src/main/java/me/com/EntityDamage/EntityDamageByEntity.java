package me.com.EntityDamage;


//import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import me.com.Com;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.helpers.Util;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.sqrt;
import static me.com.command.LoadConfig.*;

public class EntityDamageByEntity implements Listener {
    private Plugin pl = Com.getPlugin();

    public EntityDamageByEntity() {}
    static double value;
    //存储玩家对怪物造成的伤害,存入HashMap中的对应怪物ID的HashMap
    @EventHandler
    public void EntityDamegeEvent(EntityDamageByEntityEvent event) {
        BukkitAPIHelper apiHelper = new BukkitAPIHelper();
        if (apiHelper.isMythicMob(event.getEntity()))//判断被攻击是是否是mm怪物
        {
                if(event.getDamager() instanceof Projectile){
                    Projectile pj = (Projectile) event.getDamager();
                    if(pj.getShooter() instanceof Player){
                        Player player = (Player) pj.getShooter();
                        //Arrow s = (Arrow) event.getDamager();
                        Damage(event,apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName(),player);
                    }
                }
                if(event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING|| event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                        || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
                {
                    Entity player = event.getDamager();
                    if(player instanceof Player){
                        Player player1 = (Player) player;
                        Damage(event,apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName(),player1);
                    }
                }

                if(event.getEntity().getFireTicks() >=1)
                {
                    Entity player = event.getDamager();
                    if(player instanceof Player){
                        Player player1 = (Player) player;
                        Damage(event,apiHelper.getMythicMobInstance(event.getEntity()).getType().getInternalName(),player1);
                    }
                }
        }
    }
    public void Damage(EntityDamageByEntityEvent event, String MobName, Player player){
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        DecimalFormat df = new DecimalFormat(DecimalFormat);
        double entityHealth;
        final double[] entityDamage = {0.0};
        entityHealth = ((LivingEntity) event.getEntity()).getHealth();//在攻击前返回现状,获取被攻击前的血量
        double finalEntityHealth = entityHealth;
        scheduler.scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("DeathRewards"), new Runnable() {
            @Override
            public void run() {
                entityDamage[0] = ((LivingEntity) event.getEntity()).getHealth(); //获取被攻击后的血量然后进行计算,之所以不用getFinal是因为用as属性插件后,对怪物的伤害有点怪怪的,就用比较原始的方法
                value = finalEntityHealth - entityDamage[0];
                double damage = value;
                if (pl.getConfig().contains("Boss." + MobName)) {
                    Entity entity = event.getEntity();
                    String EntityDamageMessage = EntityDamage.replace("{mob}", MobName).replace("{entityDamage}", String.valueOf(df.format(damage)));
                    PutInformation(entity.getEntityId(), player.getName(), damage, EntityDamageMessage);
                }
            }
        }, 2L);
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

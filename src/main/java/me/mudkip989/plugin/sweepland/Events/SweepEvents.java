package me.mudkip989.plugin.sweepland.Events;


import io.papermc.paper.event.player.*;
import me.mudkip989.plugin.sweepland.*;
import me.mudkip989.plugin.sweepland.Actions.*;
import net.kyori.adventure.bossbar.*;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.*;

import java.util.*;

import static me.mudkip989.plugin.sweepland.SweepLand.Scorreboar;
import static me.mudkip989.plugin.sweepland.SweepLand.world;


public class SweepEvents implements Listener {
    @EventHandler
    public void OnClick(PlayerInteractEvent e){
        if(e.getHand() != EquipmentSlot.OFF_HAND && Objects.equals(e.getPlayer().getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot()), SweepLand.wand)) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

                SweepingActions.OpenCell(e);


            } else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK ) {

                SweepingActions.PlaceFlag(e);

            }
        }

    }
    @EventHandler
    public void WorldLoad(WorldLoadEvent e){
        world = Bukkit.getWorld("world");
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.SNOW_ACCUMULATION_HEIGHT, 0);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setSpawnLocation(new Location(world, 0, 0, 0));
        world.setTime(6000);
        Scorreboar = Bukkit.getScoreboardManager().getMainScoreboard();
        SweepLand.Objective = Scorreboar.getObjective("Scores");
        if(SweepLand.Objective == null){
            SweepLand.Objective = Scorreboar.registerNewObjective("Scores", Criteria.DUMMY, Component.text("Score"));
            SweepLand.Objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }



    }
    @EventHandler
    public void BlockFade(BlockFadeEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void BlockGrow(StructureGrowEvent e){e.setCancelled(true);}


    @EventHandler
    public void BreakBlock(BlockBreakEvent e){
        if(e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void OnJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.setGameMode(GameMode.ADVENTURE);
        p.setScoreboard(Scorreboar);
        if(!p.getInventory().contains(SweepLand.wand)){
            p.getInventory().addItem(SweepLand.wand);
        }
        p.setAllowFlight(true);
        p.setFlying(true);

        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 1, false, false));
        p.setResourcePack("https://github.com/mudkip989/mudkipPacks/raw/main/Loosers_Minesweeper.zip", "a75b52db553a8280830845f2f590e596654c38a5");
        BossBar boss = BossBar.bossBar(Component.text(String.valueOf(0)), 1, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_12);


        new BukkitRunnable(){
            @Override
            public void run() {

                p.setScoreboard(Scorreboar);
                int score = Scorreboar.getObjective("Scores").getScore(Bukkit.getOfflinePlayer(p.getUniqueId())).getScore();
                p.showBossBar(boss);
                boss.name(Component.text(String.valueOf(score)));
                if(!Bukkit.getOnlinePlayers().contains(p)){
                    this.cancel();
                }

            }
        }.runTaskTimer(SweepLand.instance, 20, 20);


    }
    @EventHandler
    public void OnHit(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            e.setCancelled(true);
        }

    }
}

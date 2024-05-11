package me.mudkip989.plugin.sweepland.Actions;

import me.mudkip989.plugin.sweepland.*;
import me.mudkip989.plugin.sweepland.DataTypes.*;
import org.bukkit.*;
import org.bukkit.block.data.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.*;
import org.bukkit.util.*;
import org.bukkit.util.Vector;
import org.codehaus.plexus.util.*;
import org.joml.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static me.mudkip989.plugin.sweepland.SweepLand.*;

public class SweepingActions {

    public static void PlaceFlag(PlayerInteractEvent e){
        Location loc = getClickLocation(e.getPlayer());
        if(loc != null) {
            if (isClosedCell(loc)) {
                Location tloc = loc.clone().toBlockLocation();
                SweepLand.FlagQueue.add(new CellOpenData(loc, e.getPlayer().getUniqueId(), Biome.GetBiomeAtLocation(new FastNoiseLite.Vector2((int)tloc.x(), (int)tloc.z()))));
            }
        }

    }

    public static void OpenCell(PlayerInteractEvent e){

        Location loc = getClickLocation(e.getPlayer());

        if(loc != null) {

            Location floc = loc.clone();
            floc.add(0, 1, 0);
            if ((((SweepingActions.isClosedCell(loc) && world.getType(floc) != Material.FIRE_CORAL_FAN)) || SweepLand.Numbers.contains(world.getType(loc)))) {
                Location tloc = loc.clone().toBlockLocation();
                SweepLand.CellQueue.add(new CellOpenData(loc, e.getPlayer().getUniqueId(), Biome.GetBiomeAtLocation(new FastNoiseLite.Vector2((int)tloc.x(), (int)tloc.z()))));
            }
            if(SurfaceBombs.contains(world.getType(loc))){
                Predicate<Player> nearPlayers = player -> (player.getLocation().distance(loc)<10);
                world.spawnParticle(Particle.EXPLOSION_HUGE, loc,1);
                world.playSound(loc.clone().add(0.5, 1.5, 0.5), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                List<Player> affected = Bukkit.getOnlinePlayers().stream().filter(nearPlayers).collect(Collectors.toList());
                for (Player p : affected){
                    Vector v = p.getLocation().toVector().subtract(loc.toVector());
                    v.multiply((5/v.length()));
                    p.setVelocity(p.getVelocity().add(v));
                }
            }
        }

    }


    @Nullable
    public static Location getClickLocation(Player p){

        Vector vectordir = p.getEyeLocation().getDirection();
        double scale = (71 - p.getEyeLocation().getY())/vectordir.getY();
        if(scale > SweepLand.ClickRrange || scale < -SweepLand.ClickRrange){
            return null;
        }
        Location result = p.getEyeLocation().add(vectordir.multiply(scale));
        return result.add(0, -1, 0).toBlockLocation();
    }

    public static Integer scanPattern(List<Vector> pattern, Location loc, World world, List<Material> materials){
        int num = 0;

        for (Vector v : pattern){
            Location l = loc.clone();
            l.add(v);
            if(materials.contains(world.getType(l))) {
                num += materials.indexOf(world.getType(l));
            }
        }


        return num;
    }

    public static boolean isClosedCell(Location loc){
        if(!SurfaceBombs.contains(world.getType(loc)) && !Numbers.contains(world.getType(loc))){
            return true;
        }

        return false;
    }

    public static void AnimateOpenCell(Material m, Location l, World world){
        BlockDisplay block = (BlockDisplay) world.spawnEntity(l, EntityType.BLOCK_DISPLAY);
        block.setBlock(m.createBlockData());
        block.setInterpolationDuration(SweepLand.BlockDisplayInterpolationDuration);
        block.setInterpolationDelay(SweepLand.BlockDisplayInterpolationDelay);

        List<Float> scaleAnim = new ArrayList<>();
        for (int i = 1; i <= 10; ++i){
            scaleAnim.add(((-(4f/10f)*(i*i) + 3f*i)/20f) +1f);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if(scaleAnim.size() != 0) {
                    float scale = scaleAnim.get(0);
                    scaleAnim.remove(0);
                    block.setTransformation(new Transformation(new Vector3f(-(1f/2f)*scale + 0.5f, -(1f/2f)*scale + 0.5f, -(1f/2f)*scale + 0.5f), new Quaternionf(0, 0, 0, 1), new Vector3f(scale, scale, scale), new Quaternionf(0, 0, 0, 1)));
                }else {
                    block.remove();
                    this.cancel();
                }

            }
        }.runTaskTimer(SweepLand.instance, 0, 1);
    }

}

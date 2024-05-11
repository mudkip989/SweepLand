package me.mudkip989.plugin.sweepland.Actions;


import me.mudkip989.plugin.sweepland.*;
import me.mudkip989.plugin.sweepland.DataTypes.*;
import org.bukkit.*;
import org.bukkit.util.*;
import org.bukkit.util.Vector;

import java.util.*;

import static me.mudkip989.plugin.sweepland.SweepLand.*;

public class Logics {
    public static Material NumberNormal(Location loc, Biome biome, List<Vector> pattern){
        int bombres = SweepingActions.scanPattern(pattern, loc, world, Bombs);

        return SweepLand.Numbers.get(bombres);
    }

    public static Material NumberInverted(Location loc, Biome biome, List<Vector> pattern){
        int inc = 1;
        switch(biome.minePlacement){
            case Single -> inc = 1;
            case Double -> inc = 2;
            case Triple -> inc = 3;

        }
        int total = inc * pattern.size();

        int bombres = SweepingActions.scanPattern(pattern, loc, world, Bombs);
        if(bombres == total){
            bombres = 0;
        }else if(bombres == 0){
            bombres = total;
        }
        return SweepLand.Numbers.get(total-bombres);
    }

    public static void ClickNumberNormal(Location loc, Biome biome, List<Vector> pattern, UUID uuid){

        int bombrest = SweepingActions.scanPattern(pattern, loc, world, SurfaceBombs);
        int flagres = SweepingActions.scanPattern(pattern, loc.clone().add(0, 1 , 0), world, Flags);
        flagres += bombrest;

        int num = Numbers.indexOf(SweepLand.world.getType(loc));

        if(flagres == num){
            //Open Adjeceent if Flags and revealed bombs are equal
            for (Vector v : pattern){
                Location tloc = loc.clone();
                tloc.add(v);
                tloc.toBlockLocation();
                Location tfloc = tloc.clone();
                tfloc.add(0, 1, 0);
                if(SweepingActions.isClosedCell(tloc) && world.getType(tfloc).equals(Flags.get(0))){
                    CellQueue.add(new CellOpenData(tloc, uuid, Biome.GetBiomeAtLocation(new FastNoiseLite.Vector2((int)tloc.x(), (int)tloc.z()))));
                }
            }
        }


    }

    public static void ClickNumberInverted(Location loc, Biome biome, List<Vector> pattern, UUID uuid){



    }

}

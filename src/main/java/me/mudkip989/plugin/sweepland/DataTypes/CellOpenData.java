package me.mudkip989.plugin.sweepland.DataTypes;

import org.bukkit.*;

import java.util.*;

public class CellOpenData {

    public Location location;
    public UUID uuid;
    public Biome biome;

    public int streak;

    public CellOpenData(Location loc, UUID uuid, Biome biome, int streak){
        this.location = loc;
        this.uuid = uuid;
        this.biome = biome;
        this.streak = streak;
    }
}

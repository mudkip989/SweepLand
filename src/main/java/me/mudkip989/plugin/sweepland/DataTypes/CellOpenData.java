package me.mudkip989.plugin.sweepland.DataTypes;

import org.bukkit.*;

import java.util.*;

public class CellOpenData {

    public Location location;
    public UUID uuid;
    public Biome biome;

    public CellOpenData(Location loc, UUID uuid, Biome biome){
        this.location = loc;
        this.uuid = uuid;
        this.biome = biome;
    }
}

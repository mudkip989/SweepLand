package me.mudkip989.plugin.sweepland.DataTypes;

import it.unimi.dsi.fastutil.*;
import me.mudkip989.plugin.sweepland.*;
import org.bukkit.*;


public class Biome {
    public Material closedCellMat;
    public Material underBlock;

    public MinePlacement minePlacement;
    public NumberLogic numberLogic;
    public int BombChance;
    public String name;

    public Boolean extraPatterns;

    public int multiplier;

    public Biome(String name, Material mat1, Material mat2, MinePlacement mines, NumberLogic numb, int chance, boolean extraPatterns, int multiplier){
        this.closedCellMat = mat1;
        this.underBlock = mat2;
        this.minePlacement = mines;
        this.numberLogic = numb;
        this.BombChance = chance;
        this.extraPatterns = extraPatterns;
        this.name = name;
        this.multiplier = multiplier;
    }

    public static Biome GetBiomeAtLocation(FastNoiseLite.Vector2 vec, FastNoiseLite n, FastNoiseLite dw){


        dw.DomainWarp(vec);
        float noise = n.GetNoise(vec.x, vec.y) + 1f;
        Biome biome;
        float dif = 2f/SweepLand.BiomeList.size();
        int res = (int) Math.floor((noise/dif));
        biome = SweepLand.BiomeList.get(res);

        return biome;
    }
    public static Biome GetBiomeAtLocation(FastNoiseLite.Vector2 vec){
        FastNoiseLite n = SweepLand.BiomeNoise;
        FastNoiseLite dw = SweepLand.BiomeDomainWarp;

        dw.DomainWarp(vec);
        float noise = n.GetNoise(vec.x, vec.y) + 1f;
        Biome biome;
        float dif = 2f/SweepLand.BiomeList.size();
        int res = (int) Math.floor((noise/dif));
        biome = SweepLand.BiomeList.get(res);

        return biome;
    }



    

}

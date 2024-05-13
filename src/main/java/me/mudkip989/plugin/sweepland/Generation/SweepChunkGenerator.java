package me.mudkip989.plugin.sweepland.Generation;

import me.mudkip989.plugin.sweepland.*;
import me.mudkip989.plugin.sweepland.DataTypes.*;
import org.bukkit.*;
import org.bukkit.generator.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class SweepChunkGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        int seed = (int) worldInfo.getSeed();


        SweepLand.BiomeNoise.SetSeed(seed);
        SweepLand.BiomeDomainWarp.SetSeed(seed);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                int xn = x + (chunkX * 16);
                int zn = z + (chunkZ * 16);
                FastNoiseLite.Vector2 vec = new FastNoiseLite.Vector2(xn, zn);
                Biome biome = Biome.GetBiomeAtLocation(vec, SweepLand.BiomeNoise, SweepLand.BiomeDomainWarp);
                Material bombmat = Material.STONE;
                if (random.nextInt(0, 100) < biome.BombChance) {
                    switch (biome.minePlacement){
                        case Single -> bombmat = SweepLand.Bombs.get(1);
                        case Double -> bombmat = SweepLand.Bombs.get(random.nextInt(1,3));
                        case Triple -> bombmat = SweepLand.Bombs.get(random.nextInt(1,4));
                    }
                }
                chunkData.setBlock(x, 68, z, bombmat);
                chunkData.setBlock(x, 69, z, biome.underBlock);
                chunkData.setBlock(x, 70, z, biome.closedCellMat);
                List<Material> temp = new ArrayList<>(SweepLand.Bombs);
                temp.addAll(List.of(Material.STONE,Material.STONE,Material.STONE,Material.STONE,Material.STONE));
                chunkData.setBlock(x, 67, z, temp.get(random.nextInt(0, temp.size())));
            }
        }
    }

}

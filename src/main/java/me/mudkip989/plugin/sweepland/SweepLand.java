package me.mudkip989.plugin.sweepland;

import me.mudkip989.plugin.sweepland.Actions.*;
import me.mudkip989.plugin.sweepland.DataTypes.*;
import me.mudkip989.plugin.sweepland.DataTypes.Biome;
import me.mudkip989.plugin.sweepland.Events.*;
import me.mudkip989.plugin.sweepland.Generation.*;
import me.mudkip989.plugin.sweepland.Stats.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.*;
import org.bukkit.entity.*;
import org.bukkit.generator.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.*;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

public final class SweepLand extends JavaPlugin {

    public static SweepLand instance;
    public static Logger log = Logger.getLogger("SweepLand");
    public static World world;


    public static List<Material> Bombs = new ArrayList<>(List.of(Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.BLACKSTONE));
    public static List<Material> PatternSaps = new ArrayList<>(List.of(Material.AIR, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING, Material.JUNGLE_SAPLING));
    public static List<Material> Flags = new ArrayList<>(List.of(Material.AIR, Material.FIRE_CORAL_FAN, Material.BUBBLE_CORAL_FAN, Material.TUBE_CORAL_FAN));
    public static List<Material> Numbers = new ArrayList<>(List.of(Material.BARRIER, Material.LIGHT_BLUE_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.CYAN_STAINED_GLASS, Material.GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.YELLOW_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.PURPLE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.WHITE_STAINED_GLASS_PANE, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE, Material.BROWN_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE));
    public static List<Material> SurfaceBombs = new ArrayList<>(List.of(Material.STRUCTURE_VOID, Material.BLACK_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE));

    public static HashMap<Material, List<Vector>> Patterns = new HashMap<>();

    public static List<CellOpenData> CellQueue;
    public static List<CellOpenData> NextCellQueue;
    public static List<CellOpenData> FlagQueue;
    public static List<Vector> FlagQueue2;
    public static ItemStack wand = new ItemStack(Material.BRUSH);

    public static Scoreboard Scorreboar;

    public static Objective Objective;
    public static int BombPunish = 400;
    public static int CellScore = 1;

    public static int CellPopLimit = 10;

    public static int ClickRrange = 10;
    public static int BlockDisplayInterpolationDelay = 1;
    public static int BlockDisplayInterpolationDuration = 1;

    public static int VanillaPercentBomb = 3;
    public static int ChocoPercentBomb = 5;

    public static FastNoiseLite BiomeNoise = new FastNoiseLite();
    public static FastNoiseLite BiomeDomainWarp = new FastNoiseLite();


    public static List<Biome> BiomeList;
    public static boolean SpawnErrored = false;
    public static BukkitScheduler scheduler;


    @Override
    public void onEnable() {
        instance = this;
        scheduler = getServer().getScheduler();
        this.saveDefaultConfig();
        RefreshValues();
        BiomeList = new ArrayList<>();
        CreateBiomes();
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new SweepEvents(), this);
        CellQueue = new ArrayList<>();
        NextCellQueue = new ArrayList<>();
        FlagQueue = new ArrayList<>();
        FlagQueue2 = new ArrayList<>();
        this.getCommand("sweepland").setExecutor(new Commands());
        SetNoise();

        // Plugin startup logic
        new BukkitRunnable() {
            @Override
            public void run(){

                List<CellOpenData> temp = new ArrayList<>(CellQueue);
                CellQueue = NextCellQueue;
                NextCellQueue = new ArrayList<>();
                List<CellOpenData> temp2 = new ArrayList<>(FlagQueue);
                FlagQueue = new ArrayList<>();
                List<Vector> hmm = new ArrayList<>();
                int counter = 0;
                for (CellOpenData cel : temp){
                    if(hmm.contains(cel.location.toVector())){
                        continue;
                    }
                    counter++;
                    hmm.add(cel.location.toVector());
                    if(counter > CellPopLimit){
                        CellQueue.add(cel);
                        continue;
                    }
                    Location loc = cel.location.clone();

                    loc.setPitch(0);
                    loc.setYaw(0);
                    Location bloc = loc.clone();
                    bloc.add(0, -2, 0);
                    Location floc = loc.clone();
                    floc.add(0, 1, 0);
                    List<Vector> pattern = CellCheck.normal();
                    if(SweepingActions.isClosedCell(loc) && Flags.indexOf(world.getType(floc)) <= 0) {
                        //Closed Cell, No Flag
                        SweepingActions.AnimateOpenCell(SweepLand.world.getType(loc), loc, world);
                        if (world.getType(bloc).equals(Bombs.get(0))) {
                            //Is not Bomb
                            if(cel.biome.extraPatterns) {
                                pattern = Patterns.get(PatternSaps.get(Bombs.indexOf(world.getType(bloc.clone().add(0, -1, 0)))));
                            }

                            world.playSound(floc, Sound.ENTITY_ITEM_PICKUP, 10, (-(3f/(2f*((cel.streak/20f)+1)))+2));
                            int bombres = SweepingActions.scanPattern(pattern, bloc, world, Bombs);
                            Material res;

                            switch(cel.biome.numberLogic){
                                case Normal -> res = Logics.NumberNormal(bloc, cel.biome, pattern);
                                case Inverted -> res = Logics.NumberInverted(bloc, cel.biome, pattern);
                                default -> res = Logics.NumberNormal(bloc, cel.biome, pattern);

                            }

                            world.getBlockAt(loc).setType(res, false);
                            if(cel.biome.extraPatterns){
                                Material sap = PatternSaps.get(Bombs.indexOf(world.getType(bloc.clone().add(0, -1, 0))));
                                world.getBlockAt(loc.clone().add(0, 1, 0)).setType(sap, false);
                            }else{
                                world.getBlockAt(loc.clone().add(0, 1, 0)).setType(Material.AIR, false);
                            }


                            Score score = Objective.getScore(Bukkit.getOfflinePlayer(cel.uuid));
                            score.setScore(score.getScore()+(CellScore * cel.biome.multiplier));
                            if (bombres == 0) {
                                //Auto Open 0
                                for (Vector v : pattern) {
                                    Location tnloc = loc.clone();
                                    tnloc.add(v);
                                    Location tfloc = tnloc.clone();
                                    tfloc.add(0, 3, 0);
                                    if (SweepingActions.isClosedCell(tnloc) && SweepLand.world.getType(tfloc) != Material.FIRE_CORAL_FAN) {
                                        Location tloc = tnloc.clone();
                                        CellQueue.add(new CellOpenData(tnloc, cel.uuid, Biome.GetBiomeAtLocation(new FastNoiseLite.Vector2((int)tloc.x(), (int)tloc.z())), cel.streak + 1));
                                    }


                                }
                            }
                        } else {
                            //Is Bomb
                            world.getBlockAt(loc).setType(SurfaceBombs.get(Bombs.indexOf(world.getType(bloc))), false);
                            Score score = Objective.getScore(Bukkit.getOfflinePlayer(cel.uuid));
                            score.setScore(score.getScore()-(cel.biome.multiplier*BombPunish*Bombs.indexOf(world.getType(bloc))));
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
                    }else if (Numbers.contains(SweepLand.world.getType(loc))){
                        //Is Number Clicked
                        List<Material> fest = new ArrayList<>(Flags);
                        pattern = Patterns.get(world.getType(floc));
                        switch(cel.biome.numberLogic){
                            case Normal -> Logics.ClickNumberNormal(loc, cel.biome, pattern, cel.uuid);
                            case Inverted -> Logics.ClickNumberInverted(loc, cel.biome, pattern, cel.uuid);
                            default -> Logics.NumberNormal(loc, cel.biome, pattern);

                        }


                    }
                }
                for (CellOpenData cel2 : temp2) {
                    Location loc = cel2.location;
                    Location floc = loc.clone();
                    floc.add(0, 1, 0);

                    if (Flags.contains(SweepLand.world.getType(floc))) {
                        int index = Flags.indexOf(SweepLand.world.getType(floc));
                        index++;

                        switch (cel2.biome.minePlacement){
                            case Single -> {if(index > 1) index = 0;}
                            case Double -> {if(index > 2) index = 0;}
                            case Triple -> {if(index > 3) index = 0;}
                        }
                        world.getBlockAt(floc).setType(Flags.get(index), false);
                        if(!Flags.get(index).equals(Material.AIR)) {
                            Waterlogged wl = ((Waterlogged) SweepLand.world.getBlockAt(floc).getBlockData());
                            wl.setWaterlogged(false);

                            SweepLand.world.getBlockAt(floc).setBlockData(wl, false);
                        }
                        Bukkit.getPlayer(cel2.uuid).playSound(loc.clone().add(0.5, 1.5, 0.5), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                    }


                }


            }
        }.runTaskTimer(SweepLand.instance, 1 ,1);

    }

    public static void RefreshValues(){
        SweepLand.instance.reloadConfig();
        BombPunish = SweepLand.instance.getConfig().getInt("Scoring.MineRevealPunishment");
        CellScore = SweepLand.instance.getConfig().getInt("Scoring.CellRevealReward");
        CellPopLimit = SweepLand.instance.getConfig().getInt("Performance.CellPopLimit");
        ClickRrange = SweepLand.instance.getConfig().getInt("Performance.ClickRange");
        BlockDisplayInterpolationDuration = SweepLand.instance.getConfig().getInt("Visual.BlockDisplayInterpolationDuration");
        BlockDisplayInterpolationDelay = SweepLand.instance.getConfig().getInt("Visual.BlockDisplayInterpolationDelay");
        VanillaPercentBomb = SweepLand.instance.getConfig().getInt("Generation.VanillaBombChance");
        ChocoPercentBomb = SweepLand.instance.getConfig().getInt("Generation.ChocoBombChance");


    }

    private static void CreateBiomes(){
        BiomeList.add(new Biome("Vanilla", Material.WHITE_WOOL, Material.WHITE_TERRACOTTA, MinePlacement.Single, NumberLogic.Normal, 19, false, 1));
        BiomeList.add(new Biome("Chocolate", Material.BROWN_WOOL, Material.BROWN_TERRACOTTA, MinePlacement.Single, NumberLogic.Normal, 31, false, 2));
        BiomeList.add(new Biome("Mint", Material.GREEN_WOOL, Material.GREEN_TERRACOTTA, MinePlacement.Single, NumberLogic.Normal, 19, true, 2));
        BiomeList.add(new Biome("BlueBerry", Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_TERRACOTTA, MinePlacement.Triple, NumberLogic.Normal, 19, false,3));
        BiomeList.add(new Biome("Water", Material.CYAN_WOOL, Material.CYAN_TERRACOTTA, MinePlacement.Double, NumberLogic.Normal, 19, false, 2));
        BiomeList.add(new Biome("Chaos", Material.TNT, Material.BLACK_CONCRETE_POWDER, MinePlacement.Triple, NumberLogic.Normal, 25, true, 10));



        ArrayList<Vector> pattern = new ArrayList<>();
        pattern.add(new Vector(1, 0, 0));
        pattern.add(new Vector(1, 0, 1));
        pattern.add(new Vector(0, 0, 1));
        pattern.add(new Vector(-1, 0, 1));
        pattern.add(new Vector(-1, 0, 0));
        pattern.add(new Vector(-1, 0, -1));
        pattern.add(new Vector(0, 0, -1));
        pattern.add(new Vector(1, 0, -1));
        Patterns.put(Material.AIR, pattern);
        pattern = new ArrayList<>();
        pattern.add(new Vector(1, 0, 0));
        pattern.add(new Vector(2, 0, 1));
        pattern.add(new Vector(1, 0, 1));
        pattern.add(new Vector(0, 0, 1));
        pattern.add(new Vector(-1, 0, 1));
        pattern.add(new Vector(-2, 0, 1));
        pattern.add(new Vector(-1, 0, 0));
        pattern.add(new Vector(0, 0, -1));
        Patterns.put(Material.OAK_SAPLING, pattern);
        pattern = new ArrayList<>();
        pattern.add(new Vector(1, 0, 0));
        pattern.add(new Vector(2, 0, 0));
        pattern.add(new Vector(0, 0, 1));
        pattern.add(new Vector(0, 0, 2));
        pattern.add(new Vector(-1, 0, 0));
        pattern.add(new Vector(-2, 0, 0));
        pattern.add(new Vector(0, 0, -1));
        pattern.add(new Vector(0, 0, -2));
        Patterns.put(Material.SPRUCE_SAPLING, pattern);
        pattern = new ArrayList<>();
        pattern.add(new Vector(1, 0, 0));
        pattern.add(new Vector(1, 0, 1));
        pattern.add(new Vector(-1, 0, 1));
        pattern.add(new Vector(-1, 0, 0));
        pattern.add(new Vector(-1, 0, -1));
        pattern.add(new Vector(1, 0, -1));
        Patterns.put(Material.BIRCH_SAPLING, pattern);
        pattern = new ArrayList<>();
        pattern.add(new Vector(1, 0, 1));
        pattern.add(new Vector(0, 0, 1));
        pattern.add(new Vector(-1, 0, 1));
        pattern.add(new Vector(-1, 0, -1));
        pattern.add(new Vector(0, 0, -1));
        pattern.add(new Vector(1, 0, -1));
        Patterns.put(Material.JUNGLE_SAPLING, pattern);

    }

    private static void SetNoise() {

        BiomeNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        BiomeNoise.SetFrequency(0.01f);
        BiomeNoise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq);
        BiomeNoise.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);
        BiomeNoise.SetCellularJitter(1.25f);
        BiomeDomainWarp.SetFractalType(FastNoiseLite.FractalType.DomainWarpIndependent);
        BiomeDomainWarp.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        BiomeDomainWarp.SetDomainWarpAmp(30f);
        BiomeDomainWarp.SetFrequency(0.02f);
        BiomeDomainWarp.SetFractalOctaves(9);
        BiomeDomainWarp.SetFractalLacunarity(1f);
        BiomeDomainWarp.SetFractalGain(25f);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return new SweepChunkGenerator();
    }
}

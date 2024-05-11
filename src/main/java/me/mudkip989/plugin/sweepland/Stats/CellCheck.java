package me.mudkip989.plugin.sweepland.Stats;


import org.bukkit.util.*;
import org.bukkit.util.Vector;

import java.util.*;

public class CellCheck {
    public static ArrayList<Vector> normal(){
        ArrayList<Vector> pattern = new ArrayList<>();
        pattern.add(new Vector(1, 0, 0));
        pattern.add(new Vector(1, 0, 1));
        pattern.add(new Vector(0, 0, 1));
        pattern.add(new Vector(-1, 0, 1));
        pattern.add(new Vector(-1, 0, 0));
        pattern.add(new Vector(-1, 0, -1));
        pattern.add(new Vector(0, 0, -1));
        pattern.add(new Vector(1, 0, -1));

        return pattern;
    }
}

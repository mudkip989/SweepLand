package me.mudkip989.plugin.sweepland.Events;

import me.mudkip989.plugin.sweepland.*;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.*;
import net.kyori.adventure.util.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.permissions.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && !sender.isOp()) {
            return false;
        }
        System.out.println(args.length);
        System.out.println(args[0]);
        if(args.length == 1) {

            switch (args[0]) {
                case "refresh" -> {
                    SweepLand.RefreshValues();
                    SweepLand.world.sendMessage(Component.text("Config has been Reloaded").color(TextColor.color(1f, 1f, 0f)).append(Component.text("\nCell Score: " + SweepLand.CellScore + "\nBomb Punishment: " + SweepLand.BombPunish + "\nCell Pop Limit: " + SweepLand.CellPopLimit).color(TextColor.color(1f, 0.5f, 0f))));
                }
                default -> {}
            }
        }

        return true;
    }
}

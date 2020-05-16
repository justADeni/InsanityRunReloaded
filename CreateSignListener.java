import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import java.util.Iterator;
import org.bukkit.event.Listener;

public class CreateSignListener implements Listener
{
    public static String getArena(final String original) {
        for (final String s : InsanityRun.arenaList) {
            if (s.toLowerCase().equalsIgnoreCase(original)) {
                return s;
            }
        }
        return null;
    }
    
    private static void set(final SignChangeEvent event, final Player p) {
        final String prefix = event.getLine(0);
        if (!prefix.equalsIgnoreCase("[insanityrun]") && !prefix.equalsIgnoreCase("[irun]")) {
            return;
        }
        if (!p.hasPermission("insanityrun.sign")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".noSignPerms")));
            return;
        }
        final String part = event.getLine(1);
        final String arena = event.getLine(2);
        if (arena.equalsIgnoreCase("")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".noSignArenaName")));
            return;
        }
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arena) + ".world") == null) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".noArena").replace("%arena%", arena)));
            return;
        }
        int inArenaCount = 0;
        if (part.equalsIgnoreCase("join")) {
            if (InsanityRun.playersInThisArena.get(arena) != null) {
                inArenaCount = InsanityRun.playersInThisArena.get(arena);
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.1") != null) {
                event.setLine(0, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.1").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.2") != null) {
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.2").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.3") != null) {
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.3").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.4") != null) {
                event.setLine(3, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.JoinGame.4").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            final int amount = InsanityRun.plugin.getConfig().getInt("signs.JoinGame." + arena + ".amount") + 1;
            InsanityRun.plugin.getConfig().set("signs.JoinGame." + arena + ".amount", (Object)amount);
            InsanityRun.plugin.getConfig().set("signs.JoinGame." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs.JoinGame." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs.JoinGame." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs.JoinGame." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.saveConfig();
        }
        if (part.equalsIgnoreCase("leave")) {
            if (InsanityRun.playersInThisArena.get(arena) != null) {
                inArenaCount = InsanityRun.playersInThisArena.get(arena);
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.1") != null) {
                event.setLine(0, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.1").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.2") != null) {
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.2").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.3") != null) {
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.3").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.4") != null) {
                event.setLine(3, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.LeaveGame.4").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            final int amount = InsanityRun.plugin.getConfig().getInt("signs.LeaveGame." + arena + ".amount") + 1;
            InsanityRun.plugin.getConfig().set("signs.LeaveGame." + arena + ".amount", (Object)amount);
            InsanityRun.plugin.getConfig().set("signs.LeaveGame." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs.LeaveGame." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs.LeaveGame." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs.LeaveGame." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.saveConfig();
        }
        if (part.equalsIgnoreCase("info")) {
            if (InsanityRun.playersInThisArena.get(arena) != null) {
                inArenaCount = InsanityRun.playersInThisArena.get(arena);
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.1") != null) {
                event.setLine(0, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.1").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.2") != null) {
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.2").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.3") != null) {
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.3").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.4") != null) {
                event.setLine(3, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.ArenaInfo.4").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            final int amount = InsanityRun.plugin.getConfig().getInt("signs.ArenaInfo." + arena + ".amount") + 1;
            InsanityRun.plugin.getConfig().set("signs.ArenaInfo." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs.ArenaInfo." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs.ArenaInfo." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs.ArenaInfo." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.saveConfig();
        }
        if (part.equalsIgnoreCase("fastest") || part.equalsIgnoreCase("top")) {
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.1.name", (Object)"Nobody");
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.2.name", (Object)"Nobody");
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.3.name", (Object)"Nobody");
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.4.name", (Object)"Nobody");
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.5.name", (Object)"Nobody");
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.1.time", (Object)1500000);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.2.time", (Object)1600000);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.3.time", (Object)1700000);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.4.time", (Object)1800000);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.5.time", (Object)1900000);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.1.coins", (Object)0);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.2.coins", (Object)0);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.3.coins", (Object)0);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.4.coins", (Object)0);
            InsanityRun.plugin.getConfig().set(String.valueOf(arena) + ".fastest.5.coins", (Object)0);
            if (InsanityRun.playersInThisArena.get(arena) != null) {
                inArenaCount = InsanityRun.playersInThisArena.get(arena);
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.1") != null) {
                event.setLine(0, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.1").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.2") != null) {
                event.setLine(1, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.2").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.3") != null) {
                event.setLine(2, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.3").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            if (InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.4") != null) {
                event.setLine(3, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats.FastestRun.4").replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%arena%", arena)));
            }
            final int amount = InsanityRun.plugin.getConfig().getInt("signs.FastestRun." + arena + ".amount") + 1;
            InsanityRun.plugin.getConfig().set("signs.FastestRun." + arena + ".amount", (Object)amount);
            InsanityRun.plugin.getConfig().set("signs.FastestRun." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs.FastestRun." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs.FastestRun." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs.FastestRun." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".x." + amount, (Object)event.getBlock().getX());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".y." + amount, (Object)event.getBlock().getY());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".z." + amount, (Object)event.getBlock().getZ());
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".world." + amount, (Object)event.getBlock().getWorld().getName());
            InsanityRun.plugin.saveConfig();
        }
    }
    
    @EventHandler
    public void sign(final SignChangeEvent event) {
        final Player player = event.getPlayer();
        set(event, player);
    }
}

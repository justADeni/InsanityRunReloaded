import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.World;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import org.bukkit.Location;
import java.util.List;
import java.util.HashMap;
import org.bukkit.block.Sign;
import java.util.Iterator;

public class SignManager
{
    static String formatIntoHHMMSS(final Long millisecs) {
        final int secs = (int)(millisecs / 1000L);
        final int remainder = secs % 3600;
        final int minutes = remainder / 60;
        final int seconds;
        return String.valueOf(minutes) + ":" + (((seconds = remainder % 60) < 10) ? "0" : "") + seconds;
    }
    
    public static String getArenaFromString(final String string) {
        if (InsanityRun.plugin.getConfig().getString("signs") != null) {
            for (final String s : InsanityRun.plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
                for (final String ss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s).getKeys(false)) {
                    if (InsanityRun.plugin.getConfig().getString(String.valueOf(ss) + ".world") != null && ss.toLowerCase().equalsIgnoreCase(ss)) {
                        return ss;
                    }
                }
            }
        }
        return null;
    }
    
    public static void removeSign(final Sign sign) {
        final HashMap<String, String> ad = new HashMap<String, String>();
        if (isPluginSign(sign)) {
            for (final String s : InsanityRun.plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
                final String p = "signs." + s + "." + getSignRealArena(sign);
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".x").getKeys(false)) {
                    if (sign.getX() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".x." + d)) {
                        ad.put(s, d);
                    }
                }
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".y").getKeys(false)) {
                    if (sign.getY() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".y." + d)) {
                        ad.put(s, d);
                    }
                }
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".z").getKeys(false)) {
                    if (sign.getY() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".z." + d)) {
                        ad.put(s, d);
                    }
                }
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".world").getKeys(false)) {
                    if (sign.getWorld().getName().equals(InsanityRun.plugin.getConfig().getString(String.valueOf(p) + ".world." + d))) {
                        ad.put(s, d);
                    }
                }
            }
        }
        for (final String d2 : ad.keySet()) {
            final String arena = getSignRealArena(sign);
            InsanityRun.plugin.getConfig().set("signs." + d2 + "." + arena + ".y." + ad.get(d2), (Object)null);
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".y." + ad.get(d2), (Object)null);
            InsanityRun.plugin.getConfig().set("signs." + d2 + "." + arena + ".x." + ad.get(d2), (Object)null);
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".x." + ad.get(d2), (Object)null);
            InsanityRun.plugin.getConfig().set("signs." + d2 + "." + arena + ".z." + ad.get(d2), (Object)null);
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".z." + ad.get(d2), (Object)null);
            InsanityRun.plugin.getConfig().set("signs." + d2 + "." + arena + ".world." + ad.get(d2), (Object)null);
            InsanityRun.plugin.getConfig().set("signs-ByName." + arena + ".world." + ad.get(d2), (Object)null);
        }
        InsanityRun.plugin.saveConfig();
    }
    
    public static List<Location> getSignWithName(String arena) {
        final List<Location> list = new ArrayList<Location>();
        if (InsanityRun.plugin.getConfig().getString("signs") != null) {
            for (final String s : InsanityRun.plugin.getConfig().getConfigurationSection("signs-ByName").getKeys(false)) {
                arena = getArenaFromString(s);
                if (arena != null) {
                    int count = 1;
                    for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection("signs-ByName." + arena + ".x").getKeys(false)) {
                        final int x = InsanityRun.plugin.getConfig().getInt("signs-ByName." + arena + ".x." + count);
                        final int y = InsanityRun.plugin.getConfig().getInt("signs-ByName." + arena + ".y." + count);
                        final int z = InsanityRun.plugin.getConfig().getInt("signs-ByName." + arena + ".z." + count);
                        final World w = Bukkit.getWorld(InsanityRun.plugin.getConfig().getString("signs-ByName." + arena + ".world." + count));
                        final Location loc = new Location(w, (double)x, (double)y, (double)z);
                        ++count;
                        if (loc != null) {
                            list.add(loc);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public static void updateSign(final String arena) {
        if (!getSignWithName(arena).isEmpty()) {
            for (final Location loc : getSignWithName(arena)) {
                updateSign(loc.getBlock());
            }
        }
    }
    
    public static void updateSign(final Block block) {
        if (block.getState() instanceof Sign) {
            final Sign sign = (Sign)block.getState();
            if (isPluginSign(sign)) {
                SignType types = null;
                switch (getSignType(sign)) {
                    case JOIN: {
                        types = SignType.JOIN;
                        break;
                    }
                    case LEAVE: {
                        types = SignType.LEAVE;
                        break;
                    }
                    case TOP: {
                        types = SignType.TOP;
                        break;
                    }
                    case INFO: {
                        types = SignType.INFO;
                        break;
                    }
                }
                String status = "";
                String stat = "";
                if (types.toString().equals("JOIN")) {
                    status = "JoinGame";
                    stat = "Join";
                }
                if (types.toString().equals("LEAVE")) {
                    status = "LeaveGame";
                    stat = "Leave";
                }
                if (types.toString().equals("TOP")) {
                    status = "FastestRun";
                    stat = "Top";
                }
                if (types.toString().equals("INFO")) {
                    status = "ArenaInfo";
                    stat = "Info";
                }
                final int inArenaCount = InsanityRun.playersInThisArena.get(getSignRealArena(sign));
                for (final String s : InsanityRun.plugin.getConfig().getConfigurationSection("SignsFormats." + status).getKeys(false)) {
                    sign.setLine(GameManager.getInt(s) - 1, ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString("SignsFormats." + status + "." + s).replace("%arena%", getSignRealArena(sign)).replace("%playing%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%players%", new StringBuilder(String.valueOf(inArenaCount)).toString()).replace("%status%", stat)));
                }
                sign.update();
            }
        }
    }
    
    public static boolean isPluginSign(final Sign sign) {
        final HashMap<String, String> ad = new HashMap<String, String>();
        if (InsanityRun.plugin.getConfig().getString("signs") != null) {
            for (final String s : InsanityRun.plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
                for (final String ss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s).getKeys(false)) {
                    final String p = "signs." + s + "." + ss;
                    for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".x").getKeys(false)) {
                        if (sign.getX() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".x." + d)) {
                            ad.put(s, d);
                        }
                    }
                    for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".y").getKeys(false)) {
                        if (sign.getY() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".y." + d)) {
                            ad.put(s, d);
                        }
                    }
                    for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".z").getKeys(false)) {
                        if (sign.getY() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".z." + d)) {
                            ad.put(s, d);
                        }
                    }
                    for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".world").getKeys(false)) {
                        if (sign.getWorld().getName().equals(InsanityRun.plugin.getConfig().getString(String.valueOf(p) + ".world." + d))) {
                            ad.put(s, d);
                        }
                    }
                    for (final String dw : ad.keySet()) {
                        final String amount = ad.get(dw);
                        boolean x = false;
                        boolean y = false;
                        boolean z = false;
                        boolean w = false;
                        final int x2 = InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".x." + amount);
                        final int y2 = InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".y." + amount);
                        final int z2 = InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".z." + amount);
                        final String w2 = InsanityRun.plugin.getConfig().getString(String.valueOf(p) + ".world." + amount);
                        if (x2 == sign.getX()) {
                            x = true;
                        }
                        if (y2 == sign.getY()) {
                            y = true;
                        }
                        if (z2 == sign.getZ()) {
                            z = true;
                        }
                        if (w2.equals(sign.getWorld().getName())) {
                            w = true;
                        }
                        if (x && y && z && w) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static SignType getSignType(final Sign sign) {
        SignType type = null;
        final HashMap<String, String> ad = new HashMap<String, String>();
        if (isPluginSign(sign) && InsanityRun.plugin.getConfig().getString("signs") != null) {
            for (String s : InsanityRun.plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
                final String p = "signs." + s + "." + getSignRealArena(sign);
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".x").getKeys(false)) {
                    if (sign.getX() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".x." + d)) {
                        ad.put(s, d);
                    }
                }
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".y").getKeys(false)) {
                    if (sign.getY() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".y." + d)) {
                        ad.put(s, d);
                    }
                }
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".z").getKeys(false)) {
                    if (sign.getY() == InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".z." + d)) {
                        ad.put(s, d);
                    }
                }
                for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection(String.valueOf(p) + ".world").getKeys(false)) {
                    if (sign.getWorld().getName().equals(InsanityRun.plugin.getConfig().getString(String.valueOf(p) + ".world." + d))) {
                        ad.put(s, d);
                    }
                }
                for (final String a : ad.keySet()) {
                    final String amount = ad.get(a);
                    boolean x = false;
                    boolean y = false;
                    boolean z = false;
                    boolean w = false;
                    final int x2 = InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".x." + amount);
                    final int y2 = InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".y." + amount);
                    final int z2 = InsanityRun.plugin.getConfig().getInt(String.valueOf(p) + ".z." + amount);
                    final String w2 = InsanityRun.plugin.getConfig().getString(String.valueOf(p) + ".world." + amount);
                    if (x2 == sign.getX()) {
                        x = true;
                    }
                    if (y2 == sign.getY()) {
                        y = true;
                    }
                    if (z2 == sign.getZ()) {
                        z = true;
                    }
                    if (w2.equals(sign.getWorld().getName())) {
                        w = true;
                    }
                    if (x && y && z && w) {
                        if (s.equalsIgnoreCase("JoinGame")) {
                            s = "JOIN";
                        }
                        if (s.equalsIgnoreCase("LeaveGame")) {
                            s = "LEAVE";
                        }
                        if (s.equalsIgnoreCase("ArenaInfo")) {
                            s = "INFO";
                        }
                        if (s.equalsIgnoreCase("FastestRun")) {
                            s = "TOP";
                        }
                        type = SignType.valueOf(s);
                    }
                }
            }
        }
        return type;
    }
    
    public static String getSignArena(final Sign sign) {
        String arena = null;
        if (InsanityRun.plugin.getConfig().getString("signs") != null) {
            for (final String s : InsanityRun.plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
                for (final String ss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s).getKeys(false)) {
                    int path = 0;
                    boolean x = false;
                    boolean y = false;
                    boolean z = false;
                    boolean w = false;
                    for (final String sss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s + "." + ss + ".world").getKeys(false)) {
                        if (InsanityRun.plugin.getConfig().getString("signs." + s + "." + ss + ".world." + sss).equals(sign.getLocation().getWorld().getName())) {
                            path = GameManager.getInt(sss);
                            w = true;
                        }
                    }
                    for (final String sss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s + "." + ss + ".x").getKeys(false)) {
                        if (InsanityRun.plugin.getConfig().getInt("signs." + s + "." + ss + ".x." + sss) == sign.getX()) {
                            path = GameManager.getInt(sss);
                            x = true;
                        }
                    }
                    for (final String sss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s + "." + ss + ".y").getKeys(false)) {
                        if (InsanityRun.plugin.getConfig().getInt("signs." + s + "." + ss + ".y." + sss) == sign.getY()) {
                            path = GameManager.getInt(sss);
                            y = true;
                        }
                    }
                    for (final String sss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s + "." + ss + ".z").getKeys(false)) {
                        if (InsanityRun.plugin.getConfig().getInt("signs." + s + "." + ss + ".z." + sss) == sign.getZ()) {
                            path = GameManager.getInt(sss);
                            z = true;
                        }
                    }
                    if (x && y && z && w) {
                        arena = InsanityRun.plugin.getConfig().getString("signs." + s + "." + ss + ".world." + path);
                    }
                }
            }
        }
        return arena;
    }
    
    public static String getSignRealArena(final Sign sign) {
        String arena = null;
        if (InsanityRun.plugin.getConfig().getString("signs") != null) {
            for (final String s : InsanityRun.plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
                for (final String ss : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s).getKeys(false)) {
                    for (final String d : InsanityRun.plugin.getConfig().getConfigurationSection("signs." + s + "." + ss + ".x").getKeys(false)) {
                        if (InsanityRun.plugin.getConfig().getInt("signs." + s + "." + ss + ".x." + d) == sign.getX()) {
                            arena = ss;
                        }
                    }
                }
            }
        }
        return arena;
    }
    
    public static void processAction(final Block clickedBlock, final Player p) {
        if (clickedBlock.getType() != Material.SIGN && clickedBlock.getType() != Material.LEGACY_SIGN_POST && clickedBlock.getType() != Material.WALL_SIGN) {
            return;
        }
        if (clickedBlock.getState() instanceof Sign) {
            final Sign sign = (Sign)clickedBlock.getState();
            final String arenaName = getSignRealArena(sign);
            if (isPluginSign(sign)) {
                switch (getSignType(sign)) {
                    case JOIN: {
                        Bukkit.dispatchCommand((CommandSender)p, "irun join " + arenaName);
                        break;
                    }
                    case LEAVE: {
                        Bukkit.dispatchCommand((CommandSender)p, "irun leave " + arenaName);
                        break;
                    }
                    case TOP: {
                        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null || Objects.equals(InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world"), "")) {
                            p.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".noArena"));
                            return;
                        }
                        p.sendMessage(ChatColor.YELLOW + "[Insanity Run] " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".fastestRunsText"));
                        for (int i = 1; i < 6; ++i) {
                            p.sendMessage(String.valueOf(String.valueOf(ChatColor.GREEN)) + i + ". " + formatIntoHHMMSS(InsanityRun.plugin.getConfig().getLong(String.valueOf(arenaName) + ".fastest." + i + ".time")) + " - " + InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".fastest." + i + ".name") + " (" + InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".fastest." + i + ".coins") + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".gameCurrency") + ")");
                        }
                        break;
                    }
                    case INFO: {
                        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null || Objects.equals(InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world"), "")) {
                            p.sendMessage(ChatColor.RED + arenaName + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".noArena"));
                            return;
                        }
                        p.sendMessage(ChatColor.YELLOW + "[Insanity Run]");
                        p.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".arenaNameText") + ChatColor.WHITE + arenaName);
                        p.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".arenaChargeText") + ChatColor.WHITE + InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".charge"));
                        p.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".arenaPayText") + ChatColor.WHITE + InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".pay"));
                        if (InsanityRun.playersInThisArena.get(arenaName) != null) {
                            p.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".playersInArena") + ChatColor.WHITE + InsanityRun.playersInThisArena.get(arenaName));
                        }
                        else {
                            p.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".playersInArena") + ChatColor.WHITE + "0");
                        }
                        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link") != null) {
                            p.sendMessage(ChatColor.GREEN + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".arenaLinkText") + ChatColor.WHITE + InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link"));
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }
}

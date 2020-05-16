import org.bukkit.potion.PotionEffect;
import org.bukkit.World;
import org.bukkit.Location;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.GameMode;
import org.bukkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.OfflinePlayer;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

public class GameManager
{
    static FileConfiguration config;
    static FileConfiguration sc;
    
    static {
        GameManager.config = InsanityRun.plugin.getConfig();
        GameManager.sc = InsanityRun.scFile;
    }
    
    public static void showHelp(final Player player) {
        CommandManager.msg("help1", (CommandSender)player);
        for (int i = 3; i < 4; ++i) {
            CommandManager.msg("help" + i, (CommandSender)player);
        }
        if (player.hasPermission("insanityrun.admin")) {
            for (int i = 4; i < 14; ++i) {
                CommandManager.msg("help" + i, (CommandSender)player);
            }
        }
    }
    
    public static void setScoreboard(final Player p) {
        final Scoreboard board = p.getServer().getScoreboardManager().getNewScoreboard();
        final Objective objective = board.registerNewObjective("a", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', InsanityRun.scFile.getString("Name")));
        List<String> list = (List<String>)GameManager.sc.getStringList("Lines");
        int test = list.size();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            list = (List<String>)PlaceholderAPI.setPlaceholders(p, (List)list);
        }
        for (String s : list) {
            --test;
            final int inArenaCount = InsanityRun.playersInThisArena.get(InsanityRun.playerObject.get(p.getName()).getInArena());
            final int coins = InsanityRun.playerObject.get(p.getName()).getCoins();
            final long runTime = (System.currentTimeMillis() - InsanityRun.playerObject.get(p.getName()).getStartRaceTime()) / 1000L;
            s = s.replaceAll("%money%", new StringBuilder(String.valueOf(InsanityRun.economy.getBalance((OfflinePlayer)p))).toString());
            s = s.replaceAll("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
            s = s.replaceAll("%max_players%", String.valueOf(Bukkit.getMaxPlayers()));
            s = s.replaceAll("%coins%", String.valueOf(coins));
            s = s.replaceAll("%time%", String.valueOf(runTime));
            s = s.replaceAll("%ingame%", String.valueOf(runTime));
            s = s.replaceAll("%playing%", String.valueOf(inArenaCount));
            objective.getScore(ChatColor.translateAlternateColorCodes('&', s)).setScore(test);
        }
        p.setScoreboard(board);
    }
    
    public static List<String> arenas() {
        final ArrayList<String> l = new ArrayList<String>();
        for (final String s : InsanityRun.arenaList) {
            l.add(s);
        }
        return l;
    }
    
    public static void joinGame(final Player player, final String arenaName) {
        final String playerName = player.getName();
        if (InsanityRun.playerObject.get(playerName) != null && InsanityRun.playerObject.get(playerName).getInGame()) {
            return;
        }
        if (!player.hasPermission("insanityrun.join")) {
            CommandManager.perms("join", (CommandSender)player);
            return;
        }
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null || InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world").equals("")) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return;
        }
        if (InsanityRun.useVault) {
            if (!InsanityRun.economy.has((OfflinePlayer)player, (double)InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".charge"))) {
                CommandManager.msgNormal(String.valueOf(InsanityRun.plugin.getConfig().getString(new StringBuilder(String.valueOf(InsanityRun.useLanguage)).append(".notEnoughMoneyText").toString()).replace("%cost%", new StringBuilder().append(InsanityRun.plugin.getConfig().getInt(new StringBuilder(String.valueOf(arenaName)).append(".charge").toString())).toString())) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".payCurrency"), (CommandSender)player);
            }
            final EconomyResponse r = InsanityRun.economy.withdrawPlayer((OfflinePlayer)player, (double)InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".charge"));
            if (!r.transactionSuccess()) {
                CommandManager.msgNormal(String.format("An error occured: %s", r.errorMessage), (CommandSender)player);
            }
        }
        CommandManager.msg("readyToPlay", (CommandSender)player);
        final iPlayer newPlayerObject = new iPlayer();
        newPlayerObject.setPlayerName(playerName);
        newPlayerObject.setCoins(0);
        newPlayerObject.setInGame(true);
        newPlayerObject.setInArena(arenaName);
        newPlayerObject.setFrozen(false);
        newPlayerObject.setSignClickLoc(player.getLocation());
        InsanityRun.playerObject.put(playerName, newPlayerObject);
        if (InsanityRun.playerObject.size() == 1) {
            InsanityRun.idleTaskID = InsanityRun.plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)InsanityRun.plugin, (Runnable)new TaskManager(), 20L, 20L);
            InsanityRun.playersInThisArena.put(arenaName, 0);
        }
        teleportToSpawn(player, arenaName);
        updatePlayerXYZ(player);
        newPlayerObject.setArenaWorld(InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world"));
        newPlayerObject.setLastMovedTime(System.currentTimeMillis());
        newPlayerObject.setStartRaceTime(System.currentTimeMillis());
        newPlayerObject.setIdleCount(0);
        player.setGameMode(GameMode.SURVIVAL);
        int playCount = InsanityRun.playersInThisArena.get(arenaName);
        InsanityRun.playersInThisArena.put(arenaName, ++playCount);
        setScoreboard(player);
        SignManager.updateSign(arenaName);
    }
    
    public static void leaveGame(final Player player, final String arenaName, final iPlayer currentPlayerObject) {
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return;
        }
        if (!player.hasPermission("insanityrun.leave")) {
            CommandManager.perms("leave", (CommandSender)player);
            return;
        }
        if (currentPlayerObject == null) {
            CommandManager.msg("haveNotJoined", (CommandSender)player);
            return;
        }
        if (!currentPlayerObject.getInGame()) {
            CommandManager.msg("haveNotJoined", (CommandSender)player);
            return;
        }
        if (currentPlayerObject.getInGame()) {
            CommandManager.msg("haveNowLeft", "arena", arenaName, (CommandSender)player);
            currentPlayerObject.setInGame(false);
            player.teleport(currentPlayerObject.getSignClickLoc());
            gameOver(player, currentPlayerObject.getInArena(), currentPlayerObject);
            player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
            InsanityRun.playerObject.remove(player.getName());
            SignManager.updateSign(arenaName);
        }
    }
    
    public static void createArena(final Player player, final String arenaName) {
        InsanityRun.currentArena = arenaName;
        InsanityRun.arenaList.add(arenaName);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".world", (Object)"");
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".x", (Object)0.0);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".y", (Object)0.0);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".z", (Object)0.0);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".pitch", (Object)0.0);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".yaw", (Object)0.0);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".pay", (Object)0);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".charge", (Object)0);
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".perform", (Object)"");
        InsanityRun.plugin.getConfig().set("arenaList", (Object)InsanityRun.arenaList);
        InsanityRun.plugin.saveConfig();
        CommandManager.msg("createdArena", "arena", arenaName, (CommandSender)player);
    }
    
    public static void deleteArena(final Player player, final String arenaName) {
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return;
        }
        InsanityRun.plugin.getConfig().set(arenaName, (Object)null);
        InsanityRun.arenaList.remove(arenaName);
        CommandManager.msg("arenaDeleted", "arena", arenaName, (CommandSender)player);
        InsanityRun.plugin.saveConfig();
    }
    
    public static void setSpawnArena(final Player player, final String arenaName) {
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return;
        }
        final Location playerLocation = player.getLocation();
        final String world = playerLocation.getWorld().getName();
        final double x = playerLocation.getX();
        final double y = playerLocation.getY();
        final double z = playerLocation.getZ();
        final float pitch = playerLocation.getPitch();
        final float yaw = playerLocation.getYaw();
        GameManager.config.set(String.valueOf(arenaName) + ".world", (Object)world);
        GameManager.config.set(String.valueOf(arenaName) + ".x", (Object)x);
        GameManager.config.set(String.valueOf(arenaName) + ".y", (Object)y);
        GameManager.config.set(String.valueOf(arenaName) + ".z", (Object)z);
        GameManager.config.set(String.valueOf(arenaName) + ".pitch", (Object)pitch);
        GameManager.config.set(String.valueOf(arenaName) + ".yaw", (Object)yaw);
        InsanityRun.plugin.saveConfig();
        CommandManager.msg("setSpawnFor", "arena", arenaName, (CommandSender)player);
    }
    
    public static void teleportToSpawn(final Player player, final String arenaName) {
        if (GameManager.config.getString(String.valueOf(arenaName) + ".world") == null) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return;
        }
        final World world = Bukkit.getWorld((String)InsanityRun.plugin.getConfig().get(String.valueOf(arenaName) + ".world"));
        final double x = GameManager.config.getDouble(String.valueOf(arenaName) + ".x");
        final double y = GameManager.config.getDouble(String.valueOf(arenaName) + ".y");
        final double z = GameManager.config.getDouble(String.valueOf(arenaName) + ".z");
        final float pitch = (float)GameManager.config.getDouble(String.valueOf(arenaName) + ".pitch");
        final float yaw = (float)GameManager.config.getDouble(String.valueOf(arenaName) + ".yaw");
        player.teleport(new Location(world, x, y, z, yaw, pitch));
    }
    
    public static void setPayArena(final Player player, final String arenaName, final int pay) {
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return;
        }
        GameManager.config.set(String.valueOf(arenaName) + ".pay", (Object)pay);
        InsanityRun.plugin.saveConfig();
        CommandManager.msgNormal(String.valueOf(GameManager.config.getString(new StringBuilder(String.valueOf(InsanityRun.useLanguage)).append(".setPayTo").toString()).replace("%arena%", arenaName).replace("%pay%", new StringBuilder(String.valueOf(pay)).toString())) + " " + GameManager.config.getString(String.valueOf(InsanityRun.useLanguage) + ".payCurrency"), (CommandSender)player);
    }
    
    public static int getPayArena(final Player player, final String arenaName) {
        if (GameManager.config.getString(String.valueOf(arenaName) + ".world") == null) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return 0;
        }
        final int pay = GameManager.config.getInt(String.valueOf(arenaName) + ".pay");
        InsanityRun.plugin.saveConfig();
        return pay;
    }
    
    public static void setChargeArena(final Player player, final String arenaName, final int charge) {
        if (GameManager.config.getString(String.valueOf(arenaName) + ".world") == null) {
            CommandManager.msg("noArena", "arena", arenaName, (CommandSender)player);
            return;
        }
        GameManager.config.set(String.valueOf(arenaName) + ".charge", (Object)charge);
        InsanityRun.plugin.saveConfig();
        CommandManager.msgNormal(String.valueOf(GameManager.config.getString(new StringBuilder(String.valueOf(InsanityRun.useLanguage)).append(".setChargeTo").toString()).replace("%arena%", arenaName).replace("%charge%", new StringBuilder(String.valueOf(charge)).toString())) + " " + GameManager.config.getString(String.valueOf(InsanityRun.useLanguage) + ".payCurrency"), (CommandSender)player);
    }
    
    public static void linkArenas(final Player player, final String arenaName1, final String arenaName2) {
        InsanityRun.plugin.getConfig().set(String.valueOf(arenaName1) + ".link", (Object)arenaName2);
        InsanityRun.plugin.saveConfig();
        CommandManager.msgNormal(GameManager.config.getString(String.valueOf(InsanityRun.useLanguage) + ".linkedTo").replace("%linked%", arenaName1).replace("%linkedto%", arenaName2), (CommandSender)player);
    }
    
    public static void gameOver(final Player player, final String arenaName, final iPlayer currentPlayerObject) {
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setFireTicks(0);
        player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
        InsanityRun.playersInThisArena.replace(arenaName, InsanityRun.playersInThisArena.get(arenaName) - 1);
        SignManager.updateSign(arenaName);
    }
    
    public static int getInt(String string) {
        string = string.replaceAll("[a-zA-Z]+", "");
        if (isInt(string)) {
            return Integer.parseInt(string);
        }
        return 0;
    }
    
    public static boolean isInt(final String string) {
        try {
            Integer.parseInt(string);
        }
        catch (NumberFormatException nFE) {
            return false;
        }
        return true;
    }
    
    public static void setSignSpawn(final Player player, final String arenaName) {
        final Location playerLocation = player.getLocation();
        final String playerName = player.getName();
        final iPlayer currentPlayerObject = InsanityRun.playerObject.get(playerName);
        currentPlayerObject.setSignClickLoc(playerLocation);
    }
    
    public static void serverRestartKick() {
        final ArrayList<String> playersToKick = new ArrayList<String>();
        if (!InsanityRun.playerObject.isEmpty()) {
            for (final String playerName : InsanityRun.playerObject.keySet()) {
                playersToKick.add(playerName);
            }
        }
        if (!playersToKick.isEmpty()) {
            for (final String idlePlayers : playersToKick) {
                final Player player = InsanityRun.plugin.getServer().getPlayer(idlePlayers);
                final iPlayer playerObject = InsanityRun.playerObject.get(idlePlayers);
                player.getInventory().setHelmet(playerObject.getHelmetWorn());
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.teleport(playerObject.getSignClickLoc());
            }
        }
    }
    
    public static void refundMoney(final String arenaName, final Player playerName) {
        final EconomyResponse res;
        if (InsanityRun.useVault && InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".charge") > 0 && !(res = InsanityRun.economy.depositPlayer((OfflinePlayer)playerName, (double)InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".charge"))).transactionSuccess()) {
            Bukkit.getConsoleSender().sendMessage(String.format(String.valueOf(InsanityRun.plugin.getDescription().getName()) + " Vault Deposit - An error occured: %s", res.errorMessage));
        }
    }
    
    public static void updatePlayerXYZ(final Player player) {
        final iPlayer playerObject = InsanityRun.playerObject.get(player.getName());
        final Location playerLocation = player.getLocation();
        playerObject.setLastX((int)playerLocation.getX());
        playerObject.setLastY((int)playerLocation.getY());
        playerObject.setLastZ((int)playerLocation.getZ());
        playerObject.setIdleX((int)playerLocation.getX());
        playerObject.setIdleZ((int)playerLocation.getZ());
    }
}

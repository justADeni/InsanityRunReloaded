import org.bukkit.util.StringUtil;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class CommandManager implements CommandExecutor, TabCompleter
{
    public static void msgNormal(final String s, final CommandSender d) {
        d.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }
    
    public static void msg(final String s, final CommandSender d) {
        d.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + "." + s)));
    }
    
    public static void msg(final String s, final String replace, final List<String> replaced, final CommandSender d) {
        d.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + "." + s).replace("%" + replace + "%", StringUtils.join((Collection)replaced, ","))));
    }
    
    public static void msg(final String s, final String replace, final String replaced, final CommandSender d) {
        d.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + "." + s).replace("%" + replace + "%", replaced)));
    }
    
    public static void perms(final String subcmd, final CommandSender d) {
        msg("noCmdPerms", "command", subcmd, d);
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            msg("ConsoleError", sender);
            return true;
        }
        final Player player = (Player)sender;
        final String playerName = player.getName();
        final iPlayer currentPlayerObject = InsanityRun.playerObject.get(playerName);
        if (commandLabel.equalsIgnoreCase("irun")) {
            if (args.length == 0) {
                GameManager.showHelp(player);
                return true;
            }
            final String subCom = args[0];
            if (subCom.equalsIgnoreCase("randomjoin")) {
                int chosse = 0;
                final int random = InsanityRun.arenaList.size();
                if (random > chosse) {
                    ++chosse;
                    if (random <= chosse) {
                        chosse = 0;
                    }
                    if (!InsanityRun.arenaList.isEmpty()) {
                        GameManager.joinGame(player, InsanityRun.arenaList.get(chosse));
                    }
                    return true;
                }
            }
            if (subCom.equalsIgnoreCase("shop")) {
                if (!player.hasPermission("insanityrun.shop")) {
                    perms(subCom, sender);
                    return true;
                }
                shop.setupGui(player);
                return true;
            }
            else if (subCom.equalsIgnoreCase("reload")) {
                if (!player.hasPermission("insanityrun.setpay")) {
                    perms(subCom, sender);
                    return true;
                }
                InsanityRun.ScoreboardLoading();
                InsanityRun.saveScoreboard();
                InsanityRun.PlLoading();
                InsanityRun.savePl();
                InsanityRun.ShopLoading();
                InsanityRun.saveShop();
                InsanityRun.instance().reloadConfig();
                InsanityRun.instance().saveConfig();
                msg("ConfigReload", sender);
                return true;
            }
            else if (args.length == 3 && subCom.equalsIgnoreCase("setpay")) {
                if (player.hasPermission("insanityrun.setpay")) {
                    final String arenaName = args[1];
                    final int pay = Integer.parseInt(args[2]);
                    GameManager.setPayArena(player, arenaName, pay);
                    return true;
                }
                perms(subCom, sender);
                return true;
            }
            else if (args.length == 3 && subCom.equalsIgnoreCase("setcharge")) {
                if (player.hasPermission("insanityrun.setcharge")) {
                    final String arenaName = args[1];
                    final int pay = Integer.parseInt(args[2]);
                    GameManager.setChargeArena(player, arenaName, pay);
                    return true;
                }
                perms(subCom, sender);
                return true;
            }
            else if (args.length == 3 && subCom.equalsIgnoreCase("adjoin")) {
                if (!player.hasPermission("insanityrun.create")) {
                    perms(subCom, sender);
                    return true;
                }
                final String arenaName = args[2];
                final String joinPlayerName = args[1];
                final Player targetPlayer = InsanityRun.plugin.getServer().getPlayer(joinPlayerName);
                if (targetPlayer == null) {
                    return true;
                }
                if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null) {
                    msg("noArena", "arena", arenaName, sender);
                    return true;
                }
                GameManager.joinGame(targetPlayer, arenaName);
                return true;
            }
            else if (args.length == 3 && subCom.equalsIgnoreCase("adleave")) {
                if (!player.hasPermission("insanityrun.create")) {
                    perms(subCom, sender);
                    return true;
                }
                final String arenaName = args[2];
                final String leavePlayerName = args[1];
                final Player targetPlayer = InsanityRun.plugin.getServer().getPlayer(leavePlayerName);
                if (targetPlayer == null) {
                    return true;
                }
                if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".world") == null) {
                    msg("noArena", "arena", arenaName, sender);
                    return true;
                }
                GameManager.leaveGame(targetPlayer, arenaName, currentPlayerObject);
                return true;
            }
            else if (args.length == 3 && subCom.equalsIgnoreCase("linkarenas")) {
                if (!player.hasPermission("insanityrun.create")) {
                    perms(subCom, sender);
                    return true;
                }
                final String arenaName2 = args[1];
                final String arenaName3 = args[2];
                if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName2) + ".world") == null) {
                    msg("noArena", "arena", arenaName2, sender);
                    return true;
                }
                if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName3) + ".world") == null) {
                    msg("noArena", "arena", arenaName3, sender);
                    return true;
                }
                GameManager.linkArenas(player, arenaName2, arenaName3);
                return true;
            }
            else if (args.length == 1 && subCom.equalsIgnoreCase("list")) {
                if (player.hasPermission("insanityrun.create")) {
                    msg("listArenas", sender);
                    for (final String s : GameManager.arenas()) {
                        msgNormal(InsanityRun.plugin.getConfig().getString("ArenaListFormat").replace("%cost%", new StringBuilder(String.valueOf(InsanityRun.plugin.getConfig().getInt(String.valueOf(s) + ".charge"))).toString()).replace("%arena%", s).replace("%reward%", new StringBuilder(String.valueOf(InsanityRun.plugin.getConfig().getInt(String.valueOf(s) + ".pay"))).toString()), sender);
                    }
                    return true;
                }
                perms(subCom, sender);
                return true;
            }
            else {
                if (args.length != 2) {
                    GameManager.showHelp(player);
                    return true;
                }
                final String arenaName = args[1];
                if (subCom.equalsIgnoreCase("join")) {
                    GameManager.joinGame(player, arenaName);
                    return true;
                }
                if (subCom.equalsIgnoreCase("leave")) {
                    GameManager.leaveGame(player, arenaName, currentPlayerObject);
                    return true;
                }
                if (subCom.equalsIgnoreCase("create")) {
                    if (player.hasPermission("insanityrun.create")) {
                        GameManager.createArena(player, arenaName);
                        return true;
                    }
                    perms(subCom, sender);
                    return true;
                }
                else if (subCom.equalsIgnoreCase("delete")) {
                    if (player.hasPermission("insanityrun.delete")) {
                        GameManager.deleteArena(player, arenaName);
                        return true;
                    }
                    perms(subCom, sender);
                    return true;
                }
                else if (subCom.equalsIgnoreCase("setspawn")) {
                    if (player.hasPermission("insanityrun.setspawn")) {
                        GameManager.setSpawnArena(player, arenaName);
                        return true;
                    }
                    perms(subCom, sender);
                    return true;
                }
            }
        }
        GameManager.showHelp(player);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender s, final Command a, final String ea, final String[] args) {
        final List<String> c = new ArrayList<String>();
        final List<String> normal = Arrays.asList("Join", "RandomJoin", "Leave", "Help");
        if (s instanceof Player) {
            if (args.length == 1) {
                c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)normal, (Collection)new ArrayList()));
                if (s.hasPermission("insanityrun.create")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("Create"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.delete")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("Delete"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.Reload")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("Reload"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.SetCharge")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("SetCharge"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.SetPay")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("SetPay"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.adjoin")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("AdJoin"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.adleave")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("AdLeave"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.adleave")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("LinkArenas"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.setspawn")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("SetSpawn"), (Collection)new ArrayList()));
                }
                if (s.hasPermission("insanityrun.shop")) {
                    c.addAll(StringUtil.copyPartialMatches(args[0], (Iterable)Arrays.asList("Shop"), (Collection)new ArrayList()));
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create") && s.hasPermission("insanityrun.create")) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)Arrays.asList("?"), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("delete") && s.hasPermission("insanityrun.delete") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)InsanityRun.arenaList, (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("SetCharge") && s.hasPermission("insanityrun.SetCharge") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("SetPay") && s.hasPermission("insanityrun.SetPay") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("LinkArenas") && s.hasPermission("insanityrun.LinkArenas") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("Join") && s.hasPermission("insanityrun.Join") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("Leave") && s.hasPermission("insanityrun.Leave") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("SetSpawn") && s.hasPermission("insanityrun.SetSpawn") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[1], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("AdJoin")) {
                    return null;
                }
                if (args[0].equalsIgnoreCase("AdLeave")) {
                    return null;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("SetCharge") && s.hasPermission("insanityrun.SetCharge") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[2], (Iterable)Arrays.asList("?"), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("SetPay") && s.hasPermission("insanityrun.SetPay") && !InsanityRun.arenaList.isEmpty()) {
                    c.addAll(StringUtil.copyPartialMatches(args[2], (Iterable)Arrays.asList("?"), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("AdJoin") && s.hasPermission("insanityrun.AdJoin")) {
                    c.addAll(StringUtil.copyPartialMatches(args[2], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("AdLeave") && s.hasPermission("insanityrun.AdLeave")) {
                    c.addAll(StringUtil.copyPartialMatches(args[2], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
                if (args[0].equalsIgnoreCase("LinkArenas") && s.hasPermission("insanityrun.LinkArenas")) {
                    c.addAll(StringUtil.copyPartialMatches(args[2], (Iterable)GameManager.arenas(), (Collection)new ArrayList()));
                }
            }
        }
        return c;
    }
}

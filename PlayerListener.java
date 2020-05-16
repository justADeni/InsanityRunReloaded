import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Sound;
import java.awt.Point;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.Material;
import org.bukkit.World;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import java.util.Random;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.ChatColor;
import java.util.Iterator;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    @EventHandler
    public static void onFoodLevelChangeEvent(final FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && InsanityRun.playerObject.get(((Player)event.getEntity()).getName()) != null) {
            event.setCancelled(true);
        }
    }
    
    private static void waterRestart(final iPlayer currentPlayerObject) {
        final Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
        player.setFireTicks(0);
        currentPlayerObject.setCoins(0);
        currentPlayerObject.setFrozen(false);
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        currentPlayerObject.setInGame(true);
        currentPlayerObject.clearGoldWalkedArray();
        GameManager.teleportToSpawn(player, currentPlayerObject.getInArena());
        GameManager.updatePlayerXYZ(player);
    }
    
    private static void checkpointRestart(final iPlayer currentPlayerObject) {
        final Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
        player.setFireTicks(0);
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        currentPlayerObject.setInGame(true);
        if (currentPlayerObject.getLastCheckpoint() != null) {
            player.teleport(currentPlayerObject.getLastCheckpoint());
            GameManager.updatePlayerXYZ(player);
        }
        else {
            GameManager.teleportToSpawn(player, currentPlayerObject.getInArena());
            GameManager.updatePlayerXYZ(player);
        }
    }
    
    private static void defaultRestart(final iPlayer currentPlayerObject) {
        final Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
        player.setFireTicks(0);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(InsanityRun.plugin.getConfig().getString(new StringBuilder(String.valueOf(InsanityRun.useLanguage)).append(".gameOver").toString())) + " " + currentPlayerObject.getCoins() + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".gameCurrency")));
        player.teleport(currentPlayerObject.getSignClickLoc());
        GameManager.gameOver(player, currentPlayerObject.getInArena(), currentPlayerObject);
        GameManager.updatePlayerXYZ(player);
    }
    
    @EventHandler
    public void blockplace(final BlockPlaceEvent e) {
        if (InsanityRun.playerObject.get(e.getPlayer().getName()) != null && InsanityRun.playerObject.get(e.getPlayer().getName()).getInGame()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void blockbreak(final BlockBreakEvent e) {
        if (InsanityRun.playerObject.get(e.getPlayer().getName()) != null && InsanityRun.playerObject.get(e.getPlayer().getName()).getInGame()) {
            e.setCancelled(true);
        }
    }
    
    private static void endLevelOrGame(final String w) {
        final iPlayer currentPlayerObject = InsanityRun.playerObject.get(w);
        final Long runTime = System.currentTimeMillis() - currentPlayerObject.getStartRaceTime();
        final Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
        final Location loc = player.getPlayer().getLocation();
        final String playerName = currentPlayerObject.getPlayerName();
        final String arenaName = currentPlayerObject.getInArena();
        currentPlayerObject.setInGame(false);
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        final Firework fw = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        final FireworkMeta fwm = fw.getFireworkMeta();
        final Random r = new Random();
        final FireworkEffect.Type type = FireworkEffect.Type.BALL;
        final Color c1 = Color.GREEN;
        final Color c2 = Color.YELLOW;
        final FireworkEffect effect2 = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        fwm.addEffect(effect2);
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);
        if (InsanityRun.broadcastWins) {
            InsanityRun.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".broadcastWinsText").replace("%arena%", arenaName).replace("%coins%", String.valueOf(currentPlayerObject.getCoins()) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".gameCurrency")).replace("%player%", playerName).replace("%time%", new StringBuilder().append(formatIntoHHMMSS(runTime)).toString())));
        }
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".gameOver").replace("%coins%", String.valueOf(currentPlayerObject.getCoins()) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".gameCurrency")).replace("%time%", new StringBuilder().append(formatIntoHHMMSS(runTime)).toString())));
        }
        scoresUpdate(arenaName, playerName, runTime, currentPlayerObject.getCoins());
        Coins.give(player, currentPlayerObject.getCoins());
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".perform") != null) {
            for (final String s : InsanityRun.plugin.getConfig().getStringList(String.valueOf(arenaName) + ".perform")) {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), s);
            }
        }
        player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
        if (InsanityRun.useVault && InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".pay") > 0) {
            final EconomyResponse res = InsanityRun.economy.depositPlayer(player.getName(), (double)InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".pay"));
            if (res.transactionSuccess()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".vaultAward").replace("%reward%", String.valueOf(InsanityRun.plugin.getConfig().getInt(new StringBuilder(String.valueOf(arenaName)).append(".pay").toString())) + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".payCurrency"))));
            }
            else {
                player.sendMessage(String.format("An error occured: %s", res.errorMessage));
            }
        }
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link") != null && (!InsanityRun.useVault || canAfford(currentPlayerObject, InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link")))) {
            int playCount = InsanityRun.playersInThisArena.get(arenaName);
            InsanityRun.playersInThisArena.put(arenaName, --playCount);
            GameManager.teleportToSpawn(player, InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link"));
            GameManager.updatePlayerXYZ(player);
            currentPlayerObject.setLastCheckpoint(player.getLocation());
            currentPlayerObject.setInArena(InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link"));
            currentPlayerObject.setFrozen(false);
            currentPlayerObject.setCoins(0);
            currentPlayerObject.setLastMovedTime(System.currentTimeMillis());
            currentPlayerObject.setStartRaceTime(System.currentTimeMillis());
            currentPlayerObject.setInGame(true);
            InsanityRun.playersInThisArena.putIfAbsent(InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link"), 0);
            playCount = InsanityRun.playersInThisArena.get(InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link"));
            InsanityRun.playersInThisArena.put(InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".link"), ++playCount);
        }
        if (InsanityRun.plugin.getConfig().getBoolean("noEndTeleport")) {
            currentPlayerObject.setInGame(false);
            InsanityRun.playerObject.remove(player.getName());
            InsanityRun.playerObject.remove(currentPlayerObject.getPlayerName());
            GameManager.gameOver(player, arenaName, currentPlayerObject);
        }
        else {
            player.teleport(currentPlayerObject.getSignClickLoc());
            currentPlayerObject.setInGame(false);
            InsanityRun.playerObject.remove(player.getName());
            InsanityRun.playerObject.remove(currentPlayerObject.getPlayerName());
            GameManager.gameOver(player, arenaName, currentPlayerObject);
        }
    }
    
    private static boolean canAfford(final iPlayer currentPlayerObject, final String arenaName) {
        final Player player = InsanityRun.plugin.getServer().getPlayer(currentPlayerObject.getPlayerName());
        final String playerName = currentPlayerObject.getPlayerName();
        if (!InsanityRun.useVault) {
            return false;
        }
        if (!InsanityRun.economy.has(player.getName(), InsanityRun.plugin.getConfig().getDouble(String.valueOf(arenaName) + ".charge"))) {
            CommandManager.msgNormal(String.valueOf(InsanityRun.plugin.getConfig().getString(new StringBuilder(String.valueOf(InsanityRun.useLanguage)).append(".notEnoughMoneyText").toString()).replace("%cost%", new StringBuilder(String.valueOf(InsanityRun.plugin.getConfig().getInt(new StringBuilder(String.valueOf(arenaName)).append(".charge").toString()))).toString())) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".payCurrency"), (CommandSender)player);
            return false;
        }
        final EconomyResponse r = InsanityRun.economy.withdrawPlayer(playerName, (double)InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".charge"));
        if (r.transactionSuccess()) {
            return true;
        }
        player.sendMessage(String.format("An error occured: %s", r.errorMessage));
        return false;
    }
    
    private static String formatIntoHHMMSS(final Long millisecs) {
        final int secs = (int)(millisecs / 1000L);
        final int remainder = secs % 3600;
        final int minutes = remainder / 60;
        final int seconds;
        return String.valueOf(minutes) + ":" + (((seconds = remainder % 60) < 10) ? "0" : "") + seconds;
    }
    
    private static void scoresUpdate(final String arenaName, final String playerName, final Long runTime, final int coins) {
        if (InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".fastsign.world") == null) {
            return;
        }
        final String worldtemp = InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".fastsign.world");
        final World world = InsanityRun.plugin.getServer().getWorld(worldtemp);
        final double x = InsanityRun.plugin.getConfig().getDouble(String.valueOf(arenaName) + ".fastsign.x");
        final double y = InsanityRun.plugin.getConfig().getDouble(String.valueOf(arenaName) + ".fastsign.y");
        final double z = InsanityRun.plugin.getConfig().getDouble(String.valueOf(arenaName) + ".fastsign.z");
        final Long[] runTimes = new Long[6];
        final String[] runTimeNames = new String[6];
        final int[] coinsCollected = new int[6];
        if (runTime < InsanityRun.plugin.getConfig().getLong(String.valueOf(arenaName) + ".fastest." + 1 + ".time") && InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".fastest.1.name") == null) {
            return;
        }
        for (int i = 1; i < 6; ++i) {
            runTimes[i] = InsanityRun.plugin.getConfig().getLong(String.valueOf(arenaName) + ".fastest." + i + ".time");
            runTimeNames[i] = InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".fastest." + i + ".name");
            coinsCollected[i] = InsanityRun.plugin.getConfig().getInt(String.valueOf(arenaName) + ".fastest." + i + ".coins");
        }
        for (int i = 1; i < 6; ++i) {
            if (runTime < runTimes[i]) {
                for (int j = 4; j > i - 1; --j) {
                    runTimes[j + 1] = runTimes[j];
                    runTimeNames[j + 1] = runTimeNames[j];
                    coinsCollected[j + 1] = coinsCollected[j];
                }
                runTimes[i] = runTime;
                runTimeNames[i] = playerName;
                coinsCollected[i] = coins;
                break;
            }
        }
        for (int i = 5; i > 0; --i) {
            InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".fastest." + i + ".name", (Object)runTimeNames[i]);
            InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".fastest." + i + ".time", (Object)runTimes[i]);
            InsanityRun.plugin.getConfig().set(String.valueOf(arenaName) + ".fastest." + i + ".coins", (Object)coinsCollected[i]);
        }
        InsanityRun.plugin.saveConfig();
        bottomSignUpdate(new Location(world, x, y, z), arenaName);
    }
    
    public static void bottomSignUpdate(final Location bLocation, final String arenaName) {
        bLocation.setY(bLocation.getY() - 1.0);
        final World w = bLocation.getWorld();
        final Block b = w.getBlockAt(bLocation);
        final Material bm = w.getBlockAt(bLocation).getType();
        if (bm == Material.LEGACY_SIGN_POST || bm == Material.WALL_SIGN) {
            final Sign sign = (Sign)b.getState();
            for (int i = 2; i < 6; ++i) {
                String signName = InsanityRun.plugin.getConfig().getString(String.valueOf(arenaName) + ".fastest." + i + ".name");
                final Long signTime = InsanityRun.plugin.getConfig().getLong(String.valueOf(arenaName) + ".fastest." + i + ".time");
                if (signName.length() > 9) {
                    signName = signName.substring(0, 9);
                }
                sign.setLine(i - 2, String.valueOf(signName) + " " + formatIntoHHMMSS(signTime));
            }
            sign.update();
        }
    }
    
    @EventHandler
    public void onPlayerMoveEvent(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final iPlayer currentPlayerObject = InsanityRun.playerObject.get(player.getName());
        if (currentPlayerObject == null) {
            return;
        }
        if (!currentPlayerObject.getInGame()) {
            return;
        }
        final Location loc = player.getLocation();
        if (currentPlayerObject.getFrozen()) {
            player.teleport(loc);
        }
        final int lastX = currentPlayerObject.getLastX();
        final int lastY = currentPlayerObject.getLastY();
        final int lastZ = currentPlayerObject.getLastZ();
        final int locX = (int)loc.getX();
        final int locY = (int)loc.getY();
        final int locZ = (int)loc.getZ();
        if (locX != lastX || locY != lastY || locZ != lastZ) {
            currentPlayerObject.setLastX(locX);
            currentPlayerObject.setLastY(locY);
            currentPlayerObject.setLastZ(locZ);
            loc.setY(loc.getY() - InsanityRun.blockJumpHeight);
            final Material blockOn = loc.getWorld().getBlockAt(loc).getType();
            switch (blockOn) {
                case GOLD_BLOCK: {
                    final ArrayList<Point> playerGoldWalked = currentPlayerObject.getGoldWalkedArray();
                    if (playerGoldWalked.contains(new Point(locX, locZ))) {
                        break;
                    }
                    playerGoldWalked.add(new Point(locX, locZ));
                    currentPlayerObject.setGoldWalkedArray(playerGoldWalked);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    final int coinsOwned = currentPlayerObject.getCoins();
                    currentPlayerObject.setCoins(coinsOwned + 1);
                    break;
                }
                case DIAMOND_BLOCK: {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    player.setVelocity(player.getVelocity().setY(1.5));
                    break;
                }
                case GRAVEL:
                case SAND: {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
                    break;
                }
                case EMERALD_BLOCK: {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 2));
                    break;
                }
                case LAPIS_BLOCK: {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
                    break;
                }
                case COAL_BLOCK: {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 2));
                    break;
                }
                case OBSIDIAN: {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 80, 2));
                    break;
                }
                case PUMPKIN: {
                    ItemStack helmet = player.getInventory().getHelmet();
                    if (helmet == null) {
                        helmet = new ItemStack(Material.AIR, 1, (short)14);
                    }
                    if (helmet.getType() != Material.PUMPKIN) {
                        currentPlayerObject.setHelmetWorn(helmet);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN, 1, (short)14));
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)InsanityRun.plugin, () -> player.getInventory().setHelmet(currentPlayerObject.getHelmetWorn()), 40L);
                    break;
                }
                case ICE: {
                    currentPlayerObject.setFrozen(true);
                    Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)InsanityRun.plugin, () -> currentPlayerObject.setFrozen(false), 40L);
                    break;
                }
                case SPONGE: {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
                    player.setVelocity(player.getLocation().getDirection().multiply(-1));
                    break;
                }
                case GLOWSTONE: {
                    currentPlayerObject.setLastCheckpoint(player.getLocation());
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    break;
                }
                case REDSTONE_BLOCK: {
                    if (!currentPlayerObject.getInGame()) {
                        break;
                    }
                    endLevelOrGame(playerName);
                    break;
                }
                case LAVA:
                case WATER:
                case LEGACY_STATIONARY_WATER:
                case LEGACY_STATIONARY_LAVA: {
                    if (!currentPlayerObject.getInGame()) {
                        break;
                    }
                    currentPlayerObject.setInGame(false);
                    if (blockOn == Material.WATER || blockOn == Material.LEGACY_STATIONARY_WATER) {
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.0f);
                    }
                    if (blockOn == Material.LAVA || blockOn == Material.LEGACY_STATIONARY_LAVA) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1.0f, 1.0f);
                    }
                    final iPlayer currentPlayerObject2;
                    Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)InsanityRun.plugin, () -> {
                        if (InsanityRun.plugin.getConfig().getBoolean("waterRestartsRun")) {
                            waterRestart(currentPlayerObject2);
                        }
                        else if (InsanityRun.plugin.getConfig().getBoolean("useCheckpoints")) {
                            checkpointRestart(currentPlayerObject2);
                        }
                        else {
                            defaultRestart(currentPlayerObject2);
                        }
                        return;
                    }, 20L);
                    break;
                }
            }
            InsanityRun.playerObject.put(playerName, currentPlayerObject);
        }
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final iPlayer playerObject = InsanityRun.playerObject.get(playerName);
        if (InsanityRun.playerObject.size() > 0 && playerObject != null && playerObject.getInGame() && (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final iPlayer playerObject = InsanityRun.playerObject.get(playerName);
        if (playerObject != null && playerObject.getInGame()) {
            final Location loc = playerObject.getSignClickLoc();
            InsanityRun.playerQuitList.put(playerName, loc);
            GameManager.refundMoney(playerObject.getInArena(), player);
            GameManager.gameOver(player, playerObject.getInArena(), playerObject);
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final ArrayList<String> playersToRemove = new ArrayList<String>();
        final Player joinPlayer = event.getPlayer();
        for (final String playerName : InsanityRun.playerQuitList.keySet()) {
            if (!joinPlayer.getName().equals(playerName)) {
                continue;
            }
            final Location loc = InsanityRun.playerQuitList.get(playerName);
            joinPlayer.teleport(loc);
            playersToRemove.add(playerName);
            joinPlayer.setFireTicks(0);
            for (final PotionEffect effect : joinPlayer.getActivePotionEffects()) {
                joinPlayer.removePotionEffect(effect.getType());
            }
            joinPlayer.getInventory().setHelmet(new ItemStack(Material.AIR, 1, (short)14));
        }
        for (final String delPlayers : playersToRemove) {
            InsanityRun.playerQuitList.remove(delPlayers);
        }
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && InsanityRun.playerObject.get(((Player)event.getEntity()).getName()) != null && InsanityRun.playerObject.get(event.getEntity().getName()).getInGame()) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);
        }
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        final String playerName = player.getName();
        final iPlayer playerObject = InsanityRun.playerObject.get(playerName);
        if (playerObject != null && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }
}

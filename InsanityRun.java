import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import java.util.Iterator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import org.bukkit.Location;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class InsanityRun extends JavaPlugin
{
    public static InsanityRun plugin;
    public static String currentArena;
    public static String gameVersion;
    public static Integer idleKickTime;
    public static Float blockJumpHeight;
    public static Integer idleTaskID;
    public static Boolean broadcastWins;
    public static String broadcastWinsText;
    public static ScoreboardManager manager;
    public static boolean useVault;
    public static Economy economy;
    public static String useLanguage;
    public static ArrayList<String> helpText;
    public static HashMap<String, iPlayer> playerObject;
    public static List<String> arenaList;
    public static HashMap<String, Integer> playersInThisArena;
    public static HashMap<String, Location> playerQuitList;
    private static File sc;
    public static FileConfiguration scFile;
    private static File pl;
    public static FileConfiguration players;
    private static File sf;
    public static FileConfiguration shop;
    
    static {
        InsanityRun.useVault = false;
        InsanityRun.economy = null;
        InsanityRun.useLanguage = null;
        InsanityRun.helpText = new ArrayList<String>();
        InsanityRun.playerObject = new HashMap<String, iPlayer>();
        InsanityRun.arenaList = new ArrayList<String>();
        InsanityRun.playersInThisArena = new HashMap<String, Integer>();
        InsanityRun.playerQuitList = new HashMap<String, Location>();
    }
    
    public static void ScoreboardLoading() {
        InsanityRun.sc = new File("plugins/InsanityRunReloaded/Scoreboard.yml");
        InsanityRun.scFile = (FileConfiguration)YamlConfiguration.loadConfiguration(InsanityRun.sc);
        InsanityRun.scFile.options().copyDefaults(true).copyHeader(true);
        InsanityRun.scFile.options().header("%money%   player money\n%coins%   collected coins\n%time%, %ingame%   arena running time\n%online%   online players\n%playing%   how many players playing in arena that playing player\n%max_players%   maximum players on server\n");
        final List<String> lines = (List<String>)InsanityRun.scFile.getStringList("Lines");
        lines.add("&r&lMoney: &a%money%$");
        lines.add("&r&lCoins:  &a%coins%");
        lines.add("&r&lOnline:  &a%online% / %max_players%");
        InsanityRun.scFile.addDefault("Name", (Object)"&6InsanityRun");
        InsanityRun.scFile.addDefault("Lines", (Object)lines);
        saveScoreboard();
        if (!InsanityRun.sc.exists()) {
            InsanityRun.plugin.saveResource("Scoreboard.yml", false);
        }
    }
    
    public static void saveScoreboard() {
        try {
            InsanityRun.scFile.save(InsanityRun.sc);
        }
        catch (Exception ex) {}
    }
    
    public static void PlLoading() {
        InsanityRun.pl = new File("plugins/InsanityRunReloaded/PlayersData.data");
        InsanityRun.players = (FileConfiguration)YamlConfiguration.loadConfiguration(InsanityRun.pl);
        InsanityRun.players.options().copyDefaults(true);
        InsanityRun.players.addDefault("Players", (Object)"");
        savePl();
        if (!InsanityRun.pl.exists()) {
            InsanityRun.plugin.saveResource("PlayersData.data", false);
        }
    }
    
    public static void savePl() {
        try {
            InsanityRun.players.save(InsanityRun.pl);
        }
        catch (Exception ex) {}
    }
    
    public static InsanityRun instance() {
        return InsanityRun.plugin;
    }
    
    public static void setupConfigVars() {
        InsanityRun.useVault = InsanityRun.plugin.getConfig().getBoolean("useVault");
        InsanityRun.useLanguage = InsanityRun.plugin.getConfig().getString("useLanguage");
        for (int i = 1; i < 13; ++i) {
            InsanityRun.helpText.add(InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".help" + i));
        }
        for (final String s : InsanityRun.plugin.getConfig().getStringList("arenaList")) {
            InsanityRun.arenaList.add(s);
        }
        InsanityRun.idleKickTime = InsanityRun.plugin.getConfig().getInt("idleKickTime");
        InsanityRun.broadcastWins = InsanityRun.plugin.getConfig().getBoolean("broadcastWins");
        InsanityRun.broadcastWinsText = InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".broadcastWinsText");
        InsanityRun.blockJumpHeight = (float)InsanityRun.plugin.getConfig().getDouble("blockJumpHeight");
    }
    
    private String path(final VaultType type) {
        String path = "";
        switch (type) {
            case Enabled: {
                path = String.valueOf(this.getDescription().getName()) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".vaultEnabled");
                break;
            }
            case Disabled: {
                path = String.valueOf(this.getDescription().getName()) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".vaultDisabled");
                break;
            }
            case Found: {
                path = String.valueOf(this.getDescription().getName()) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".vaultFound");
                break;
            }
            case NotFound: {
                path = String.valueOf(this.getDescription().getName()) + " " + InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".vaultNotFound");
                break;
            }
        }
        return path;
    }
    
    public void onEnable() {
        InsanityRun.plugin = this;
        final PluginManager plMan = this.getServer().getPluginManager();
        new CommandManager();
        plMan.registerEvents((Listener)new CreateSignListener(), (Plugin)this);
        plMan.registerEvents((Listener)new ClickSignListener(), (Plugin)this);
        plMan.registerEvents((Listener)new DestroySignListener(), (Plugin)this);
        plMan.registerEvents((Listener)new PlayerListener(), (Plugin)this);
        plMan.registerEvents((Listener)new PlayerShopClick(), (Plugin)this);
        this.getCommand("irun").setExecutor((CommandExecutor)new CommandManager());
        InsanityRun.manager = Bukkit.getScoreboardManager();
        this.saveDefaultConfig();
        ScoreboardLoading();
        saveScoreboard();
        PlLoading();
        savePl();
        ShopLoading();
        saveShop();
        InsanityRun.gameVersion = this.getDescription().getVersion();
        this.configureKeys();
        setupConfigVars();
        Bukkit.getScheduler().runTaskLater((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (InsanityRun.useVault) {
                    InsanityRun.this.getLogger().info(InsanityRun.this.path(VaultType.Enabled));
                    if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                        if (InsanityRun.this.setupVault()) {
                            InsanityRun.this.getLogger().info(InsanityRun.this.path(VaultType.Found));
                        }
                        else {
                            InsanityRun.this.getLogger().info(InsanityRun.this.path(VaultType.NotFound));
                            InsanityRun.useVault = false;
                        }
                    }
                }
                else {
                    InsanityRun.this.getLogger().info(InsanityRun.this.path(VaultType.Disabled));
                }
            }
        }, 0L);
    }
    
    private boolean setupVault() {
        final RegisteredServiceProvider<Economy> service = (RegisteredServiceProvider<Economy>)Bukkit.getServicesManager().getRegistration((Class)Economy.class);
        if (service != null) {
            InsanityRun.economy = (Economy)service.getProvider();
        }
        return InsanityRun.economy != null;
    }
    
    public static void ShopLoading() {
        InsanityRun.sf = new File("plugins/InsanityRunReloaded/Shop.yml");
        InsanityRun.shop = (FileConfiguration)YamlConfiguration.loadConfiguration(InsanityRun.sf);
        InsanityRun.shop.options().copyDefaults(true).copyHeader(true);
        InsanityRun.shop.options().header("%player%   select player\n%coins%   amount of coins\n");
        InsanityRun.shop.addDefault("ShopMenu", (Object)"&6Shop");
        InsanityRun.shop.addDefault("Format.Item", (Object)"&a%item% &7- &6%cost% coins");
        InsanityRun.shop.addDefault("Items.Example.Name", (Object)"&aAn example item");
        final List<String> dec = (List<String>)InsanityRun.shop.getStringList("Items.Example.OnBuy.ProcessCommands");
        dec.add("&eThis is an example kit with");
        dec.add("&e special items! Cost %cost% coins");
        InsanityRun.shop.addDefault("Items.Example.Description", (Object)dec);
        InsanityRun.shop.addDefault("Items.Example.Icon", (Object)"STONE_SWORD");
        InsanityRun.shop.addDefault("Items.Example.Cost", (Object)50);
        final List<String> cmds = (List<String>)InsanityRun.shop.getStringList("Items.Example.OnBuy.ProcessCommands");
        cmds.add("say &aPlayer &n%player%&r &abuy an example kit for &n%cost%&r &acoins!");
        cmds.add("eco give %player% 65");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.ProcessCommands", (Object)cmds);
        final List<String> msgs = (List<String>)InsanityRun.shop.getStringList("Items.Example.OnBuy.SendMessages");
        msgs.add("&6You buy an &aExample kit &6for &a%cost% coins");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.SendMessages", (Object)msgs);
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Diamond_Helmet.Name", (Object)"&bShiny helmet!!");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Diamond_Helmet.Amount", (Object)1);
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Diamond_Helmet.HideEnchants", (Object)true);
        final List<String> enchss = (List<String>)InsanityRun.shop.getStringList("Items.Example.OnBuy.GiveItem.Diamond_Helmet.Enchants");
        enchss.add("Unbreaking 1");
        enchss.add("Protection_Fire 8");
        enchss.add("Mending");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Iron_Chestplate.Enchants", (Object)enchss);
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Iron_Chestplate.Name", (Object)"&7Knight Chestplate");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Iron_Chestplate.Amount", (Object)1);
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Iron_Chestplate.Unbreakable", (Object)true);
        final List<String> enchs = (List<String>)InsanityRun.shop.getStringList("Items.Example.OnBuy.GiveItem.Iron_Chestplate.Enchants");
        enchs.add("Unbreaking 2");
        enchs.add("Protection 5");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Iron_Chestplate.Enchants", (Object)enchs);
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Apple.Name", (Object)"&4Tasty apple");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Apple.Amount", (Object)10);
        final List<String> lore = (List<String>)InsanityRun.shop.getStringList("Items.Example.OnBuy.GiveItem.Apple.Lore");
        lore.add("&cIm healthy apple!");
        lore.add("&4&lEAT ME");
        InsanityRun.shop.addDefault("Items.Example.OnBuy.GiveItem.Apple.Lore", (Object)lore);
        saveShop();
        if (!InsanityRun.sf.exists()) {
            InsanityRun.plugin.saveResource("Shop.yml", false);
        }
    }
    
    public static void saveShop() {
        try {
            InsanityRun.shop.save(InsanityRun.sf);
        }
        catch (Exception ex) {}
    }
    
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks((Plugin)InsanityRun.plugin);
        GameManager.serverRestartKick();
        ScoreboardLoading();
        saveScoreboard();
        PlLoading();
        savePl();
        ShopLoading();
        saveShop();
    }
    
    private void configureKeys() {
        if (!InsanityRun.plugin.getConfig().contains("blockJumpHeight")) {
            InsanityRun.plugin.getConfig().set("blockJumpHeight", (Object)0.5f);
        }
        this.saveConfig();
    }
    
    private enum VaultType
    {
        Enabled("Enabled", 0), 
        Disabled("Disabled", 1), 
        Found("Found", 2), 
        NotFound("NotFound", 3);
        
        private VaultType(final String name, final int ordinal) {
        }
    }
}

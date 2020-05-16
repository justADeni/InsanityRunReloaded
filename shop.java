import org.bukkit.inventory.ItemFlag;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;

public class shop
{
    static FileConfiguration s;
    public static HashMap<ItemStack, String> items;
    public static HashMap<Player, Inventory> iven;
    public static Inventory inv;
    static String itemm;
    
    static {
        shop.s = InsanityRun.shop;
        shop.items = new HashMap<ItemStack, String>();
        shop.iven = new HashMap<Player, Inventory>();
        shop.inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', shop.s.getString("ShopMenu")));
    }
    
    private static boolean ex(final String path) {
        return shop.s.getString("Items." + shop.itemm + "." + path) != null;
    }
    
    public static void addItems(final Inventory inv) {
        for (final String item : shop.s.getConfigurationSection("Items").getKeys(false)) {
            final int cost = shop.s.getInt("Items." + item + ".Cost");
            String ItemName = item;
            if (ex("Name")) {
                ItemName = shop.s.getString("Items." + item + ".Name").replace("%item%", item).replace("%cost%", new StringBuilder(String.valueOf(cost)).toString());
            }
            List<String> Dec = null;
            if (ex("Description")) {
                Dec = (List<String>)shop.s.getStringList("Items." + item + ".Description");
            }
            Material icon = Material.matchMaterial(shop.s.getString("Items." + item + ".Icon"));
            if (icon == null) {
                icon = Material.STONE;
            }
            final String format = shop.s.getString("Format.Item").replace("%item%", ItemName).replace("%cost%", new StringBuilder(String.valueOf(cost)).toString());
            final ItemStack i = new ItemStack(icon);
            final ItemMeta m = i.getItemMeta();
            m.setDisplayName(ChatColor.translateAlternateColorCodes('&', format));
            if (Dec != null) {
                final ArrayList<String> lore = new ArrayList<String>();
                for (final String s : Dec) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("%item%", item).replace("%cost%", new StringBuilder(String.valueOf(cost)).toString())));
                }
                m.setLore((List)lore);
            }
            i.setItemMeta(m);
            shop.items.put(i, item);
            inv.addItem(new ItemStack[] { i });
        }
    }
    
    public static void updateGui(final Player humanEntity, final InventoryView inventoryView) {
        shop.inv.clear();
        shop.items.clear();
        final ItemStack close = new ItemStack(Material.REDSTONE_BLOCK, 1);
        final ItemMeta w = close.getItemMeta();
        w.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Close"));
        close.setItemMeta(w);
        final ItemStack coins = new ItemStack(Material.SUNFLOWER, 1);
        final ItemMeta metas = coins.getItemMeta();
        metas.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6" + Coins.bal(humanEntity) + " Coins"));
        coins.setItemMeta(metas);
        final ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));
        item.setItemMeta(meta);
        inventoryView.setItem(0, item);
        inventoryView.setItem(1, item);
        inventoryView.setItem(2, item);
        inventoryView.setItem(3, item);
        inventoryView.setItem(4, item);
        inventoryView.setItem(5, item);
        inventoryView.setItem(6, item);
        inventoryView.setItem(7, item);
        inventoryView.setItem(8, item);
        inventoryView.setItem(9, item);
        inventoryView.setItem(17, item);
        inventoryView.setItem(18, item);
        inventoryView.setItem(26, item);
        inventoryView.setItem(27, item);
        inventoryView.setItem(35, item);
        inventoryView.setItem(36, item);
        inventoryView.setItem(44, item);
        inventoryView.setItem(45, item);
        inventoryView.setItem(46, item);
        inventoryView.setItem(47, item);
        inventoryView.setItem(48, item);
        inventoryView.setItem(49, coins);
        inventoryView.setItem(50, item);
        inventoryView.setItem(51, item);
        inventoryView.setItem(52, close);
        inventoryView.setItem(53, item);
        addItems(shop.inv);
        shop.iven.remove(humanEntity);
        shop.iven.put(humanEntity, shop.inv);
    }
    
    public static void setupGui(final Player p) {
        shop.inv.clear();
        shop.items.clear();
        final ItemStack close = new ItemStack(Material.REDSTONE_BLOCK, 1);
        final ItemMeta w = close.getItemMeta();
        w.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Close"));
        close.setItemMeta(w);
        final ItemStack coins = new ItemStack(Material.SUNFLOWER, 1);
        final ItemMeta metas = coins.getItemMeta();
        metas.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6" + Coins.bal(p) + " Coins"));
        coins.setItemMeta(metas);
        final ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));
        item.setItemMeta(meta);
        shop.inv.setItem(0, item);
        shop.inv.setItem(1, item);
        shop.inv.setItem(2, item);
        shop.inv.setItem(3, item);
        shop.inv.setItem(4, item);
        shop.inv.setItem(5, item);
        shop.inv.setItem(6, item);
        shop.inv.setItem(7, item);
        shop.inv.setItem(8, item);
        shop.inv.setItem(9, item);
        shop.inv.setItem(17, item);
        shop.inv.setItem(18, item);
        shop.inv.setItem(26, item);
        shop.inv.setItem(27, item);
        shop.inv.setItem(35, item);
        shop.inv.setItem(36, item);
        shop.inv.setItem(44, item);
        shop.inv.setItem(45, item);
        shop.inv.setItem(46, item);
        shop.inv.setItem(47, item);
        shop.inv.setItem(48, item);
        shop.inv.setItem(49, coins);
        shop.inv.setItem(50, item);
        shop.inv.setItem(51, item);
        shop.inv.setItem(52, close);
        shop.inv.setItem(53, item);
        addItems(shop.inv);
        shop.iven.put(p, shop.inv);
        p.openInventory(shop.inv);
    }
    
    public static String replaceAllEnchantsNamesToRealNames(String string) {
        if (Enchantment.getByName(string) == null) {
            string = string.toUpperCase();
            return string.replaceAll("SHARPNESS", "DAMAGE_ALL").replaceAll("FIRE", "ARROW_FIRE").replaceAll("INFINITY", "ARROW_INFINITE").replaceAll("FIREASPECT", "FIRE_ASPECT").replaceAll("RESPIRATION", "OXYGEN").replaceAll("LOOTING", "LOOT_BONUS_MOBS").replaceAll("FORTUNE", "LOOT_BONUS_BLOCKS").replaceAll("UNBREAKING", "DURABILITY").replaceAll("AQUA_AFFINITY", "WATER_WORKER").replaceAll("PROTECTION", "PROTECTION_ENVIRONMENTAL").replaceAll("BLAST_PROTECTION", "PROTECTION_EXPLOSIONS").replaceAll("FEATHER_FALLING", "PROTECTION_FALL").replaceAll("FIRE_PROTECTION", "PROTECTION_FIRE").replaceAll("PROJECTILE_PROTECTION", "PROTECTION_PROJECTILE").replaceAll("CURSE_OF_VANISHING", "VANISHING_CURSE").replaceAll("CURSE_OF_BINDING", "BINDING_CURSE").replaceAll("SMITE", "DAMAGE_UNDEAD").replaceAll("POWER", "ARROW_DAMAGE").replaceAll("ALLDAMAGE", "DAMAGE_ALL").replaceAll("DAMAGEALL", "DAMAGE_ALL").replaceAll("BANE_OF_ARTHROPODS", "DAMAGE_ARTHROPODS").replaceAll("EFFICIENCY", "DIG_SPEED").replaceAll("ALL_DAMAGE", "DAMAGE_ALL").replaceAll("PUNCH", "ARROW_KNOCKBACK").replaceAll("LOOTMOBS", "LOOT_BONUS_MOBS").replaceAll("LOOTBLOCKS", "LOOT_BONUS_BLOCKS");
        }
        return string;
    }
    
    public static void buyItem(final String item, final Player p) {
        shop.itemm = item;
        if (shop.s.getString("Items." + item) != null) {
            final int cost = shop.s.getInt("Items." + item + ".Cost");
            if (Coins.has(p, cost)) {
                Coins.take(p, cost);
                List<String> cmds = null;
                if (ex("OnBuy.ProcessCommands")) {
                    cmds = (List<String>)shop.s.getStringList("Items." + item + ".OnBuy.ProcessCommands");
                }
                if (cmds != null) {
                    for (final String f : cmds) {
                        Bukkit.dispatchCommand((CommandSender)p, ChatColor.translateAlternateColorCodes('&', f.replace("%player%", p.getName()).replace("%item%", item).replace("%cost%", new StringBuilder(String.valueOf(cost)).toString())));
                    }
                }
                List<String> msgs = null;
                if (ex("OnBuy.SendMessages")) {
                    msgs = (List<String>)shop.s.getStringList("Items." + item + ".OnBuy.SendMessages");
                }
                if (msgs != null) {
                    for (final String f2 : msgs) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', f2.replace("%player%", p.getName()).replace("%item%", item).replace("%cost%", new StringBuilder(String.valueOf(cost)).toString())));
                    }
                }
                if (ex("OnBuy.GiveItem")) {
                    for (final String f2 : shop.s.getConfigurationSection("Items." + item + ".OnBuy.GiveItem").getKeys(false)) {
                        try {
                            final ItemStack i = new ItemStack(Material.matchMaterial(f2), shop.s.getInt("Items." + item + ".OnBuy.GiveItem." + f2 + ".Amount"));
                            final ItemMeta m = i.getItemMeta();
                            final String name = ChatColor.translateAlternateColorCodes('&', shop.s.getString("Items." + item + ".OnBuy.GiveItem." + f2 + ".Name").replace("%player%", p.getName()).replace("%item%", item).replace("%cost%", new StringBuilder(String.valueOf(cost)).toString()));
                            m.setDisplayName(name);
                            if (shop.s.getString("Items." + item + ".OnBuy.GiveItem." + f2 + ".Lore") != null) {
                                final List<String> lore = (List<String>)shop.s.getStringList("Items." + item + ".OnBuy.GiveItem." + f2 + ".Lore");
                                final List<String> wd = new ArrayList<String>();
                                for (final String w : lore) {
                                    wd.add(ChatColor.translateAlternateColorCodes('&', w.replace("%item%", item).replace("%player%", p.getName()).replace("%cost%", new StringBuilder(String.valueOf(cost)).toString())));
                                }
                                if (wd != null) {
                                    m.setLore((List)wd);
                                }
                            }
                            m.setUnbreakable(shop.s.getBoolean("Items." + item + ".OnBuy.GiveItem." + f2 + ".Unbreakable"));
                            if (shop.s.getBoolean("Items." + item + ".OnBuy.GiveItem." + f2 + ".HideEnchants")) {
                                m.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
                            }
                            try {
                                if (shop.s.getString("Items." + item + ".OnBuy.GiveItem." + f2 + ".Enchants") != null) {
                                    for (final String s : InsanityRun.shop.getStringList("Items." + item + ".OnBuy.GiveItem." + f2 + ".Enchants")) {
                                        final String ench = replaceAllEnchantsNamesToRealNames(s.replaceAll(":", "").replaceAll(" ", "").replaceAll("[0-9]+", ""));
                                        final int num = GameManager.getInt(s.replaceAll(":", "").replaceAll("[A-Za-z]+", "").replaceAll(" ", "").replaceAll("_", ""));
                                        m.addEnchant(Enchantment.getByName(ench), num, true);
                                    }
                                }
                            }
                            catch (Exception e) {
                                Bukkit.getLogger().warning("Error when setting on item from InsanityRun Shop enchants, ShopItem: " + item + ", GiveItem: " + f2);
                            }
                            i.setItemMeta(m);
                            p.getInventory().addItem(new ItemStack[] { i });
                        }
                        catch (Exception e2) {
                            Bukkit.getLogger().warning("Error when giving item from InsanityRun Shop to player " + p.getName() + ", ShopItem: " + item + ", GiveItem: " + f2);
                        }
                    }
                }
            }
        }
    }
}

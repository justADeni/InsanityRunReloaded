import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerShopClick extends PlayerListener
{
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        final Player p = (Player)e.getWhoClicked();
        if (e.getInventory() == shop.iven.get(p)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getRawSlot() == 52) {
                    e.getWhoClicked().getOpenInventory().close();
                }
                else if (shop.items.get(e.getCurrentItem()) != null) {
                    shop.buyItem(shop.items.get(e.getCurrentItem()), (Player)e.getWhoClicked());
                    shop.updateGui((Player)e.getWhoClicked(), e.getWhoClicked().getOpenInventory());
                }
            }
        }
    }
}

import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class ClickSignListener implements Listener
{
    private boolean isSign(final Material m) {
        return m == Material.SIGN || m == Material.WALL_SIGN || m == Material.LEGACY_WALL_SIGN || m == Material.LEGACY_SIGN_POST || m == Material.LEGACY_SIGN;
    }
    
    @EventHandler
    public void interact(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.isSign(clickedBlock.getType())) {
            SignManager.processAction(clickedBlock, player);
        }
    }
}

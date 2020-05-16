import org.bukkit.event.EventHandler;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class DestroySignListener implements Listener
{
    @EventHandler
    public void interact(final BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            if (InsanityRun.playerObject.get(event.getPlayer().getName()) != null && !InsanityRun.playerObject.get(event.getPlayer().getName()).getInGame()) {
                SignManager.removeSign((Sign)event.getBlock().getState());
            }
            SignManager.removeSign((Sign)event.getBlock().getState());
        }
    }
}

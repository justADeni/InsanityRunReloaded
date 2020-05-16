import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

public class TaskManager implements Runnable
{
    @Override
    public void run() {
        for (final String playerName : InsanityRun.playerObject.keySet()) {
            final Player player = Bukkit.getServer().getPlayer(playerName);
            if (player != null) {
                final Location loc = player.getLocation();
                final iPlayer playerObject = InsanityRun.playerObject.get(playerName);
                int tempIdleCount = 0;
                final iPlayer currentPlayerObject = InsanityRun.playerObject.get(playerName);
                if (!currentPlayerObject.getInGame()) {
                    continue;
                }
                final int lastX = playerObject.getIdleX();
                final int lastZ = playerObject.getIdleZ();
                final int locX = (int)loc.getX();
                final int locZ = (int)loc.getZ();
                playerObject.setIdleX(locX);
                GameManager.setScoreboard(player);
                playerObject.setIdleZ(locZ);
                if (locX == lastX && locZ == lastZ) {
                    tempIdleCount = playerObject.getIdleCount();
                    playerObject.setIdleCount(++tempIdleCount);
                }
                else {
                    playerObject.setIdleCount(0);
                }
                if (playerObject.getFrozen()) {
                    playerObject.setIdleCount(0);
                }
                if (!playerObject.getInGame()) {
                    playerObject.setIdleCount(0);
                }
                if (tempIdleCount < InsanityRun.idleKickTime || playerObject.getFrozen()) {
                    continue;
                }
                if (!playerObject.getInGame()) {
                    continue;
                }
                final String arenaName = playerObject.getInArena();
                final iPlayer currentPlayerObject2 = InsanityRun.playerObject.get(playerName);
                currentPlayerObject2.setInGame(false);
                player.teleport(playerObject.getSignClickLoc());
                GameManager.gameOver(player, playerObject.getInArena(), playerObject);
                InsanityRun.plugin.getServer().getPlayer(playerName).sendMessage(ChatColor.translateAlternateColorCodes('&', InsanityRun.plugin.getConfig().getString(String.valueOf(InsanityRun.useLanguage) + ".idleKickText")));
                GameManager.refundMoney(arenaName, player);
            }
            InsanityRun.playerObject.keySet().remove(playerName);
        }
    }
}

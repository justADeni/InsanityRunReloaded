import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

public class Coins
{
    static FileConfiguration s;
    
    static {
        Coins.s = InsanityRun.players;
    }
    
    public static void take(final Player p, final int m) {
        final double money = Coins.s.getInt("Players." + p.getName()) - m;
        Coins.s.set("Players." + p.getName(), (Object)money);
        InsanityRun.savePl();
    }
    
    public static void give(final Player p, final int m) {
        final double money = Coins.s.getInt("Players." + p.getName()) + m;
        Coins.s.set("Players." + p.getName(), (Object)money);
        InsanityRun.savePl();
    }
    
    public static int bal(final Player p) {
        return Coins.s.getInt("Players." + p.getName());
    }
    
    public static boolean has(final Player p, final int m) {
        return bal(p) >= m;
    }
}

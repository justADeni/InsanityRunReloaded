import java.awt.Point;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class iPlayer
{
    private String playerName;
    private String inArena;
    private int coinsCollected;
    private int lastLocX;
    private int lastLocY;
    private int lastLocZ;
    private String arenaWorld;
    private int idleLocX;
    private int idleLocZ;
    private Boolean inGame;
    private Boolean isFrozen;
    private Long lastMovedTime;
    private Long startRaceTime;
    private ItemStack helmetWorn;
    private Location signClickLoc;
    private ArrayList<Point> goldWalked;
    private Location lastCheckpoint;
    private int idleCount;
    
    public iPlayer() {
        this.goldWalked = new ArrayList<Point>();
    }
    
    public int getIdleCount() {
        return this.idleCount;
    }
    
    public void setIdleCount(final int idleCount) {
        this.idleCount = idleCount;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public Location getLastCheckpoint() {
        return this.lastCheckpoint;
    }
    
    public void setLastCheckpoint(final Location loc) {
        this.lastCheckpoint = loc;
    }
    
    public String getInArena() {
        return this.inArena;
    }
    
    public void setInArena(final String Arena) {
        this.inArena = Arena;
    }
    
    public int getCoins() {
        return this.coinsCollected;
    }
    
    public void setCoins(final int coins) {
        this.coinsCollected = coins;
    }
    
    public int getLastX() {
        return this.lastLocX;
    }
    
    public void setLastX(final int lastX) {
        this.lastLocX = lastX;
    }
    
    public int getLastY() {
        return this.lastLocY;
    }
    
    public void setLastY(final int lastY) {
        this.lastLocY = lastY;
    }
    
    public int getLastZ() {
        return this.lastLocZ;
    }
    
    public void setLastZ(final int lastZ) {
        this.lastLocZ = lastZ;
    }
    
    public String getArenaWorld() {
        return this.arenaWorld;
    }
    
    public void setArenaWorld(final String world) {
        this.arenaWorld = world;
    }
    
    public int getIdleX() {
        return this.idleLocX;
    }
    
    public void setIdleX(final int idleX) {
        this.idleLocX = idleX;
    }
    
    public int getIdleZ() {
        return this.idleLocZ;
    }
    
    public void setIdleZ(final int idleZ) {
        this.idleLocZ = idleZ;
    }
    
    public Boolean getInGame() {
        return this.inGame;
    }
    
    public void setInGame(final boolean inGame) {
        this.inGame = inGame;
    }
    
    public Boolean getFrozen() {
        return this.isFrozen;
    }
    
    public void setFrozen(final boolean isFrozen) {
        this.isFrozen = isFrozen;
    }
    
    public Long getLastMovedTime() {
        return this.lastMovedTime;
    }
    
    public void setLastMovedTime(final Long lastMovedTime) {
        this.lastMovedTime = lastMovedTime;
    }
    
    public Long getStartRaceTime() {
        return this.startRaceTime;
    }
    
    public void setStartRaceTime(final Long startRaceTime) {
        this.startRaceTime = startRaceTime;
    }
    
    public ItemStack getHelmetWorn() {
        return this.helmetWorn;
    }
    
    public void setHelmetWorn(final ItemStack helmetWorn) {
        this.helmetWorn = helmetWorn;
    }
    
    public Location getSignClickLoc() {
        return this.signClickLoc;
    }
    
    public void setSignClickLoc(final Location signClickLoc) {
        this.signClickLoc = signClickLoc;
    }
    
    public ArrayList<Point> getGoldWalkedArray() {
        return this.goldWalked;
    }
    
    public void setGoldWalkedArray(final ArrayList<Point> arrList) {
        this.goldWalked = arrList;
    }
    
    public void clearGoldWalkedArray() {
        this.goldWalked.clear();
    }
}

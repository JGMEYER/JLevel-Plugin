package com.jmeyer.bukkit.jlevel;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * Sample plugin for Bukkit
 *
 * @author JMEYER
 */
public class JLevelPlugin extends JavaPlugin {
    private final JLevelPlayerListener playerListener = new JLevelPlayerListener(this);
    private final JLevelBlockListener blockListener = new JLevelBlockListener(this);
    private final JLevelEntityListener entityListener = new JLevelEntityListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    public JLevelPlugin(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }

    public void onDisable() {
    	PluginDescriptionFile pdfFile = this.getDescription();
    	System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }

    public void onEnable() {
    	// TODO: Eliminate need for these - move to DatabaseManager
    	// Create JLevel directories
    	FileManager.createRootDirectoryIfNotExists();
    	FileManager.createMainPlayerDirectoryIfNotExists();
    	FileManager.createMainSkillDirectoryIfNotExists();
    	
    	DatabaseManager.createDirectoriesIfNotExists();
    	
    	// Create default skills if not already added
    	String[] miningItemRules = {"270:0","274:5","285:10","257:25","278:40"};
    	String[] miningExpRules = {"blockbreak:4:-1:0","blockbreak:1:-1:3","blockbreak:48:-1:0","blockbreak:16:-1:15","blockbreak:15:-1:25","blockbreak:21:-1:40","blockbreak:14:-1:35","blockbreak:73:-1:40","blockbreak:74:-1:30","blockbreak:56:-1:50","blockbreak:49:-1:30"};
    	String[] miningExpTable = {"1:83","2:91","3:102","4:112","5:124","6:138","7:151","8:168","9:185","10:204","11:226","12:249","13:274","14:304","15:335","16:369","17:408","18:450","19:497","20:548","21:606","22:667","23:737","24:814","25:898","26:990","27:1094","28:1207","29:1332","30:1470","31:1623","32:1791","33:1977","34:2182","35:2409","36:2658","37:2935","38:3240","39:3576","40:3947","41:4358","42:4810","43:5310","44:5863","45:6471","46:7144","47:7887","48:8707","49:9612"};
    	FileManager.createNewSkillSetIfNotExists("Mining", miningItemRules, miningExpRules, miningExpTable);
    	DatabaseManager.updateSkill("Mining", miningItemRules, miningExpRules, miningExpTable, false);
    	// DatabaseManager.createSkillDatabaseIfNotExists("Mining", miningItemRules, miningExpRules, miningExpTable);
    	String[] loggingItemRules = {"0:1","271:3","275:5","286:10","258:25","279:40"};
    	String[] loggingExpRules = {"blockbreak:17:0:5","blockbreak:17:1:10","blockbreak:17:2:15"};
    	String[] loggingExpTable = {"1:83","2:91","3:102","4:112","5:124","6:138","7:151","8:168","9:185","10:204","11:226","12:249","13:274","14:304","15:335","16:369","17:408","18:450","19:497","20:548","21:606","22:667","23:737","24:814","25:898","26:990","27:1094","28:1207","29:1332","30:1470","31:1623","32:1791","33:1977","34:2182","35:2409","36:2658","37:2935","38:3240","39:3576","40:3947","41:4358","42:4810","43:5310","44:5863","45:6471","46:7144","47:7887","48:8707","49:9612"};
    	DatabaseManager.updateSkill("Logging", loggingItemRules, loggingExpRules, loggingExpTable, false);
    	// FileManager.createNewSkillSetIfNotExists("Logging", loggingItemRules, loggingExpRules, loggingExpTable);
    	
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Priority.Normal, this);

        // Announce to show all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}
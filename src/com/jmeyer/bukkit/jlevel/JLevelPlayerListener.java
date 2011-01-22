
package com.jmeyer.bukkit.jlevel;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author JMEYER
 */
public class JLevelPlayerListener extends PlayerListener {
    private final JLevelPlugin plugin;

    public JLevelPlayerListener(JLevelPlugin instance) {
        plugin = instance;
    }

    @Override
    public void onPlayerJoin(PlayerEvent event) {
    	FileManager.createPlayerDatasheetIfNotExists(event.getPlayer());
    	DatabaseManager.createPlayerDatabaseIfNotExists(event.getPlayer());
    }

    @Override
    public void onPlayerQuit(PlayerEvent event) {
    }

    // TODO: implement with SQLite queries
    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
        String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();

    	if (split[0].equalsIgnoreCase("/debugskills")) {
            plugin.setDebugging(player, !plugin.isDebugging(player));

            event.setCancelled(true);
        } else if (split[0].equalsIgnoreCase("/stats")) {
        	ArrayList<String> lines = DatabaseManager.getStatLinesToOutput(player.getName());
        	
        	player.sendMessage(player.getName() + ":");
        	for (String line : lines) {
        		player.sendMessage(line);
        	}
        	
        	event.setCancelled(true);
        	/*
        	if (split.length == 2) {
        		ArrayList<String> lines = FileManager.getStatsToOutput(split[1]);
	        	
        		if (lines.size() > 0) {
		        	player.sendMessage(split[1] + ":");
		        	for (String line : lines) {
		        		player.sendMessage(line);
		        	}
        		} else {
        			player.sendMessage("Player stats not found.");
	        	}
	        	
	        	event.setCancelled(true);
        	} else {
        		ArrayList<String> lines = FileManager.getStatsToOutput(player.getName());
	        	
	        	player.sendMessage(player.getName() + ":");
	        	for (String line : lines) {
	        		player.sendMessage(line);
	        	}
	        	
	        	event.setCancelled(true);
        	}
        	*/
        }
    }
    
}

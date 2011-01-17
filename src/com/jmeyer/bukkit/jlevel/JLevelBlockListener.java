
package com.jmeyer.bukkit.jlevel;

import java.util.ArrayList;

import org.bukkit.block.BlockDamageLevel;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Sample block listener
 * @author JMEYER
 */
public class JLevelBlockListener extends BlockListener {
    private final JLevelPlugin plugin;

    public JLevelBlockListener(final JLevelPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
    	int itemID = event.getPlayer().getItemInHand().getTypeId();
    	if (FileManager.playerCanUseItem(event.getPlayer(), itemID)) {
	    	if (event.getDamageLevel() == BlockDamageLevel.BROKEN) {
	    		ArrayList<String> skills = FileManager.getRelatedSkillsForItem(itemID);
	    		
	    		// String message = "Item " + itemID + " relates to: "; //
	        	for (String skill : skills) {
	        		// message += skill + " "; //
	        		
	        		int exp = FileManager.getExperienceGainedFromAction(skill, "blockbreak", event.getBlock().getTypeId());
	        		FileManager.addExperience(event.getPlayer(), skill, exp);
	        	}
	        	// event.getPlayer().sendMessage(message); //
	    	}    	
    	} else {
    		event.setCancelled(true);
    	}
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
    	int blockID = event.getBlock().getTypeId();
    	
    	// disallow players from farming exp by placing iron or gold ore
    	if (blockID == 14 || blockID == 15) {
    		event.setCancelled(true);
    	}
    }
    
}

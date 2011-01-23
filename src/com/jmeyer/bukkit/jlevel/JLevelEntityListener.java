package com.jmeyer.bukkit.jlevel;

import java.util.ArrayList;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityListener;

public class JLevelEntityListener extends EntityListener {
    private final JLevelPlugin plugin;

    public JLevelEntityListener(JLevelPlugin instance) {
        plugin = instance;
    }
    
    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	Entity damager = event.getDamager();
    	
    	if (damager instanceof Player) {
    		Player player = (Player)damager;
    		int itemId = player.getItemInHand().getTypeId();
        	
        	if (DatabaseManager.playerCanUseItem(player, itemId)) {		
	        	if (event.getEntity() instanceof LivingEntity) {
	        		LivingEntity le = (LivingEntity)event.getEntity();
	        		String leClass = event.getEntity().getClass().getName();
        			String leName = leClass.substring(leClass.indexOf(".Craft") + 6, leClass.length());
	        		
	        		if ((le.getHealth()-event.getDamage() < 0) && (le.getHealth() >= 0)) {
	        			ArrayList<String> skills = DatabaseManager.relatedSkillsForItem(itemId);
	        			
	        			for (String skill : skills) {
	        				int exp = DatabaseManager.getExperienceGainedFromAction(skill, "entitykill", leName, "");
	        				DatabaseManager.addExperience(player, skill, exp);
	        			}
	        		}
	        	}
        	} else {
        		event.setCancelled(true);
        	}
    	}
    	
    	
    	
    	// MobType creeper = MobType.CREEPER; 
    	// creeper == MobType.valueOf(creeper.toString())
    	
    	// System.out.println("Entity: \n(" + event.getEntity().getEntityId() + ") \n" + event.getEntity().toString());
    	// System.out.println("Damager: \n(" + event.getDamager().getEntityId() + ") \n" + event.getDamager().toString());
    }
    
}

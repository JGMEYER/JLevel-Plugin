package com.jmeyer.bukkit.jlevel;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
    	System.out.println("registered hit");
    	Entity damager = event.getDamager();	
    	
    	if (damager instanceof Player) {
    		Player player = (Player)damager;
    		// player.sendMessage("detected as player hit");
    		
        	if (event.getEntity() instanceof LivingEntity) {
        		// player.sendMessage("detected as livingentity");
        		LivingEntity le = (LivingEntity)event.getEntity();
        		
        		if ((le.getHealth() <= 0) && (event.getDamager() instanceof Player)) {
        			player.sendMessage("You killed entity " + le.getEntityId());
        		}
        	}
    	}
    	
    	
    	
    	// MobType creeper = MobType.CREEPER; 
    	// creeper == MobType.valueOf(creeper.toString())
    	
    	// System.out.println("Entity: \n(" + event.getEntity().getEntityId() + ") \n" + event.getEntity().toString());
    	// System.out.println("Damager: \n(" + event.getDamager().getEntityId() + ") \n" + event.getDamager().toString());
    }
    
}

package com.jmeyer.bukkit.jlevel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

// TODO: Convert everything to SQLite

public class FileManager {
	
	public static void createRootDirectoryIfNotExists() {
		File playerDirectory = new File("JLevel Files");
		
		if (!playerDirectory.exists()) {
			playerDirectory.mkdir();
		}
	}
	
	public static void createMainSkillDirectoryIfNotExists() {
		File playerDirectory = new File("JLevel Files/Skills");
		
		if (!playerDirectory.exists()) {
			playerDirectory.mkdir();
		}
	}
	
	public static void createMainPlayerDirectoryIfNotExists() {
		File playerDirectory = new File("JLevel Files/Players");
		
		if (!playerDirectory.exists()) {
			playerDirectory.mkdir();
		}
	}
	
	public static void createNewSkillSetIfNotExists(String name, String[] itemRules, String[] expRules, String[] expTable) {
		String skillDirectoryName = "JLevel Files/Skills/" + name;
		File skillDirectory = new File(skillDirectoryName);
		File itemRulesFile = new File(skillDirectoryName + "/Items.txt");
		File expRulesFile = new File(skillDirectoryName + "/ExpRules.txt");
		File expTableFile = new File(skillDirectoryName + "/ExpTable.txt");
		
		if (!skillDirectory.exists()) {
			skillDirectory.mkdir();
		}
	
		if (!itemRulesFile.exists()) {
			createFileWithLines(itemRulesFile, itemRules);
		}
		
		if (!expRulesFile.exists()) {
			createFileWithLines(expRulesFile, expRules);
		}
		
		if (!expTableFile.exists()) {
			createFileWithLines(expTableFile, expTable);
		}
			
	}
	
	public static void createPlayerDatasheetIfNotExists(Player player) {
		String name = player.getName();
		
		if (!playerHasDatasheet(player)) {
		    File datasheet = playerDatasheet(player);
		    
		    BufferedWriter output = null;
		    try {
				output = new BufferedWriter(new FileWriter(datasheet));
				output.write("#skill:<skillname>:<skilllvl>:<levelexp>:<tnlexp>:<totalexp>");
				output.newLine();
				output.close();
				System.out.println("[JLEVEL] Created new datasheet for user: " + name);
			} catch (IOException e) {
				System.out.println("[JLEVEL] Failed to create new datasheet for user: " + name);
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	public static ArrayList<String> getRelatedSkillsForItem(int itemID) {
		File root = new File("JLevel Files/Skills");
        String[] allSkills = root.list();
        ArrayList<String> relatedSkills = new ArrayList<String>();
		
        for (String skill : allSkills) {
        	if (itemRelatesToSkill(skill, itemID)) {
        		relatedSkills.add(skill);
        	}
        }
        
		return relatedSkills;
	}
	
	public static boolean playerCanUseItem(Player player, int itemID) {
		ArrayList<String> relatedSkills = getRelatedSkillsForItem(itemID);
		boolean canUse = true;
		
		for (String skill : relatedSkills) {
			File skillFile = new File("JLevel Files/Skills/" + skill + "/Items.txt");
			try {
				Scanner scanner = new Scanner(skillFile);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
	                
	                if (!(line.charAt(0) == '#')) {
	                	String[] values = line.split(":",2);
	                	if (Integer.parseInt(values[0]) == itemID) {
	                		int reqLevel = Integer.parseInt(values[1]);
		                	if (getPlayerSkillLevel(player, skill) < reqLevel) {
		                		player.sendMessage("You must be at least level " + reqLevel + " of the " + ChatColor.YELLOW + skill + ChatColor.WHITE + " skill to use this.");
		                		canUse = false;
		                	}
	                	}
	                }
				}
			} catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		
		return canUse;
	}
	
	public static int getPlayerSkillLevel(Player player, String skill) {
		File skillFile = new File("JLevel Files/Players/" + player.getName() +".txt");
		try {
			Scanner scanner = new Scanner(skillFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
                
                if (!(line.charAt(0) == '#')) {
                	String[] values = line.split(":",6);
                	if (values[0].equals("skill") && values[1].equals(skill)) {
                		return Integer.parseInt(values[2]);
                	}
                }
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
		
		return 1;
	}
	
	private static boolean itemRelatesToSkill(String skill, int itemID) {
		String skillFileName = "JLevel Files/Skills/" + skill + "/Items.txt";
		File skillFile = new File(skillFileName);
		
		try {
			Scanner scanner = new Scanner(skillFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
                
                if (!(line.charAt(0) == '#')) {
                	String[] values = line.split(":",2);
                	if (values[0].equals(""+itemID)) {
                		return true;
                	}
                }
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
		
		return false;
	}
	
	// Use only once skill has been added, or when skill is being added
	private static int getSkillExperienceNeededForLevel(String skill, int level) {
		String skillFileName = "JLevel Files/Skills/" + skill + "/ExpTable.txt";
		File skillFile = new File(skillFileName);
		
		try {
			Scanner scanner = new Scanner(skillFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
                
                if (!(line.charAt(0) == '#')) {
                	String[] values = line.split(":",2);
                	if (values[0].equals(""+level)) {
                		return Integer.parseInt(values[1]);
                	}
                }
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
		
		return -1;
	}
	
	public static int getExperienceGainedFromAction(String skill, String action, int itemDestroyed) {
		String skillFileName = "JLevel Files/Skills/" + skill + "/ExpRules.txt";
		File skillFile = new File(skillFileName);
		
		try {
			Scanner scanner = new Scanner(skillFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
                
                if (!(line.charAt(0) == '#')) {
                	String[] values = line.split(":",3);
                	if (values[0].equals(action) && values[1].equals(""+itemDestroyed)) {
                		return Integer.parseInt(values[2]);
                	}
                }
			}
		} catch (Exception e) {
            e.printStackTrace();
        } 
		
		return 0;
	}
	
	
	
	
	
	public static void addExperience(Player player, String skill, int amount) {
		String playerFileName = "JLevel Files/Players/" + player.getName() + ".txt";
		File playerFile = new File(playerFileName);
		ArrayList<String> newLines = new ArrayList<String>();
		
		// TODO: add subtracting exp later
		if (amount > 0) {
			try {
	            Scanner scanner = new Scanner(playerFile);
	            boolean lineEdited = false;
	            while (scanner.hasNextLine()) {
	                String line = scanner.nextLine();
	                
	                if (!(line.charAt(0) == '#')) {
	                	String[] values = line.split(":",6);
	                	if (values[0].equals("skill") && values[1].equals(skill)) {
	                		lineEdited = true;
	                		int newLvl = Integer.parseInt(values[2]);
	                		int newLvlExp = Integer.parseInt(values[3]);
	                		int newTotalExp = Integer.parseInt(values[5]);
	                		int newExpToNextLevel = Integer.parseInt(values[4]);
	                		
	                		// Add exp if not max level
	                		if (newExpToNextLevel > 0) {
	                			newLvlExp += amount;
	                			newTotalExp += amount;
	                			player.sendMessage("+(" + amount + ") " + skill);
	                		}
	                		
	                		// while loop allows for multi level
	                		while (newLvlExp > newExpToNextLevel && newExpToNextLevel > 0) {
	                			// level up
	                			newLvlExp = newLvlExp - newExpToNextLevel;
	                			newLvl++;
	                			newExpToNextLevel = getSkillExperienceNeededForLevel(skill, newLvl);
	                			player.sendMessage("Level up! You are now level " + newLvl + " of the " + ChatColor.YELLOW + skill + ChatColor.WHITE + " skill.");
	                		}
	                		newLines.add(values[0] + ":" + values[1] + ":" + newLvl + ":" + newLvlExp + ":" + newExpToNextLevel + ":" + newTotalExp);
	                	} else {
	                		newLines.add(line);
	                	}
	                } else {
	                	newLines.add(line);
	                }
	            }
	            
	            // Add skill if not already learned
	            if (!lineEdited) {
	            	newLines.add("skill:" + skill + ":1:0:" + getSkillExperienceNeededForLevel(skill, 1) + ":0");
	            	player.sendMessage("You learned the " + ChatColor.YELLOW + skill + ChatColor.WHITE + " skill!");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        playerFile.delete();
	        createFileWithLines(new File(playerFileName), newLines);
		}
        
	}
	
	// TODO: playerSkillInfoExists(Player player)?
	
	
	
	// TODO: actually use these
	public static File skillDatasheet(String skill) {
		return new File("JLevel Files/Skills/" + skill + ".txt");
	}
	
	public static File playerDatasheet(Player player) {
		return new File("JLevel Files/Players/" + player.getName() + ".txt");
	}
	
	public static boolean playerHasDatasheet(Player player) {
		return new File("JLevel Files/Players/" + player.getName() + ".txt").exists();
	}
	
	
	
	
	// TODO: clean up
	public static ArrayList<String> getStatsToOutput(String player) {
		File root = new File("JLevel Files/Skills");
        String[] allSkills = root.list();
		
        String playerFileName = "JLevel Files/Players/" + player + ".txt";
		File playerFile = new File(playerFileName);
		
		ArrayList<String> statLines = new ArrayList<String>();
		
		try {
			Scanner scanner = new Scanner(playerFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
                
                if (!(line.charAt(0) == '#')) {
                	String[] values = line.split(":",6);
                	if (values[0].equals("skill")) {
                		for (String skill: allSkills) {
                			if (values[1].equals(skill)) {
                				double currExp = Integer.parseInt(values[3]);
                				double reqExpTNL = Integer.parseInt(values[4]);
                				int totalExp = Integer.parseInt(values[5]);
                				String newStat = ChatColor.WHITE + "[" + ChatColor.AQUA;
                				int expLines = (int)((20*currExp)/reqExpTNL);
                				int blankLines = 20-expLines;
                				
                				if (reqExpTNL > 0) {
	                				for (int i = 0; i < expLines; i++) {
	                					newStat += "||";
	                				}
	                				
	                				for (int i = 0; i < blankLines; i++) {
	                					newStat += " ";
	                				}
	                				
	                				newStat += ChatColor.WHITE + "] " + (int)(currExp) + "/" + (int)(reqExpTNL) + " (" + (int)((currExp/reqExpTNL)*100) + "%) " + ChatColor.YELLOW + values[1] + ChatColor.WHITE + " lvl" + values[2];
                				} else {
                					for (int i = 0; i < 20; i++) {
	                					newStat += "||";
	                				}
                					
                					newStat += ChatColor.WHITE + "] " + totalExp + "/" + totalExp + " (100%) " + ChatColor.YELLOW + values[1] + ChatColor.RED + " (MAX)"; 
                				}
                				
                				statLines.add(newStat);
                			}
                		}
                	}
                }
			}
		} catch (Exception e) {
            // e.printStackTrace();
			System.out.println("Failed to find user: " + player);
        }
        
		return statLines;
	}
	
	
	
	
	
	private static void createFileWithLines(File file, String[] lines) {
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			for (String current : lines) {
				output.write(current);
				output.newLine();
			}			
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void createFileWithLines(File file, ArrayList<String> lines) {
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			for (String current : lines) {
				output.write(current);
				output.newLine();
			}			
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

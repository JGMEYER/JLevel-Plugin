package com.jmeyer.bukkit.jlevel;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

/**
 * Handle calls to SQLite Database
 * @author JMEYER
 * 
 * Special Thanks:
 * - tkelly: suggesting SQLite, offering IRC assistance, providing sample source
 * - thegleek: suggesting SQLite, offering IRC assistance
 */
public class DatabaseManager {
	
	public static final Logger LOG = Logger.getLogger("Minecraft");
	public static final String ROOT_DIRECTORY = "JLevel-Data";
	public static final String PLAYER_DB_DIRECTORY = "jdbc:sqlite:" + ROOT_DIRECTORY + File.separator;
	public static final String SKILL_DB_DIRECTORY = "jdbc:sqlite:" + ROOT_DIRECTORY + File.separator;
	
	// TODO: remove this. doesn't allow for name change.
	/*
	private final static String WARP_TABLE = "CREATE TABLE `warpTable` (" + "`id` INTEGER PRIMARY KEY," + "`name` varchar(32) NOT NULL DEFAULT 'warp',"
		+ "`creator` varchar(32) NOT NULL DEFAULT 'Player'," + "`world` tinyint NOT NULL DEFAULT '0'," + "`x` DOUBLE NOT NULL DEFAULT '0',"
		+ "`y` tinyint NOT NULL DEFAULT '0'," + "`z` DOUBLE NOT NULL DEFAULT '0'," + "`yaw` smallint NOT NULL DEFAULT '0'," + "`pitch` smallint NOT NULL DEFAULT '0'," + "`publicAll` boolean NOT NULL DEFAULT '1',"
		+ "`permissions` varchar(150) NOT NULL DEFAULT ''," + "`welcomeMessage` varchar(100) NOT NULL DEFAULT ''" +");";
	*/
	
	public static void createPlayerTableIfNotExists(Player player) {
		if (!playerTableExists(player)) {
			Connection conn = null;
			Statement st = null;
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(playerDatabasePath(player));
				st = conn.createStatement();
				
				String update = "CREATE TABLE `" + player.getName() + "` (" +
					"`id` INTEGER PRIMARY KEY," +
					"`skillName` varchar(32)," +
					"`skillLevel` INTEGER," +
					"`levelExp` INTEGER," +
					"`nextLevelExp` INTEGER," +
					"`totalExp` INTEGER" + ");";
				
				st.executeUpdate(update);
			} catch (SQLException e) {
				LOG.log(Level.SEVERE, "[JLEVEL]: Create Table Exception", e);
			} catch (ClassNotFoundException e) {
				LOG.log(Level.SEVERE, "[JLEVEL]: Error loading org.sqlite.JDBC");
			} finally {
				try {
					if (conn != null)
						conn.close();
					if (st != null)
						st.close();
				} catch (SQLException e) {
					LOG.log(Level.SEVERE, "[JLEVEL]: Could not create the table (on close)");
				}
			}
		}
	}
	
	public static void createRootDirectoryIfNotExists() {
		File playerDirectory = new File("JLevel-Data");
		
		if (!playerDirectory.exists()) {
			playerDirectory.mkdir();
		}
	}
	
	public static boolean playerTableExists(Player player) {
		Connection conn = null;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(playerDatabasePath(player));
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, player.getName(), null);
			if (!rs.next())
				return false;
			return true;
		} catch (SQLException ex) {
			LOG.log(Level.SEVERE, "[JLEVEL]: Table Check Exception", ex);
			return false;
		} catch (ClassNotFoundException e) {
			LOG.log(Level.SEVERE, "[JLEVEL]: Error loading org.sqlite.JDBC");
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				LOG.log(Level.SEVERE, "[JLEVEL]: Table Check SQL Exception (on closing)");
			}
		}
	}
	
	private static String playerDatabasePath(Player player) {
		return PLAYER_DB_DIRECTORY + player.getName() + ".db";
	}
	
	private static String skillDatabasePath(String skill) {
		return SKILL_DB_DIRECTORY + skill + ".db";
	}
	
}

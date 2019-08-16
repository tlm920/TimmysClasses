package me.frostedtimmy.yisusclasses.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.frostedtimmy.yisusclasses.YisusClasses;

public class MysqlSetterGetter implements Listener {
	private Map<String, SavedData> mSavedDataMap = new HashMap<>();
	
	class SavedData {
		SavedData(String playerName, int classid) {
			this.playerName = playerName;
			this.classid = classid;
		}
		
		String playerName;
		int classid;
	}
	
	YisusClasses plugin = YisusClasses.getPlugin(YisusClasses.class);
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (!playerExists(player.getUniqueId())) {
			createPlayer(player.getUniqueId(), player);
		}
	}
	
	public boolean playerExists(UUID uuid) {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			
			ResultSet results = statement.executeQuery();
			if(results.next()) {
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void createPlayer(final UUID uuid, Player player) {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			
			ResultSet results = statement.executeQuery();
			results.next();
			
			if (playerExists(uuid) != true) {
				PreparedStatement insert = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.table + "(UUID,NAME,CLASSID) VALUE (?,?,?)");
				insert.setString(1, uuid.toString());
				insert.setString(2, player.getName());
				insert.setInt(3, 0);
				insert.executeUpdate();
				
				SavedData mySavedData = new SavedData(player.getName(), 0);
				mSavedDataMap.put(uuid.toString(), mySavedData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateClassid(UUID uuid, int classid) {
		try {
			SavedData data = mSavedDataMap.get(uuid.toString());
			if(data != null) {
				data.classid = classid;
			}
			mSavedDataMap.put(uuid.toString(), data);
			
			PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET CLASSID=? WHERE UUID=?");
			statement.setInt(1, classid);
			statement.setString(2, uuid.toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateData(UUID uuid, int classid) {
		SavedData data = mSavedDataMap.get(uuid.toString());
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(data != null) {
					data.classid = classid;
				}
				
			}
		};
	}
	
	public int getClassid(UUID uuid) {
		SavedData data = mSavedDataMap.get(uuid.toString());
		if( data != null) {
			return data.classid;
		}
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			
			ResultSet results = statement.executeQuery();
			results.next();
			int classId = results.getInt("CLASSID");
			return classId;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
	}
	
}

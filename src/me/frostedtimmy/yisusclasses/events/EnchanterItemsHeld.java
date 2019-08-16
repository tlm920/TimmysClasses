package me.frostedtimmy.yisusclasses.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.massivecraft.factions.entity.MPlayer;

import me.frostedtimmy.yisusclasses.YisusClasses;
import me.frostedtimmy.yisusclasses.util.MysqlSetterGetter;

public class EnchanterItemsHeld implements Listener {
	YisusClasses plugin = YisusClasses.getPlugin(YisusClasses.class);
	MysqlSetterGetter sql = new MysqlSetterGetter();
	
	public HashMap<String, Long> cooldownDebuffRC = new HashMap<String, Long>();
	public HashMap<String, Long> cooldownDebuffLC = new HashMap<String, Long>();
	
	public HashMap<String, Long> cooldownHealRC = new HashMap<String, Long>();
	public HashMap<String, Long> cooldownSpeedRC = new HashMap<String, Long>();
	
	int cooldownAllyTime = 15;
	int cooldownDebuffTime = 20;
	
	/**
	 * Gets all of the members in the players factions
	 * and puts it into an arraylist of players.
	 * 
	 * @param pl
	 * Player to get the faction from.
	 * @return
	 * The nearby faction members.
	 */
	public ArrayList<Player> getNearbyFactionMembers(Player pl) {
        ArrayList<Player> nearby = new ArrayList<Player>();
        MPlayer input = MPlayer.get(pl);
        
        double range = 15;
        for (Entity e : pl.getNearbyEntities(range, range, range)){
            if (e instanceof Player){
            	MPlayer mp = MPlayer.get(((Player) e).getPlayer());
            	if (mp.getFaction().equals(input.getFaction())) {
                    nearby.add((Player) e);
            	}
            }
        }
        return nearby;
    }
	
	/**
	 * Gets all of the nearby players in a radius
	 * 
	 * @param pl
	 * Player to get players around.
	 * @return
	 * Players around the specified player.
	 */
	public ArrayList<Player> getNearbyPlayers(Player pl) {
        ArrayList<Player> nearby = new ArrayList<Player>();
        double range = 15;
        for (Entity e : pl.getNearbyEntities(range, range, range)){
            if (e instanceof Player){
                nearby.add((Player) e);
            }
        }
        return nearby;
    }
	
	/**
	 * We check if the player is a classid 4, than we check
	 * if they are holding a type of hoe. Than we add some
	 * effects to the allies or enemys in a radius.
	 * 
	 * @param e
	 * Event.
	 * 
	 * @see
	 * getNearbyFactionMembers
	 * @see
	 * getNearbyPlayers
	 */
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Material hand = p.getInventory().getItemInMainHand().getType();
	
		ArrayList<Player> allies = getNearbyFactionMembers(p);
		ArrayList<Player> enemys = getNearbyPlayers(p);
		
		enemys.removeAll(allies);
		
		if (hand != Material.AIR && (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
			ItemStack is = e.getItem();
			
			if (sql.getClassid(p.getUniqueId()) == 4) {
				
				if (is != null && !is.getType().equals(Material.AIR) && is.getType().equals(Material.GOLD_HOE)) {
					for (Player pl : allies)
						pl.setHealth(pl.getHealth() + 4);
				}
			
				if (is != null && !is.getType().equals(Material.AIR) & is.getType().equals(Material.STONE_HOE)) {
					
					for (Player pl : enemys) {
						
						pl.addPotionEffect(PotionEffectType.GLOWING.createEffect(16, 0));
						pl.addPotionEffect(PotionEffectType.SLOW.createEffect(16, 0));
						
					}
				}
			}
		}		
	}
	
}

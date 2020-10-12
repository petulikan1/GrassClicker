
package me.petulikan1.grassclicker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.ConfigAPI.Config;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.SortedMap.RankingAPI;
import me.petulikan1.grassclicker.Command.GCC;

public final class GrassClicker extends JavaPlugin implements Listener {
    public static GrassClicker plugin;   
    public static RankingAPI<String, Integer> clicks, points, rebirths, playtime;
    public static Config config, translations;
    
    @Override
	public void onLoad() {
    	plugin = this;
    }
    	
   
    
    @Override
	public void onEnable() {
    	config=new Config("GrassClicker/Config.yml");
    	config.addDefault("Command.Aliases", Arrays.asList("grassclicker","grcl", "clicker", "click", "gclick", "gclicker"));
    	config.addDefault("Command.Permission", "GrassClicker.Command");
    	config.addDefault("Options.AntiAutoClicker.Enabled", true); 
    	config.addDefault("Options.AntiAutoClicker.Limit", 3500);
    	config.addDefault("Options.Upgrades.AntiAutoClicker.MaxLevel", 20);
    	config.addDefault("Options.Upgrades.AntiAutoClicker.BonusPerLevel", 1500); 
    	config.addDefault("Options.Upgrades.AntiAutoClicker.Cost", 3500);
    	config.save();
    	translations=new Config("GrassClicker/Translations.yml");
    	Bukkit.getPluginManager().registerEvents(this, this);
    	for (String c : config.getStringList("Command.Aliases")) {
    		TheAPI.createAndRegisterCommand(c, config.getString("Coommand.Permission"), new GCC());
    	}    	
    	(new Tasker() {
			@Override
			public void run() {
				HashMap<String, Integer> mapForClicks = Maps.newHashMap();
		        for(UUID playerUUID : TheAPI.getUsers())
		        	if(TheAPI.getUser(playerUUID).exists("clicker.clicks"))
		            mapForClicks.put(TheAPI.getUser(playerUUID).getName(), TheAPI.getUser(playerUUID).getInt("clicker.clicks"));
		        clicks = new RankingAPI<>(mapForClicks);
		        
		        
		        HashMap<String, Integer> mapForPoints = Maps.newHashMap();
		        for (UUID playerUUID : TheAPI.getUsers())
		        	if(TheAPI.getUser(playerUUID).exist("clicker.points"))
		        	mapForPoints.put(TheAPI.getUser(playerUUID).getName(), TheAPI.getUser(playerUUID).getInt("clicker.points"));
		        points = new RankingAPI<>(mapForPoints);
		        
		        HashMap<String, Integer> mapForRebirths = Maps.newHashMap();
		        for (UUID playerUUID : TheAPI.getUsers())
		        	if(TheAPI.getUser(playerUUID).exist("clicker.rebirths"))
		        	mapForRebirths.put(TheAPI.getUser(playerUUID).getName(), TheAPI.getUser(playerUUID).getInt("clicker.rebirths"));
		        rebirths = new RankingAPI<>(mapForRebirths);
		        
		        HashMap<String, Integer> mapForPlaytime = Maps.newHashMap();
		        for (UUID playerUUID : TheAPI.getUsers())
		        	if(TheAPI.getUser(playerUUID).exist("clicker.playtime"))
		        	mapForPlaytime.put(TheAPI.getUser(playerUUID).getName(), TheAPI.getUser(playerUUID).getInt("clicker.playtime"));
		        playtime = new RankingAPI<>(mapForPlaytime);
			}
		}).runTask();
    }
    
}

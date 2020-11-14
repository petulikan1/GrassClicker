
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
    	config.addDefault("Options.AntiAutoClicker.Multiplier", 3500);
    	config.addDefault("Options.Upgrades.AntiAutoClicker.BonusPerLevel", 1500); 
    	config.addDefault("Options.Upgrades.AntiAutoClicker.Cost", 3500);
    	config.addDefault("Options.Upgrades.AntiAutoClicker.PerClickAdd", 1);
    	config.addDefault("Options.VIP1.Permission", "clicker.vip1");
    	config.addDefault("Options.VIP1.Value", 3);
    	config.addDefault("Options.VIP2.Permission", "clicker.vip2");
    	config.addDefault("Options.VIP2.Value", 6);
    	config.addDefault("Options.Bonus.Default", 0);
    	config.addDefault("Options.Upgrades.BetterBlocks.Cost", 2500);
    	
    	config.save();
    	translations=new Config("GrassClicker/Translations.yml");
    	translations.addDefault("Clicker.Upgrades.AntiAutoClicker.Item.Name", "&eHigher Clicker Limit");
    	translations.addDefault("Clicker.Upgrades.AntiAutoClicker.Item.Lore", Arrays.asList("Level: %level%", "Price: %cost%"));
    	translations.addDefault("Clicker.Items.Upgrade.Name", "&bUpgrades");
    	translations.addDefault("Clicker.Items.Stats.Name", "&bStats");
    	translations.addDefault("Clicker.Items.Exit.Name", "&4Exit");
    	translations.addDefault("Clicker.Items.Back.Name", "&cBack");
    	translations.addDefault("Clicker.Items.CloseMessage", "&6&lS&f&lerver &8&l»&f&l Minigame was closed.");
    	translations.addDefault("Clicker.Items.Rebirth.Name", "&bRebirths");
    	translations.addDefault("Clicker.ConsoleSender", "&6&lS&f&lerver &8&l»&f&l This command can\'t be run from console!");
    	translations.addDefault("Clicker.Menu.Main.Name", "&aGrass Clicker");
    	translations.addDefault("Clicker.Menu.Main.ClickBlock.Lore", "&7Points: %points%");
    	translations.addDefault("Clicker.Menu.Main.ClickBlock.Name", "&eClick");    	
    	translations.addDefault("Clicker.Menu.Main.Cooldown.ItemName", "&eWait a little");
    	translations.addDefault("Clicker.ActionBar.Cooldown", "&aYou can click again!");
    	translations.addDefault("Clicker.Menu.Main.VIP.Name", "&bVIP");
    	translations.addDefault("Clicker.Menu.Main.VIP.Not", "&7&l» &4&lVIP NOT BOUGHT");
    	translations.addDefault("Clicker.Menu.Main.VIP.Has", "&7&l» &a&lVIP BOUGHT");
    	translations.addDefault("Clicker.Menu.Main.VIP.Bonus", "&7&l» &6&lB&f&lonus: %bonus%");
    	translations.addDefault("Clicker.Upgrades.NotEnoughPoints", "&cNot enough points!");
    	translations.addDefault("Clicker.Upgrades.BetterBlocks.Name", "&eHigher Block Chance");
    	translations.addDefault("Clicker.Upgrades.BetterBlocks.Lore", Arrays.asList("Level: %level%", "Price: %cost%"));
    	translations.save();
    	Bukkit.getPluginManager().registerEvents(this, this);
    	for (String c : config.getStringList("Command.Aliases")) {
    		TheAPI.createAndRegisterCommand(c, config.getString("Command.Permission"), new GCC());
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
		}).runRepeating(0, 300);
    }
    
}

package me.petulikan1.grassclicker.Command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

import com.google.common.collect.Maps;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.APIs.ItemCreatorAPI;
import me.DevTec.TheAPI.ConfigAPI.Config;
import me.DevTec.TheAPI.CooldownAPI.CooldownAPI;
import me.DevTec.TheAPI.GUIAPI.GUI;
import me.DevTec.TheAPI.GUIAPI.ItemGUI;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.PercentageList;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.petulikan1.grassclicker.Blocks;
import me.petulikan1.grassclicker.GrassClicker;

public class GCC implements CommandExecutor, Listener {
    private static ItemGUI greenGlass;
    private static ItemGUI greyglass;
    private static ItemGUI redGlass;
    private static ItemGUI purpleGlass, openUpgrade = new ItemGUI(ItemCreatorAPI.create(Material.ANVIL, 1, "&bUpgrades")) {
        @Override
		public void onClick(Player player, GUI gui, ClickType clickType) {
            openUpgrades(player);
        }
    },
    		stats=new ItemGUI(ItemCreatorAPI.create(Material.BOOK, 1, "&bStats")) {
                @Override
				public void onClick(Player player, GUI gui, ClickType clickType) {
                    openStats(player);
                 }
             },
    			exit=new ItemGUI(ItemCreatorAPI.create(Material.BARRIER, 1, "&4Exit")) {						
    				@Override
					public void onClick(Player player, GUI gui, ClickType clicktype) {
    					player.closeInventory();
    					player.sendMessage(TheAPI.colorize("&6&lS&f&lerver &8&l»&f&l Minihra byla zavřena."));
    				}
    			},
    					rebirth=new ItemGUI(ItemCreatorAPI.create(Material.ENCHANTED_BOOK, 1, "&bRebirths")) {		
    				
    				@Override
					public void onClick(Player player, GUI gui, ClickType clickType) {				
    					openRebirth(player);
    				}
    			},
    					credits=new ItemGUI(ItemCreatorAPI.createHead(1, "&2&lpetulikan1", "petulikan1", Arrays.asList("&7&l» &6&lT&f&lvůrce minihry", "&7&l» &a&lKontakt: &e@petulikan1 &7(&6Instagram&7)"))) {
    							@Override
								public void onClick(Player s, GUI arg1, ClickType arg2) {
    								TheAPI.msg("&7»===============«",s);
    								TheAPI.msg("&6C&freted by &6petulikan1",s);
    								TheAPI.msg(" &7Links:",s);
    								TheAPI.msg(" &7- https://www.spigotmc.org/resources/grassclicker.84667/",s); 
    								TheAPI.msg("&b&lP&fowered by &bTheAPI",s);
    								TheAPI.msg("&7»===============«",s);
    							}
    						}; 
    private static HashMap<String, GUI> guis;
    private static HashMap<String, Long> aw = new HashMap<>();
    private static HashMap<String, Integer> clickcount;
    private static ItemGUI emptyGlass;
    private static HashMap<String, Double> cost;
    private static HashMap<String, Double> cost1;
    private static HashMap<String, Integer>tasks = new HashMap<>();
    
    private static Config c = GrassClicker.config;
    public GCC() {
    }
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            TheAPI.sendMsg("&4&lTento příkaz nelze použít z konzole!", sender);
            return true;
        } else if (!sender.hasPermission("grassclicker.command")) {
            return true;
        } else {
            openMenu(((Player)sender).getPlayer());       
            if (sender.hasPermission("clicker.svip")) {
            	TheAPI.getUser(sender.getName()).setAndSave("clicker.vip", 6);
            } else if (sender.hasPermission("clicker.vip")) {
            	TheAPI.getUser(sender.getName()).setAndSave("clicker.vip", 3);
            } else {
            	TheAPI.getUser(sender.getName()).setAndSave("clicker.vip", 0);
            }
            
            return true;
        }
    }

    public static void updateGlasses(Player p) {
        GUI g = guis.getOrDefault(p.getName(),null);
        if (g == null) {
            g = new GUI("&aGrass Clicker", 54) {
            	@Override
				public void onClose(Player p) {
            		TheAPI.getUser(p).setAndSave("clicker.playtime", TheAPI.getUser(p).getLong("clicker.playtime")+(System.currentTimeMillis()/1000)-aw.getOrDefault(p.getName(),0L));
            		aw.remove(p.getName());
            	}
            };
            if (TheAPI.getUser(p).getInt("clicker.clicklimiter") <= 0) 
                TheAPI.getUser(p).setAndSave("clicker.clicklimiter", TheAPI.getUser(p).getInt("clicker.clicklimiter") + 1);
            PercentageList<Blocks> block = randomBlocks.get(p.getName());
        	Material mat = (g.getItem(30) != null) ? g.getItem(30).getType() : block.getRandom().toMaterial();
        	itemsPerPlayer.put(p.getName(), new ItemGUI(ItemCreatorAPI.create(mat, 1, "&eClick", Arrays.asList("&7Points: &e" + TheAPI.getUser(p).getFloat("clicker.points")))) {
                @Override
				public void onClick( Player player, GUI gui, ClickType clickType) {
                	PercentageList<Blocks> block = randomBlocks.get(p.getName());
                    
                	if (c.getBoolean("Options.AntiAutoClicker.Enabled")) {
                        if (clickcount.getOrDefault(player.getName(),0) >= TheAPI.getUser(player).getInt("clicker.clicklimiter") * 3500) {
                        	CooldownAPI cooldown = new CooldownAPI(p.getUniqueId());
                        	cooldown.createCooldown("clicker", 3);
                        	this.setItem(ItemCreatorAPI.create(Material.GRAY_STAINED_GLASS_PANE, 1, "&eVydrž chvíli"));                    	
                            for(int i : new int[] {30,31,32,22,21,23})
                            	gui.setItem(i, this);
                    		ItemGUI gg = this;
                    		if(!tasks.containsKey(player.getName()))
                			tasks.put(player.getName(), new Tasker() {
    							@Override
								public void run() {
    								if(cooldown.expired("clicker")) {
    									cancel();
    		                    	cooldown.removeCooldown("clicker");
    								if(player.isOnline())
    								TheAPI.sendActionBar(player, "&a&lNyní můžete opět klikat");
    								clickcount.put(player.getName(), 0);
    								gg.setItem(ItemCreatorAPI.create(mat, 1, "&eClick", Arrays.asList("&7Points: &e" + TheAPI.getUser(p).getFloat("clicker.points"))));
                                    for(int i : new int[] {21,22,23,31,32,30})
                                    gui.setItem(i, gg);
    	                			tasks.remove(player.getName());
    								}
                             }
    						}.runRepeating(0, 60L));
                			return;                         
                        }
                	}
                    if(!gui.getItem(30).getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                    Blocks clicked = Blocks.valueOf(gui.getItem(30).getType().name());
                    TheAPI.getUser(player).set("clicker.clicks", TheAPI.getUser(player).getInt("clicker.clicks") +1);
                    TheAPI.getUser(player).setAndSave("clicker.points", TheAPI.getUser(player).getFloat("clicker.points") + (clicked.getClicks()>=0?clicked.getClicks():0 + ((clicked.getClicks()>=0?clicked.getClicks():0)*TheAPI.getUser(p).getInt("clicker.rebirths")*0.5))+TheAPI.getUser(p).getInt("clicker.vip"));
                    setItem(ItemCreatorAPI.create(block.getRandom().toMaterial(), 1, "&eClick", Arrays.asList("&7Clicks: &e" + TheAPI.getUser(player).getFloat("clicker.points"))));
                    for(int i : new int[] {21,22,23,30,31,32}) {                        
                        gui.setItem(i, this);
                    }
                    }
                    this.setItem(ItemCreatorAPI.create(block.getRandom().toMaterial(), 1, "&eClick", Arrays.asList("&7Clicks: &e" + TheAPI.getUser(player).getFloat("clicker.points"))));
                    for(int i : new int[] {21,22,23,30,31,32}) {                        
                        gui.setItem(i, this);
                    }                    
                    GCC.clickcount.put(p.getName(), GCC.clickcount.getOrDefault(player.getName(),0) +100);
                }
            });
            for(int i : new int[] {21,22,23,30,31,32})
                g.setItem(i, itemsPerPlayer.get(p.getName()));
            g.setItem(18, openUpgrade);
            g.setItem(27, stats);
            g.setItem(49, exit);
            g.setItem(26, rebirth);
            g.setItem(4, credits);
            guis.put(p.getName(), g);
        }
        aw.put(p.getName(),System.currentTimeMillis()/1000);
        ItemGUI glass = TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 5?greenGlass:(TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 12
        		?greyglass:(TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 18?redGlass:purpleGlass));
        int i = 0;   
        for(i = 0; i < 9; ++i) {
            	if(i!=4)
                g.setItem(i, glass);
            }

            for(i = 45; i < 54; ++i) {
            	if(i!=49)
                g.setItem(i, glass);
            }
    }
    
    private static HashMap<String, PercentageList<Blocks>> randomBlocks = new HashMap<>();
    private static HashMap<String, ItemGUI> itemsPerPlayer = new HashMap<>();
    
    public static void openMenu(Player p) {
        PercentageList<Blocks> block = new PercentageList<>();
        for(Blocks b : Blocks.getByLevel(TheAPI.getUser(p).getInt("clicker.blocksLevel")))
            block.add(b, b.getChance());
        randomBlocks.put(p.getName(), block);
        updateGlasses(p);
        GUI g = guis.get(p.getName());
        g.open(new Player[]{p});
        g.setItem(35, new ItemGUI(ItemCreatorAPI.create(Material.EMERALD, 1, "&bVIP", Arrays.asList((TheAPI.getUser(p).getInt("clicker.vip")<=0?"&7&l» &4&lVIP NEZAKOUPENO":"&7&l» &a&lVIP ZAKOUPENO"),"&7&l» &6&lB&f&lonus: " + TheAPI.getUser(p).getInt("clicker.vip")))) {
			@Override
			public void onClick(Player player, GUI arg1, ClickType arg2) {
			}
		});
    }
    
        
    public static void openUpgrades(final Player p) {
        final GUI g = new GUI("&bUpgrades", 45, new Player[]{p});
        (new Tasker() {
        	@Override
			public void run() {          	
            		
                
                for(int i = 0; i < 9; ++i) {
                    g.setItem(i, GCC.emptyGlass);
                }

                for(int i = 36; i < 45; ++i) {
                    g.setItem(i, GCC.emptyGlass);
                }

                g.setItem(40, new ItemGUI(ItemCreatorAPI.create(Material.BARRIER, 1, "&cZpět")) {
                    @Override
					public void onClick(Player player, GUI gui, ClickType clickType) {
                        openMenu(player);
                        p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK,5 , 1);
                    }
                });
                
                if (!GCC.cost.containsKey(p.getName())) {
                    GCC.cost.put(p.getName(), (double)(2500 * (TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 0 ? 1 : TheAPI.getUser(p).getInt("clicker.blocksLevel"))));
                }
                if (TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 19) {
                    g.setItem(20, new ItemGUI(ItemCreatorAPI.create(Material.GRASS_BLOCK, 1, "&eŠance pro lepší blocky", Arrays.asList("Cena: " + GCC.cost.get(p.getName()), "Stávající úroveň: " + TheAPI.getUser(p).getInt("clicker.blocksLevel")))) {
                        @Override
						public void onClick(Player player, GUI gui, ClickType clickType) {
                            if (TheAPI.getUser(p).getFloat("clicker.points") >= GCC.cost.get(p.getName())) {
                                if (TheAPI.getUser(p).getInt("clicker.blocksLevel") == 19) {
                                    TheAPI.getUser(p).setAndSave("clicker.blocksLevel", (TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 0 ? 1 : TheAPI.getUser(p).getInt("clicker.blocksLevel")) + 1);
                                    TheAPI.getUser(p).setAndSave("clicker.points", TheAPI.getUser(p).getFloat("clicker.points") - GCC.cost.get(p.getName()));
                                    GCC.cost.put(p.getName(), (double)(2500 * (TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 0 ? 1 : TheAPI.getUser(p).getInt("clicker.blocksLevel"))));
                                    this.setItem(ItemCreatorAPI.create(Material.BARRIER, 1, "&cMaximální úroveň zakoupena.", Arrays.asList("Úroveň: " + TheAPI.getUser(p).getInt("clicker.blocksLevel"))));
                                    gui.setItem(20, this);
                                } else if (TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 18) {
                                    TheAPI.getUser(p).set("clicker.blocksLevel", TheAPI.getUser(p).getInt("clicker.blocksLevel") + 1);
                                    TheAPI.getUser(p).setAndSave("clicker.points", TheAPI.getUser(p).getFloat("clicker.points") - GCC.cost.get(p.getName()));
                                    GCC.cost.put(p.getName(), (double)(2500 * (TheAPI.getUser(p).getInt("clicker.blocksLevel") <= 0 ? 1 : TheAPI.getUser(p).getInt("clicker.blocksLevel"))));
                                    this.setItem(ItemCreatorAPI.create(Material.GRASS_BLOCK, 1, "&eŠance pro lepší blocky", Arrays.asList("Cena: " + GCC.cost.get(p.getName()), "Stávající úroveň: " + TheAPI.getUser(p).getInt("clicker.blocksLevel"))));
                                    gui.setItem(20, this);
                                }
                            } else {
                                TheAPI.msg("&cNemáte dostatek bodů.", p);
                            }

                        }
                    });
                } else {
                    g.setItem(20, new ItemGUI(ItemCreatorAPI.create(Material.BARRIER, 1, "&cMaximální úroveň zakoupena.", Arrays.asList("Úroveň: " + TheAPI.getUser(p).getInt("clicker.blocksLevel")))) {
                        @Override
						public void onClick(Player player, GUI gui, ClickType clickType) {
                        }
                    });
                }

                if (!GCC.cost1.containsKey(p.getName())) {
                    GCC.cost1.put(p.getName(), (double)(c.getInt("Options.Upgrades.AntiAutoClicker.Cost") * (TheAPI.getUser(p).getInt("clicker.clicklimiter") <= 0 ? 1 : TheAPI.getUser(p).getInt("clicker.clicklimiter"))));
                }

                g.setItem(22, new ItemGUI(ItemCreatorAPI.create(Material.PAPER, 1, "&eVyšší limit kliků.", Arrays.asList("Úroveň: " + (TheAPI.getUser(p).getInt("clicker.clicklimiter")-1), "Cena: " + GCC.cost1.get(p.getName())))) {
                    @Override
					public void onClick(Player player, GUI gui, ClickType clickType) {
                        if (TheAPI.getUser(p).getFloat("clicker.points") >= GCC.cost1.get(p.getName())) {
                            TheAPI.getUser(p).set("clicker.clicklimiter", (TheAPI.getUser(p).getInt("clicker.clicklimiter") <= 0 ? 1 : TheAPI.getUser(p).getInt("clicker.clicklimiter")) + 1);
                            TheAPI.getUser(p).setAndSave("clicker.points", TheAPI.getUser(p).getFloat("clicker.points") - GCC.cost1.get(p.getName()));
                            TheAPI.msg("&eVyšší limit kliků zakoupen.", player);
                            GCC.cost1.put(p.getName(), (double)(c.getInt("Options.Upgrades.AntiAutoClicker.Cost") * (TheAPI.getUser(p).getInt("clicker.clicklimiter") <= 0 ? 1 : TheAPI.getUser(p).getInt("clicker.clicklimiter"))));
                            this.setItem(ItemCreatorAPI.create(Material.PAPER, 1, "&eVyšší limit kliků.", Arrays.asList("Úroveň: " + TheAPI.getUser(p).getInt("clicker.clicklimiter"), "Cena: " + GCC.cost1.get(p.getName()))));
                            gui.setItem(22, this);
                        } else {
                            TheAPI.msg("&cNemáte dostatek bodů.", p);
                        }

                    }
                });
            }
        }).runTask();
    }

    public static void openStats(Player p) {
        final GUI g = new GUI("&bStats", 54, new Player[]{p});
        (new Tasker() {
            @Override
			public void run() {
                int i;
                for(i = 0; i < 9; ++i) {
                    g.setItem(i, GCC.emptyGlass);
                }

                for(i = 45; i < 54; ++i) {
                    g.setItem(i, GCC.emptyGlass);
                }
                	
                g.setItem(49, new ItemGUI(ItemCreatorAPI.create(Material.BARRIER, 1, "&cZpět")) {
                    @Override
					public void onClick(Player player, GUI gui, ClickType clickType) {
                        openMenu(player);
                        p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK,5 , 1);
                    }
                });
                switch(TheAPI.getUser(p).getInt("clicker.stats")) {
                case 0:
                	g.setItem(52, new ItemGUI(ItemCreatorAPI.create(Material.PAPER, 1, "&6&lK&f&llikněte pro změnu řazení tabulky", Arrays.asList("&c&l» &6&lK&f&lliky","&7&l» &6&lP&f&lointy","&7&l» &6&lR&f&lebirths","&7&l» &6&lP&f&llaytime"))) {
                    	@Override
						public void onClick(Player player, GUI gui, ClickType clickType) {
                    		checker(player);
                    		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN,10 , 1);
                    	}
                    });
                	int ix = 0;
                    for(Entry<String, Integer> entry : GrassClicker.clicks.entrySet()) {
                        if (++ix == 37)break;
                        
                        String other = TheAPI.colorize("&f&l"+  entry.getKey().substring(1));
                        String first = TheAPI.colorize("&6&l" +  entry.getKey().substring(0,1));
                        String playername = first+other;
                        String playtime = StringUtils.timeToString(TheAPI.getUser(entry.getKey()).getLong("clicker.playtime"));
                        
                        
                        
                        g.setItem(8 + ix, new ItemGUI(ItemCreatorAPI.createHead(1, "&e" + +ix + "&f. &fPozice", entry.getKey(), Arrays.asList("&e» &fJméno: " +playername,  " ", "&c» &fPočet kliků: &e" + entry.getValue(), "&e» &fPočet bodů: &e" + TheAPI.getUser(entry.getKey()).getFloat("clicker.points"), "&e» &fRebirthů: &e" + (TheAPI.getUser(entry.getKey()).getInt("clicker.rebirths")), "&e» &fOdehraný čas: &e" + playtime))) {
                            @Override
							public void onClick(Player player, GUI gui, ClickType clickType) {
                            }
                        });
                    }
                	break;
                case 1:
                	g.setItem(52, new ItemGUI(ItemCreatorAPI.create(Material.PAPER, 1, "&6&lK&f&llikněte pro změnu řazení tabulky", Arrays.asList("&7&l» &6&lK&f&lliky","&c&l» &6&lP&f&lointy","&7&l» &6&lR&f&lebirths","&7&l» &6&lP&f&llaytime"))) {
                    	@Override
						public void onClick(Player player, GUI gui, ClickType clickType) {
                    		checker(player);
                    		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN,10 , 1);
                    	}
                    });
                	ix = 0;
                    for(Entry<String, Integer> entry : GrassClicker.points.entrySet()) {
                        if (++ix == 37)break;
                        
                        String other = TheAPI.colorize("&f&l"+  entry.getKey().substring(1));
                        String first = TheAPI.colorize("&6&l" +  entry.getKey().substring(0,1));
                        String playername = first+other;
                        String playtime = StringUtils.timeToString(TheAPI.getUser(entry.getKey()).getLong("clicker.playtime"));
                        
                        
                        
                        g.setItem(8 + ix, new ItemGUI(ItemCreatorAPI.createHead(1, "&e" + +ix + "&f. &fPozice", entry.getKey(), Arrays.asList("&e» &fJméno: " +playername, " ", "&e» &fPočet kliků: &e" + TheAPI.getUser(entry.getKey()).getInt("clicker.clicks"), "&c» &fPočet bodů: &e" + TheAPI.getUser(entry.getKey()).getFloat("clicker.points"), "&e» &fRebirthů: &e" + (TheAPI.getUser(entry.getKey()).getInt("clicker.rebirths")), "&e» &fOdehraný čas: &e" + playtime))) {
                            @Override
							public void onClick(Player player, GUI gui, ClickType clickType) {
                            }
                        });
                    }
                	break;
                case 2:
                	g.setItem(52, new ItemGUI(ItemCreatorAPI.create(Material.PAPER, 1, "&6&lK&f&llikněte pro změnu řazení tabulky", Arrays.asList("&7&l» &6&lK&f&lliky","&7&l» &6&lP&f&lointy","&c&l» &6&lR&f&lebirths","&7&l» &6&lP&f&llaytime"))) {
                    	@Override
						public void onClick(Player player, GUI gui, ClickType clickType) {
                    		checker(player);
                    		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN,10 , 1);
                    	}
                    });  
                	ix = 0;
                    for(Entry<String, Integer> entry : GrassClicker.rebirths.entrySet()) {
                        if (++ix == 37)break;
                        
                        String other = TheAPI.colorize("&f&l"+  entry.getKey().substring(1));
                        String first = TheAPI.colorize("&6&l" +  entry.getKey().substring(0,1));
                        String playername = first+other;
                        String playtime = StringUtils.timeToString(TheAPI.getUser(entry.getKey()).getLong("clicker.playtime"));
                        
                        
                        
                        g.setItem(8 + ix, new ItemGUI(ItemCreatorAPI.createHead(1, "&e" + +ix + "&f. &fPozice", entry.getKey(), Arrays.asList("&e» &fJméno: " +playername,  " ", "&e» &fPočet kliků: &e" + TheAPI.getUser(entry.getKey()).getInt("clicker.clicks"), "&e» &fPočet bodů: &e" + TheAPI.getUser(entry.getKey()).getFloat("clicker.points"), "&c» &fRebirthů: &e" + (TheAPI.getUser(entry.getKey()).getInt("clicker.rebirths")), "&e» &fOdehraný čas: &e" + playtime))) {
                            @Override
							public void onClick(Player player, GUI gui, ClickType clickType) {
                            }
                        });
                    }
                break;
                case 3:
                	g.setItem(52, new ItemGUI(ItemCreatorAPI.create(Material.PAPER, 1, "&6&lK&f&llikněte pro změnu řazení tabulky", Arrays.asList("&7&l» &6&lK&f&lliky","&7&l» &6&lP&f&lointy","&7&l» &6&lR&f&lebirths","&c&l» &6&lP&f&llaytime"))) {
                    	@Override
						public void onClick(Player player, GUI gui, ClickType clickType) {
                    		checker(player);
                    		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN,10 , 1);
                    	}
                    });
                	ix = 0;
                    for(Entry<String, Integer> entry : GrassClicker.playtime.entrySet()) {
                        if (++ix == 37)break;
                        
                        String other = TheAPI.colorize("&f&l"+  entry.getKey().substring(1));
                        String first = TheAPI.colorize("&6&l" +  entry.getKey().substring(0,1));
                        String playername = first+other;
                        String playtime = StringUtils.timeToString(TheAPI.getUser(entry.getKey()).getLong("clicker.playtime"));
                        
                        //toto celé gui může být static a při updatu v ném updatovat items -> každý hráč bude mít stejné gui -> nebudeš pro každého hráče vytvářet nové uh to je mi jedno
                        //takto to je
                        
                        g.setItem(8 + ix, new ItemGUI(ItemCreatorAPI.createHead(1, "&e" + +ix + "&f. &fPozice", entry.getKey(), Arrays.asList("&e» &fJméno: " +playername,  " ", "&e» &fPočet kliků: &e" + TheAPI.getUser(entry.getKey()).getInt("clicker.clicks"), "&e» &fPočet bodů: &e" + TheAPI.getUser(entry.getKey()).getFloat("clicker.points"), "&e» &fRebirthů: &e" + (TheAPI.getUser(entry.getKey()).getInt("clicker.rebirths")), "&c» &fOdehraný čas: &e" + playtime))) {
                            @Override
							public void onClick(Player player, GUI gui, ClickType clickType) {
                            } ///cool
                        });
                    }
                break; //that's all
                }
            }
        }).runTask();
    }    
    public static void checker(Player player ) {
    	if (TheAPI.getUser(player).getInt("clicker.stats")==3) { //5 -> 0
			TheAPI.getUser(player).set("clicker.stats", 0);
			openStats(player);
			return;
		}
		TheAPI.getUser(player).set("clicker.stats", TheAPI.getUser(player).getInt("clicker.stats")+1); //0 - 5 //tam jsou 4...
		openStats(player);
    }
    //na princip
    public static void openRebirth(Player p) {
    	final GUI g = new GUI("&bRebirths", 45, new Player[] {p});
    	(new Tasker() {	
			@Override
			public void run() {
				int a;
				for (a=0;a<9;++a) {
					g.setItem(a, GCC.emptyGlass);				
				}
				for (a=36;a<45;++a) {
					g.setItem(a, GCC.emptyGlass);	
				}
				g.setItem(40, new ItemGUI(ItemCreatorAPI.create(Material.BARRIER, 1, "&cZpět")) {
                    @Override
					public void onClick(Player player, GUI gui, ClickType clickType) {
                        openMenu(player);
                        p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK,5 , 1);
                    }
                });
				if (TheAPI.getUser(p).getFloat("clicker.points")>=1000000) {
					g.setItem(22, new ItemGUI(ItemCreatorAPI.create(Material.ENCHANTED_BOOK, 1, "&6&lC&b&llick for &6&lR&b&lebirth")) {
						@Override
						public void onClick(Player p, GUI arg1, ClickType arg2) {
							openConfirm(p);
						}
					});
				} else {
					g.setItem(22, new ItemGUI(ItemCreatorAPI.create(Material.BARRIER, 1, "&cNemáte dostatek pointů!", Arrays.asList("&7&l» &f&lChybějící počet: " + (1000000.0-TheAPI.getUser(p).getFloat("clicker.points"))))) {
						@Override
						public void onClick(Player arg0, GUI arg1, ClickType arg2) {
						}
					});
				}
			}
		}).runTask();
    	
    }

    
    public static void openConfirm(Player p) {
    	GUI g = new GUI("&a&lConfirm Menu", 27, new Player[] {p});
    	(new Tasker() {
			@Override
			public void run() {
				int a;
				for (a=0;a<9;++a) {
					g.setItem(a, GCC.emptyGlass);
				}
				for(a=18;a<27;++a) {
					g.setItem(a, GCC.emptyGlass);
				}
				g.setItem(9, GCC.emptyGlass);
				g.setItem(17, GCC.emptyGlass);
				g.setItem(13, GCC.emptyGlass);
				ItemGUI it = new ItemGUI(ItemCreatorAPI.create(Material.LIME_STAINED_GLASS_PANE, 1, "&a&lConfirm Rebirth", Arrays.asList("&7&l» &6&lT&f&lhis action cannot be undone &7&l«", "&7&l» &6&lY&f&lou will loose everything, but will gain bonus of: &6&l" + ((TheAPI.getUser(p).getInt("clicker.rm")+1)*0.5), "&7&l» &6&lC&f&llick to continue &7&l«"))) {
					@Override
					public void onClick(Player player, GUI gui, ClickType clickType) {
						TheAPI.getUser(player).set("clicker.points",0);
						TheAPI.getUser(player).set("clicker.clicklimiter", 0);
						TheAPI.getUser(player).setAndSave("clicker.rebirths", TheAPI.getUser(player).getInt("clicker.rebirths")+1);
						TheAPI.msg("&6&lS&f&lerver &7&l» &f&lRebirth proběhl úspěšně", player);
						player.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
						openMenu(player);
					}
				};
				ItemGUI ia = new ItemGUI(ItemCreatorAPI.create(Material.RED_STAINED_GLASS_PANE, 1, "&c&lCancel Action")) {
					@Override
					public void onClick(Player player, GUI g, ClickType clickType) {
						openRebirth(player);
					}
				};
				for (a=10;a<13;++a) {
				g.setItem(a, it);
				}
				for (a=14;a<17;++a) {
				g.setItem(a, ia);
				}
			}
		}).runTask();
    }    
    static {
    	String message = "&b&lProgrammed by: petulikan1, helped: StraikerinaCZ";
        greenGlass = new ItemGUI(ItemCreatorAPI.create(Material.LIME_STAINED_GLASS_PANE, 1, "&7")) {
            @Override
			public void onClick(Player player, GUI gui, ClickType clickType) {
                TheAPI.msg(message, player);
            }
        };
        greyglass = new ItemGUI(ItemCreatorAPI.create(Material.GRAY_STAINED_GLASS_PANE, 1, "&7")) {
            @Override
			public void onClick(Player player, GUI gui, ClickType clickType) {
                TheAPI.msg(message, player);
            }
        };
        redGlass = new ItemGUI(ItemCreatorAPI.create(Material.RED_STAINED_GLASS_PANE, 1, "&7")) {
            @Override
			public void onClick(Player player, GUI gui, ClickType clickType) {
                TheAPI.msg(message, player);
            }
        };
        purpleGlass = new ItemGUI(ItemCreatorAPI.create(Material.PURPLE_STAINED_GLASS_PANE, 1, "&7")) {
            @Override
			public void onClick(Player player, GUI gui, ClickType clickType) {
                TheAPI.msg(message, player);
            }
        };
        guis = Maps.newHashMap();
        clickcount = Maps.newHashMap();    
        emptyGlass = new ItemGUI(ItemCreatorAPI.create(Material.BLACK_STAINED_GLASS_PANE, 1, "&7")) {
            @Override
			public void onClick(Player player, GUI gui, ClickType clickType) {
                TheAPI.msg(message, player);
            }
        };
        cost = Maps.newHashMap();
        cost1 = Maps.newHashMap();
    }   
    
	
}

package org.herreurobeat.oldGamemode;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Main extends JavaPlugin {
	
	String supportedversion = "1.16";
	String pluginversion = "1.2";
	List<String> targetselectors = Arrays.asList("@a", "@e", "@p", "@r", "@s");
	
	@Override
	public void onEnable() {
		System.out.println("oldGamemode for version " + supportedversion + " enabled!");
	}
	
	//Handle command execution
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { //handle calling command methods

		//Since this is a small plugin I will handle every command in this file
		switch (cmd.getName()) { //handle execution of each command
			case "oginfo":
				oginfoCommand(sender, args);
				break;
			case "gm":
			case "gamemode":
				gamemodeCommand(sender, cmd, args);
				break;	
			default:
				break;
		}
		
		return false;
	}
	
	//Handle oginfo command code
	public void oginfoCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) { //check if sender is a player
			Player p = (Player) sender; //convert to player
			if(!p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) return; //check gamerule and stop if send command feedback is false
		}
		sender.sendMessage("oldGamemodePlugin by 3urobeat v" + pluginversion + " \n----\nType /gm or /gamemode and use the argument 0, 1, 2 or 3 to change your gamemode the old way.\n\nUse survival, creative, adventure or spectator as an argument to change your gamemode the new and default way.\n----\nhttps://github.com/HerrEurobeat/oldGamemodePlugin");
	}
	
	//Handle gamemode command code
	public void gamemodeCommand(CommandSender sender, Command cmd, String[] args) {
		
		if (args.length < 1) { //no argument provided? Catch! 
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if(!p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) return;
			}
			sender.sendMessage("You are missing an argument. (0: survival, 1: creative, 2: adventure, 3: spectator)");
			return;
		}
		
		List<Player> targetplayer = new ArrayList<Player>(); //define our List
		
		if (args.length == 2) { //a player name was provided
			
			if (targetselectors.contains(args[1])) { //user seems to have provided a target selector
				if (!(sender instanceof Player) && !sender.getName().equals("@")) { sender.sendMessage("Only a player is allowed to use a target selector!"); return; }
				
				for (Entity thisentity : Bukkit.selectEntities(sender, args[1])) { //get all entities from target selector
					if (thisentity instanceof Player) { //filter Players from the list because how should be change the game mode of a Sheep?
						Player thisplayer = (Player) thisentity; //convert Entity type to Player
						targetplayer.add(thisplayer); //add the player to our List
					}
				}
				
			} else { //user seems to have provided a player name		
				if (Bukkit.getPlayer(args[1]) != null) { //check if provided player name is online, otherwise it will throw an error
					targetplayer.add(Bukkit.getPlayer(args[1]));
				} else {
					sender.sendMessage("That player doesn't seem to be online!");
					return;
				}
			}
			
			
		} else if (sender instanceof Player) { //no player name provided but the sender is a player
			targetplayer.add((Player) sender);
			
		} else { //no player name and sender isn't a player
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if(!p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) return;
			}
			sender.sendMessage("You need to provide a player name!");
			return;
		}
			
		if (!(sender instanceof Player) || sender.hasPermission("minecraft.command.gamemode")) { //check if he/she/it even has the permissions or he/she/it is the console
			
			switch (args[0].toLowerCase()) { //get message and work with it
				case "0":
				case "survival":
					handleGameModeChange(targetplayer, sender, "Survival", GameMode.SURVIVAL);
					break;
				case "1":
				case "creative":
					handleGameModeChange(targetplayer, sender, "Creative", GameMode.CREATIVE);
					break;
				case "2":
				case "adventure":
					handleGameModeChange(targetplayer, sender, "Adventure", GameMode.ADVENTURE);
					break;
				case "3":
				case "spectator":
					handleGameModeChange(targetplayer, sender, "Spectator", GameMode.SPECTATOR);
					break;
				default: //wrong argument
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if(!p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) return;
					}
					sender.sendMessage("Your argument seems to be wrong. (0: survival, 1: creative, 2: adventure, 3: spectator)");
					break;
			}
			

		} else {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if(!p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) return;
			}
			sender.sendMessage("You don't have permission to execute this command.");
		}
	}
	
	//Handle game mode change once in order to reduce duplicate code
	public void handleGameModeChange(List<Player> targetplayer, CommandSender sender, String gamemodestr, GameMode gamemode) {
		for (Player thisplayer : targetplayer) { //take action for each player in targetplayer list
			if (thisplayer.getGameMode().toString().equalsIgnoreCase(gamemodestr)) return; //if the game mode is already the requested one then do nothing (not that cool but vanilla mc does that like that)
			
			thisplayer.setGameMode(gamemode);
			
			//Handle all the different messages
			if (sender != thisplayer && thisplayer.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) thisplayer.sendMessage("Your gamemode has been updated to " + gamemodestr + " Mode"); //if sender is not targetplayer then send update notification
			
			if (sender instanceof Player && thisplayer.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) { //send message to sender & targetplayer
				if (sender != thisplayer) sender.sendMessage("Set " + thisplayer.getName() + "'s game mode to " + gamemodestr + " Mode"); //sender
					else sender.sendMessage("Set own game mode to " + gamemodestr + " Mode"); //sender is targetplayer?
			}
			
			//Console logging
			if (sender.getName() == "CONSOLE") { //log action to console
				Bukkit.getLogger().info("Set " + thisplayer.getName() + "'s game mode to " + gamemodestr + " Mode"); //cmd got executed by console
				
			} else { //cmd got executed by Player or Command Block
				if (thisplayer.getWorld().getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS)) { //log admin commands gamerule blocks output in console
					
					if (sender != thisplayer) { 
						if (!sender.getName().equals("@")) Bukkit.getLogger().info("[" + sender.getName() + ": Set " + thisplayer.getName() + "'s game mode to " + gamemodestr + " Mode]"); //user changed gamemode of someone else but is not a command block (that gets checked later)
						
					} else Bukkit.getLogger().info("[" + sender.getName() + ": Set own game mode to " + gamemodestr + " Mode]"); //user changed own game mode
					
					//command block output gamerule blocks output for player & console
					if (sender.getName().equals("@") && thisplayer.getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) Bukkit.getLogger().info("[@: Set " + thisplayer.getName() + "'s game mode to " + gamemodestr + " Mode]"); //command block changed gamemode
				}
			}
			
			
			//Player messages
			if (thisplayer.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.isOp() && sender != player) {
						String sendername = sender.getName();
						if (sender.getName() == "CONSOLE") sendername = "Server"; //vanilla also says Server instead of CONSOLE so here we go
	
						if (sendername.equals("@") && !player.getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) return;
							
						player.sendMessage("§7§o[" + sendername + ": Set " + thisplayer.getName() + "'s game mode to " + gamemodestr + " Mode]"); //send the grey and italic message
					}
				}
			}
		}
	}
	
	
	//Handle tab completion
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) { //handle auto completion of my commands by returning a list (Thanks: https://youtu.be/839z-w7RFSI)
		
		if (sender.isOp()) { //only bother if the user is op			
			if (cmd.getName().equalsIgnoreCase("gamemode") || cmd.getName().equalsIgnoreCase("gm")) { //user seems to be meaning the gamemode command
				
				if (args.length == 1) { //this is the gamemode argument
					ArrayList<String> gamemodes = new ArrayList<String>();
					
					if (!args[0].equals("")) { //user started typing
						for (GameMode gamemode : GameMode.values()) {
							if (gamemode.name().toLowerCase().startsWith(args[0].toLowerCase())) { //filter game modes which don't start with what the user typed (we can ignore the number args because they are only 1 char long)
								gamemodes.add(gamemode.name().toLowerCase()); //add all gamemodes the user could still mean
							}
						}
					} else { //user hasn't started typing
						gamemodes.add("0"); //add my custom arguments 
						gamemodes.add("1");
						gamemodes.add("2");
						gamemodes.add("3");
						
						for (GameMode type : GameMode.values()) {
							gamemodes.add(type.name().toLowerCase()); //add all default gamemode types
						}
					}
					
					Collections.sort(gamemodes); //sort that thing
					
					return gamemodes; //return our list
					
				} else if (args.length == 2) { //this is the player name argument
					ArrayList<String> players = new ArrayList<String>();
					
					if (!args[1].equals("")) { //user started typing
						for (Player thisplayer : Bukkit.getOnlinePlayers()) {
							if (thisplayer.getName().toLowerCase().startsWith(args[1].toLowerCase())) { //filter player names which don't start with what the user typed
								players.add(thisplayer.getName()); //add all names the user could still mean
							}
						}
						
						for (String targetselector : targetselectors) {
							if (targetselector.toLowerCase().startsWith(args[1].toLowerCase())) {
								players.add(targetselector);
							}
						}
					} else { //user hasn't started typing
						players.addAll(targetselectors);
						
						for (Player thisplayer : Bukkit.getOnlinePlayers()) {
							players.add(thisplayer.getName()); //add all available user names
						}
					}
					
					Collections.sort(players); //sort the list to make it *f a n c y*
					
					return players; //return our list
				} else {
					return new ArrayList<>(); //disable auto completion for all other arguments because this plugin doesn't need more
				}
			}
		}
		return new ArrayList<>(); //just return something to make the compiler happy :)
	}
}
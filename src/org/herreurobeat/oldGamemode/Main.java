package org.herreurobeat.oldGamemode;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Main extends JavaPlugin {
	
	String supportedversion = "1.16";
	String pluginversion = "1.1";
	
	@Override
	public void onEnable() {
		System.out.println("oldGamemode for version " + supportedversion + " enabled!");
	}
	
	public void sysout(String str) { //make that long system log thing nice and short
		System.out.println(str);
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
		sender.sendMessage("oldGamemodePlugin by 3urobeat v" + pluginversion + " \n----\nType /gm or /gamemode and use the argument 0, 1, 2 or 3 to change your gamemode the old way.\n\nUse survival, creative, adventure or spectator as an argument to change your gamemode the new and default way.\n----\nhttps://github.com/HerrEurobeat/oldGamemodePlugin");
	}
	
	//Handle gamemode command code
	public void gamemodeCommand(CommandSender sender, Command cmd, String[] args) {
		
		if (args.length < 1) { //no argument provided? Catch! 
			sender.sendMessage("You are missing an argument. (0: survival, 1: creative, 2: adventure, 3: spectator)");
			return;
		}
		
		Player targetplayer = null;
		
		if (args.length == 2) { //a player name was provided
			if (Bukkit.getPlayer(args[1]) != null) { //check if provided player name is online, otherwise it will throw an error
				targetplayer = (Player) Bukkit.getPlayer(args[1]);
			} else {
				sender.sendMessage("That player doesn't seem to be online!");
				return;
			}
		} else if (sender instanceof Player) { //no player name provided but the sender is a player
			targetplayer = (Player) sender;
			
		} else { //no player name and sender isn't a player
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
					sender.sendMessage("Your argument seems to be wrong. (0: survival, 1: creative, 2: adventure, 3: spectator)");
					break;
			}
			

		} else {
			sender.sendMessage("You don't have permission to execute this command.");
		}
	}
	
	//Handle game mode change once in order to reduce duplicate code
	public void handleGameModeChange(Player targetplayer, CommandSender sender, String gamemodestr, GameMode gamemode) {
		if (targetplayer.getGameMode().toString() == gamemodestr.toUpperCase()) return; //if the game mode is already the requested one then do nothing (not that cool but vanilla mc does that like that)
			
		targetplayer.setGameMode(gamemode); //set gamemode
		
		//Handle all the different messages
		if (sender != targetplayer) targetplayer.sendMessage("Your gamemode has been updated to " + gamemodestr + " Mode"); //if sender is not targetplayer then send update notification
		
		if (sender instanceof Player) {
			if (sender != targetplayer) sender.sendMessage("Set " + targetplayer.getName() + "'s game mode to " + gamemodestr + " Mode");
				else sender.sendMessage("Set own game mode to " + gamemodestr + " Mode");
		}
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.isOp() && sender != player) {
				String sendername = sender.getName();
				if (sender.getName() == "CONSOLE") sendername = "Server"; //vanilla also says Server instead of CONSOLE so here we go
				
				player.sendMessage("§7§o[" + sendername + ": Set " + targetplayer.getName() + "'s game mode to " + gamemodestr + " Mode]");
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
						for (GameMode type : GameMode.values()) {
							if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) { //filter game modes which don't start with what the user typed (we can ignore the number args because they are only 1 char long)
								gamemodes.add(type.name().toLowerCase()); //add all gamemodes the user could still mean
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
					} else { //user hasn't started typing
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
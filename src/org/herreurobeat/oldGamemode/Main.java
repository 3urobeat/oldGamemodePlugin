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
	
	@Override
	public void onEnable() {
		System.out.println("oldGamemode for version " + supportedversion + " enabled!");
	}
	
	public void sysout(String str) { //make that long system log thing nice and short
		System.out.println(str);
	}
	
	//Handle command execution
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { //user used my command!
		if (!(sender instanceof Player)) return true; //disallow the console to execute the command
		
		Player p = (Player) sender;
		
		if (args.length < 1) { //no argument provided? Catch! 
			p.sendMessage("You are missing an argument. (0: survival, 1: creative, 2: adventure, 3: spectator)"); 
			return true;
		}
		
		if (p.isOp()) { //check if he/she/it even has the permissions
			Player targetplayer = p;
			
			if (args.length == 2) { //a player name was provided
				targetplayer = (Player) Bukkit.getPlayer(args[1]);
			}
			
			switch (args[0]) { //get message and work with it
				case "0":
				case "survival":
					targetplayer.setGameMode(GameMode.SURVIVAL);
					p.sendMessage("Set " + targetplayer.getName() + "'s game mode to Survival Mode");
					sysout("[oldGamemode] Set " + targetplayer.getName() + "'s game mode to Survival Mode");
					break;
				case "1":
				case "creative":
					targetplayer.setGameMode(GameMode.CREATIVE);
					p.sendMessage("Set " + targetplayer.getName() + "'s game mode to Creative Mode");
					sysout("[oldGamemode] Set " + targetplayer.getName() + "'s game mode to Creative Mode");
					break;
				case "2":
				case "adventure":
					targetplayer.setGameMode(GameMode.ADVENTURE);
					p.sendMessage("Set " + targetplayer.getName() + "'s game mode to Adventure Mode");
					sysout("[oldGamemode] Set " + targetplayer.getName() + "'s game mode to Adventure Mode");
					break;
				case "3":
				case "spectator":
					targetplayer.setGameMode(GameMode.SPECTATOR);
					p.sendMessage("Set " + targetplayer.getName() + "'s game mode to Spectator Mode");
					sysout("[oldGamemode] Set " + targetplayer.getName() + "'s game mode to Spectator Mode");
					break;
				default: //wrong argument
					p.sendMessage("Your argument seems to be wrong. (0: survival, 1: creative, 2: adventure, 3: spectator)");
					break;
			}
		} else {
			p.sendMessage("You don't have permission to execute this command.");
		}
		
		return false;
	}
	
	//Handle tab completion
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) { //handle auto completion of my commands by returning a list (Thanks: https://youtu.be/839z-w7RFSI)
		
		if (sender.isOp()) { //only bother if the user is op
			if (cmd.getName().equalsIgnoreCase("gamemode") || cmd.getName().equalsIgnoreCase("gm")) {
				
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
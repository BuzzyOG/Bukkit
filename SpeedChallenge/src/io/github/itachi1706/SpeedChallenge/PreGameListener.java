package io.github.itachi1706.SpeedChallenge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PreGameListener implements Listener{
	
	@EventHandler
	public void freezePlayer(PlayerMoveEvent e){
		if (!Main.gameStart){
			if (Main.initGame){
				Player p = e.getPlayer();
				for (int i = 0; i < Main.playerList.size(); i++){
					Player pla = Main.playerList.get(i);
					if (p.getName().equals(pla.getName())){
						//In game, freeze player
						Location lfrom = e.getFrom(), lTo = e.getTo();
						double fx = lfrom.getX(), fz = lfrom.getZ(), tx = lTo.getX(), tz = lTo.getZ();
						if (fx != tx && fz != tz){
							p.teleport(lfrom);
						}
						
					}
				}
			}	
		}
	}
	
	@EventHandler
	public void preventBlockBreak(BlockBreakEvent e){
		if (e.getPlayer().hasPermission("sc.override")){
			e.setCancelled(false);
		} else
		if (!Main.gameStart){
			e.setCancelled(true);
		}
		if (checkSpectator(e.getPlayer())){
			//Spectator
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "You are not allowed to break blocks!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void placeBlocks(BlockPlaceEvent e){
		if (e.getPlayer().hasPermission("sc.bypass")){
			e.setCancelled(false);
		} else
		if (!Main.gameStart){
			e.setCancelled(true);
		}
		if (checkSpectator(e.getPlayer())){
			//Spectator
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "You are not allowed to place blocks!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void damagePlayer(EntityDamageEvent e){
		if (!Main.gameStart){
			e.setCancelled(true);
		} else 
		if (e.getEntity() instanceof Player){
			if (checkSpectator((Player) e.getEntity())){
					//Spectator
					e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void playerHitPlayer(EntityDamageByEntityEvent e){
		if (!Main.gameStart){
			e.setCancelled(true);
		} else if (e.getEntity() instanceof Player){
			if (Main.pvp == 2 && e.getDamager() instanceof Player){
				//PVP Off
				((Player) e.getDamager()).sendMessage(ChatColor.DARK_RED + "PVP is disabled!");
				e.setCancelled(true);
				return;
			}
			if (e.getDamager() instanceof Player && Main.invulnerable){
				((Player) e.getDamager()).sendMessage(ChatColor.DARK_RED + "Player is invulnerable!");
				e.setCancelled(true);
			}
		} if (e.getDamager() instanceof Player){
			if (checkSpectator((Player) e.getDamager())){
				((Player) e.getDamager()).sendMessage(ChatColor.DARK_RED + "You are not allowed to hit entities!");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void respawnOnDeath(PlayerRespawnEvent e){
		
		if (Main.respawn == 2){
			//Denies Respawn On Death
			e.getPlayer().sendMessage(ChatColor.RED + "You are now a spectator as respawn on death is disabled!");
			Main.spectators.add(e.getPlayer());
			for (int i = 0; i < Main.playerList.size(); i++){
				Player p = Main.playerList.get(i);
				if (p.equals(e.getPlayer())){
					Main.playerList.remove(i);
					break;
				}
			}
			Spec.addSpectator(e.getPlayer());
			Bukkit.getLogger().info("Spectators: " + Main.spectators.size());
			Bukkit.getLogger().info("Player Remaining:" + Main.playerList.size());
			if (Main.playerList.size() == 0){
				Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "As all players have died, Game will end in 30 seconds!");
				Main.countdown = 31;
			}
			
			
		}
	}
	
	@EventHandler
	public void spectatorTargeted(EntityTargetEvent e){
		if (e.getTarget() instanceof Player){
			Player p = (Player) e.getTarget();
			if (checkSpectator(p)){
				//Player is spectator
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void specInteract(PlayerInteractEvent e){
		for (int i = 0; i < Main.spectators.size(); i++){
			Player p = Main.spectators.get(i);
			if (p.equals(e.getPlayer())){
				try {
					if (e.getItem().equals(new ItemStack(Material.COMPASS))){
						//Check Nearest Spectator and distance away from the player
						Spec.spectatorMsg(p);
					} else if (e.getClickedBlock().isEmpty()){
					} else {
						e.getPlayer().sendMessage(ChatColor.DARK_RED + "As a spectator, you are not allowed to interact with blocks/players!");
					}
					e.setCancelled(true);
					break;
				} catch (Exception ex){
					Bukkit.getLogger().info("OP Erroring out Interact Event!");
				}
			}
		}
		
	}
	
	@EventHandler
	public void specCannotDrop(PlayerDropItemEvent e){
		if (checkSpectator(e.getPlayer()) && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "As a spectator, you are not allowed to drop items!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void specCannotPickup(PlayerPickupItemEvent e){
		if (checkSpectator(e.getPlayer()) && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
			e.setCancelled(true);
		}
	}
	
	public static boolean checkSpectator(Player p){
		for (int i = 0; i < Main.spectators.size(); i++){
			Player spectator = Main.spectators.get(i);
			if (spectator.equals(p)){
				//Is a spectator
				return true;
			}
		}
		return false;
	}

}
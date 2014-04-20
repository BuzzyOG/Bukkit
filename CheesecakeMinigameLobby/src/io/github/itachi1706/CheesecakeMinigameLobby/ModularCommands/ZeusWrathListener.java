package io.github.itachi1706.CheesecakeMinigameLobby.ModularCommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ZeusWrathListener implements Listener{

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		boolean check = false;
		for (int i = 0; i < ZeusWrathCmd.playerListZeused.size(); i++){
			Player pla = ZeusWrathCmd.playerListZeused.get(i);
			if (pla == p){
				check = true;
				ZeusWrathCmd.playerListZeused.remove(i);
				break;
			}
		}
		if (check){
			e.setDeathMessage(p.getDisplayName() + ChatColor.WHITE + " suffered the Wrath of Zeus!");
		}
	}

}

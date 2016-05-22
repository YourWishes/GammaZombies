/*
 * Copyright 2013 Dominic Masters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.GammaZombies;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author Dominic Masters
 */
public final class GammaPlayer {
    private static final List<GammaPlayer> PLAYERS = new ArrayList<GammaPlayer>();
    
    public static final GammaPlayer getPlayer(String name) {
        for(GammaPlayer player : PLAYERS) {
            if(player.player.equals(name.toLowerCase())) return player;
        }
        GammaPlayer player = new GammaPlayer(name);
        PLAYERS.add(player);
        return player;
    }
    
    public static final GammaPlayer getPlayer(Player player) {return getPlayer(player.getName());}
    public static final GammaPlayer getPlayer(OfflinePlayer player) {return getPlayer(player.getName());}
    public static final GammaPlayer getPlayer(CommandSender sender) {return getPlayer(sender.getName());}
    
    public static List<GammaPlayer> filterOnline(List<GammaPlayer> players) {
        List<GammaPlayer> playerz = new ArrayList<GammaPlayer>();
        if(players == null) return playerz;
        for(GammaPlayer p : players) {
            if(!p.isOnline()) continue;
            playerz.add(p);
        }
        return playerz;
    }
    
    //Instance
    private final String player;
    
    private GammaPlayer(String player) {
        this.player = player.toLowerCase();
    }
    
    public String getName() {return this.player;}
    
    public boolean compare(GammaPlayer player) {return player.getName().equalsIgnoreCase(this.player);}
    public boolean compare(OfflinePlayer player) {return player.getName().toLowerCase().equals(this.player);}
    public boolean compare(Player player) {return player.getName().toLowerCase().equals(this.player);}
    public boolean compare(CommandSender sender) {return sender.getName().toLowerCase().equals(this.player);}
    public boolean compare(Entity e) {if(e instanceof Player) return this.compare((Player) e); return false;}
    
    public Player getOnlinePlayer() {return Bukkit.getPlayerExact(this.player);}
    
    public boolean isOnline() {
        Player p = getOnlinePlayer();
        return p != null && p.isOnline();
    }
    
    //TODO: Finish Logic
    public int getLevel() {
        return 1;
    }
}

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

package com.domsplace.GammaZombies.Listener.Map;

import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Dominic Masters
 */
public class MapPlayerOutOfBoundsListener extends MapListener {
    public MapPlayerOutOfBoundsListener(Map map) {
        super(map);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void blockPlayersEnteringMap(PlayerMoveEvent e) {
        if(!this.getMap().isInMap(e.getTo())) return;
        if(this.getMap().isPlayerInMap(e.getPlayer())) return;
        //Block
        Location setBack = e.getFrom();
        if(this.getMap().isInMap(setBack)) setBack = e.getFrom().getWorld().getSpawnLocation();
        e.setTo(setBack);
        LocationUtilities.playSwoosh(e.getFrom());
        LocationUtilities.playSwoosh(setBack);
        //TODO: Maybe add a message here
    }
    
    @EventHandler(ignoreCancelled=true)
    public void blockPlayersLeavingMap(PlayerMoveEvent e) {
        if(this.getMap().isInMap(e.getTo())) return;
        if(!this.getMap().isPlayerInMap(e.getPlayer())) return;
        //Block
        Location setBack = e.getFrom();
        if(!this.getMap().isInMap(setBack)) setBack = this.getMap().getRandomLocation();
        e.setTo(setBack);
        LocationUtilities.playSwoosh(e.getFrom());
        LocationUtilities.playSwoosh(setBack);
        //TODO: Maybe add a message here
    }
    
    @EventHandler(ignoreCancelled=true)
    public void blockEntitiesTargetingOutOfBounds(EntityTargetLivingEntityEvent e) {
        if(!this.getMap().isEntityInMap(e.getEntity())) return;
        if(e.getTarget() == null) return;
        if(this.getMap().isInMap(e.getTarget().getLocation())) return;
        e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void blockOutOfBoundsEntitiesTargetingInBounds(EntityTargetLivingEntityEvent e) {
        if(this.getMap().isEntityInMap(e.getEntity())) return;
        if(e.getTarget() == null) return;
        if(!this.getMap().isInMap(e.getTarget().getLocation())) return;
        e.setCancelled(true);
    }
}

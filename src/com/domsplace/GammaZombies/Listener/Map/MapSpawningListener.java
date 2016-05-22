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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 *
 * @author Dominic Masters
 */
public class MapSpawningListener extends MapListener {    
    public MapSpawningListener(Map map) {super(map);}
    
    @EventHandler(ignoreCancelled=true)
    public void stopNaturallySpawningMobs(CreatureSpawnEvent e) {
        if(e.getSpawnReason().equals(SpawnReason.CUSTOM)) return;
        if(!this.getMap().isInMap(e.getLocation())) return;
        e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void removeEntitiesOnChunkUnload(ChunkUnloadEvent e) {
        if(e.getChunk() != null) return;
        if(e.getChunk() == null) return;
        if(e.getChunk().getEntities() == null || e.getChunk().getEntities().length < 1) return;
        //Not sure if necessary.. could be good for chunks with lots of entities
        //if(!this.getMap().isInMap(e.getChunk())) return;
        
        for(Entity en : e.getChunk().getEntities()) {
            if(en == null) continue;
            if(en.getType() == null || !en.getType().isAlive()) continue;
            if(EntityType.PLAYER.equals(en.getType())) continue;
            if(en.getLocation() == null) continue;
            //Could be CPU intensive
            //if(!this.getMap().isInMap(en.getLocation())) continue;
            this.getMap().removeEntity(en);
        }
    }
    
    //Basically same as above
    @EventHandler(ignoreCancelled=true)
    public void removeEntitiesOnChunkLoad(ChunkLoadEvent e) {
        if(e.getChunk() != null) return;
        if(e.getChunk() == null) return;
        if(e.getChunk().getEntities() == null || e.getChunk().getEntities().length < 1) return;
        for(Entity en : e.getChunk().getEntities()) {
            if(en == null) continue;
            if(en.getType() == null || !en.getType().isAlive()) continue;
            if(EntityType.PLAYER.equals(en.getType())) continue;
            if(en.getLocation() == null) continue;
            if(!this.getMap().isInMap(en.getLocation())) continue;
            this.getMap().removeEntity(en);
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void removeKilledEntities(EntityDeathEvent e) {
        if(e.getEntity() == null) return;
        this.getMap().removeEntity(e.getEntity());
    }
}

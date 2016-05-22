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

package com.domsplace.GammaZombies.Thread.Map;

import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Map.Region;
import com.domsplace.GammaZombies.Threat.SpawningThreat;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 *
 * @author Dominic Masters
 */
public class MapMobSpawningThread extends MapThread {
    //Instance
    public MapMobSpawningThread(final Map map) {
        super(0, 20, map);
    }
    
    @Override
    public void run() {
        //THIS WILL RUN EVERY TICK!
        final Map m = this.getMap();
        if(m == null) return;
        if(m.getWorld() == null) return;
        
        //Check for Entities to unload
        for(Entity e : m.getWorld().getEntities()) {
            if(e == null) continue;
            if(e.getType() == null) continue;
            if(e.getType() == null || !e.getType().isAlive()) continue;
            if(e.getType().equals(EntityType.PLAYER)) continue;
            if(e.getLocation() == null) continue;
            if(!m.isInMap(e.getLocation())) continue;
            if(m.isEntityInMap(e)) continue;
            e.remove();
        }
        
        List<SpawningThreat> spawningEntities = m.getSpawningThreats(); //This is to make sure we wait for spawning threats
        
        int max = m.getExpectedEntities();
        
        List<Entity> ents = m.getEntities();
        for(Entity e : ents) {
            if(e != null && !e.isDead()) continue;
            m.removeEntity(e);
        }
        
        if(ents.size() >= max) return;
        
        int spawned = 0;
        
        //Gotta Check Each Region
        for(Region r : m.getRegions()) {
            //OK so now let's get how many we need to spawn, and how many are already there
            int needed = r.getExpectedEntities() - spawningEntities.size();
            List<Entity> map = r.getEntities();
            if(map.size() >= needed) continue;
            
            //Spawn what I need to spawn
            for(int i = 0; i <= needed; i++) {
                r.spawnRandomEntity();
                spawned ++;
                //Make sure we don't overspawn
                if(spawned >= max) return;
            }
        }
    }
}

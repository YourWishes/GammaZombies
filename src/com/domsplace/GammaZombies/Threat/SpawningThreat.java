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

package com.domsplace.GammaZombies.Threat;

import com.domsplace.GammaZombies.GammaPlayer;
import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Map.Region;
import com.domsplace.GammaZombies.Thread.Threat.ThreatSpawnEffectThread;
import com.domsplace.GammaZombies.Utils.MessagingUtilities;
import com.domsplace.GammaZombies.Utils.StringUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 *
 * @author Dominic Masters
 */
public class SpawningThreat extends Threat {
    //Instance
    private final ThreatType type;
    private final Map map;
    private final long createdTime;
    private final ThreatSpawnEffectThread effectThread;
    protected boolean isSpawned;
    private Location toSpawnLocation;
    private long spawnTime;
    
    //Overriden Classes must have exactly one constructor of (ThreatType, Map)
    public SpawningThreat(final ThreatType threatType, final Map map) {
        super(threatType, null);
        this.type = threatType;
        this.map = map;
        this.isSpawned = false;
        this.createdTime = System.currentTimeMillis();
        this.toSpawnLocation = map.getRandomLocation();
        this.effectThread = new ThreatSpawnEffectThread(this);
        map.addSpawningThreat(this);
    }
    
    public final Map getMap() {return this.map;}
    public final long getCreatedTime() {return this.createdTime;}
    public final long getSpawnTime() {return this.spawnTime;}
    public final ThreatSpawnEffectThread getThreadSpawnEffectThread() {return this.effectThread;}
    public boolean isSpawned() {return this.isSpawned && this.getEntity() != null;}
    public Location getToSpawnLocation() {return this.toSpawnLocation;}
    
    public final void spawn() {
        this.spawn(this.toSpawnLocation);
    }
    
    public void spawn(Location l) {
        //Remove Flames
        if(l.getBlock() != null) {
            if(Material.FIRE.equals(l.getBlock().getType())) l.getBlock().setType(Material.AIR);
        }
        
        this.setEntity(this.map.getWorld().spawnEntity(l, this.type.getEntityType()));
        if(this.getEntity() instanceof LivingEntity) ((LivingEntity) this.getEntity()).setCustomName(this.getThreatType().getName());
        this.isSpawned = true;
        this.spawnTime = System.currentTimeMillis();
        this.map.addEntity(this.getEntity());
        Region r = this.map.getRegionFromLocation(l);
        if(r != null) r.addEntity(this.getEntity());
        this.map.removeSpawningThreat(this);
        this.map.addThreat(this);
        
        //Broadcast if I need to!
        if(this.getThreatType().getAnnounceOnSpawn()) {
            this.spawnBroadcast();
        }
    }

    public void setToSpawnLocation(Location l) {
        this.toSpawnLocation = l;
    }
    
    @Override
    public void spawnBroadcast() {
        String name = this.getThreatType().getName();
        String msg = StringUtilities.anOrA(name) + " ";
        msg += name + " has spawned.";
        for(GammaPlayer p : this.map.getOnlinePlayers()) {
            Player pl = p.getOnlinePlayer();
            if(pl == null) continue;
            MessagingUtilities.sendMessage(pl, msg);
        }
        //Maybe MSG console
    }
    
    @Override
    public void remove() {
        super.remove();
        this.effectThread.stopAndDeregister();
    }
}

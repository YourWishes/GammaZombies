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

package com.domsplace.GammaZombies.Thread.Threat;

import com.domsplace.GammaZombies.Thread.PluginThread;
import com.domsplace.GammaZombies.Threat.SpawningThreat;
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import org.bukkit.Location;

/**
 *
 * @author Dominic Masters
 */
public class ThreatSpawnEffectThread extends PluginThread {
    public static final long TIME_BETWEEN_FLAMES = PluginThread.secondsToTicks(3);
    
    private final SpawningThreat threat;
    private long lastFlameTime = 0L;
    
    public ThreatSpawnEffectThread(final SpawningThreat threat) {
        super(0, TICKS_PER_SECOND);
        this.threat = threat;
    }
    
    public final SpawningThreat getSpawningThreat() {return this.threat;}
    
    @Override
    public void run() {
        //Should probably stop if any of these initial conditions are met..
        if(this.threat == null) return;
        //if(this.threat.isSpawned()) return;
        if(this.threat.getThreatType() == null) return;
        if(this.threat.getMap() == null || this.threat.getMap().getWorld() == null) return;
        
        long created = this.threat.getCreatedTime();
        long now = System.currentTimeMillis();
        if(PluginThread.secondsToTicks((now - created)/1000) < this.threat.getThreatType().getSpawnCooldown()) {
            //Determine if I should play flames
            if(PluginThread.secondsToTicks((now - lastFlameTime)/1000) < TIME_BETWEEN_FLAMES) return; //Don't play flames
            
            //Play Flames
            Location l = this.threat.getToSpawnLocation();
            LocationUtilities.playFlames(l, true);
            lastFlameTime = now;
            return;
        }
        
        //Spawn
        if(!this.threat.isSpawned()) this.threat.spawn();
        this.stopAndDeregister();
    }
}

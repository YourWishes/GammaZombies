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

import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Thread.PluginThread;
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Dominic Masters
 */
public class Screamer extends SpawningThreat {
    private static final long SCREAM_FREEZE_TIME = 15000;
    
    public Screamer(ThreatType threatType, Map map) {
        super(threatType, map);
    }
    
    @Override
    public void spawn(Location l) {
        super.spawn(l);
        if(this.getEntity() == null) return;
        if(!(this.getEntity() instanceof Skeleton)) return;
        Skeleton skeleton = (Skeleton) this.getEntity();
        skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
    }
    
    public void scream() {
        if(this.getEntity() == null) return;
        //TODO: Add Scream Logic here
        this.scream(this.getEntity().getLocation());
    }
    
    public void scream(Location source) {
        if(source == null) return;
        this.scream(source, null);
    }
    
    public void scream(Location source, LivingEntity target) {
        if(this.getEntity() == null) return;
        if(!this.isSpawned()) return;
        
        //LocationUtilities.playEchoingScream(source, 15, 10);
        LocationUtilities.playScream(source);
        
        if((this.getEntity() instanceof LivingEntity)) {
            PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, (int) PluginThread.secondsToTicks(Screamer.SCREAM_FREEZE_TIME/1000), 2);
            ((LivingEntity) this.getEntity()).addPotionEffect(pe);
        }
        
        double radius = 150.0;
        this.attractNearby(radius, target);
    }
}

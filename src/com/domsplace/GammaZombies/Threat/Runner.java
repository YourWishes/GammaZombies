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
import com.domsplace.GammaZombies.Thread.Threat.RunnerLogicThread;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;

/**
 *
 * @author Dominic Masters
 */
public class Runner extends SpawningThreat {
    private RunnerLogicThread logic;
    
    public Runner(ThreatType threatType, Map map) {
        super(threatType, map);
        this.logic = new RunnerLogicThread(this);
    }
    
    @Override
    public void spawn(Location l) {
        super.spawn(l);
        if(this.getEntity() == null) return;
        if(!(this.getEntity() instanceof Wolf)) return;
        Wolf wolf = (Wolf) this.getEntity();
        wolf.setAngry(true);
        wolf.setMaxHealth(wolf.getMaxHealth() * 3.0d);
        wolf.setHealth(wolf.getMaxHealth());
    }
    
    @Override
    public void remove() {
        super.remove();
        this.logic.stopAndDeregister();
        this.logic = null;
    }
    
    public LivingEntity getTarget() {
        if(!this.isSpawned) return null;
        if(this.getEntity() == null) return null;
        Entity ent = this.getEntity();
        if(!(ent))
        return ent;
    }
}

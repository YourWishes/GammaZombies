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

import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Thread.Map.MapThread;
import com.domsplace.GammaZombies.Thread.PluginThread;
import com.domsplace.GammaZombies.Threat.Threat;
import java.util.List;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

/**
 *
 * @author Dominic Masters
 */
public class ThreatTeleportingThread extends MapThread {
    public ThreatTeleportingThread(final Map map) {
        super(0, PluginThread.TICKS_PER_SECOND, map);
    }
    
    @Override
    public void run() {
        List<Threat> threat = this.getMap().getThreats();
        for(Threat t : threat) {
            if(t == null) continue;
            if(t.getEntity() == null) continue;
            
            Entity e = t.getEntity();
            if(!(e instanceof Creature)) continue;
            Creature c = (Creature) e;
            if(c.getTarget() == null) continue;
            if(!this.getMap().isEntityInMap(c)) continue;
            t.teleportTowards(c.getTarget());
        }
    }
}

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

import com.domsplace.GammaZombies.Event.Threat.ThreatTargetEvent;
import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Threat.Screamer;
import com.domsplace.GammaZombies.Threat.SpawningThreat;
import com.domsplace.GammaZombies.Threat.Threat;
import com.domsplace.GammaZombies.Threat.Threat.ThreatType;
import com.domsplace.GammaZombies.Threat.Vomiter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

/**
 *
 * @author Dominic Masters
 */
public class MapEntityAlteringListener extends MapListener {
    public MapEntityAlteringListener(final Map map) {
        super(map);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void blockMobIgnitionDuringDay(EntityCombustEvent e) {
        //TODO: Maybe add a way to check if they're ignited due to a player
        if(!this.getMap().isEntityInMap(e.getEntity())) return;
        e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void teleportOnTargetPlayer(ThreatTargetEvent e) {
        boolean forward = !e.getReason().equals(TargetReason.TARGET_ATTACKED_ENTITY); //If TRUE, go towards player, FALSE, away
        
        //Teleport a little closer
        LivingEntity p = e.getTarget();
        
        //Determine the distance, don't teleport if under a certain distance
        Threat t = e.getThreat();
        if((t instanceof SpawningThreat) && !((SpawningThreat) t).isSpawned()) return;
        
        if(forward) {
            t.teleportTowards(p);
        } else {
            t.teleportAwayFrom(p);
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void vomitOnTargetPlayer(ThreatTargetEvent e) {
        if(e.getReason().equals(TargetReason.TARGET_ATTACKED_ENTITY)) return;
        Threat t = e.getThreat();
        if((t instanceof SpawningThreat) && !((SpawningThreat) t).isSpawned()) return;
        if(!(t instanceof Vomiter)) return;
        
        ((Vomiter) t).vomitOn(e.getTarget());
    }
    
    @EventHandler(ignoreCancelled=true)
    public void screamOnTargetPlayer(ThreatTargetEvent e) {
        if(!(e.getTarget() instanceof LivingEntity)) return;
        if((e.getThreat() instanceof SpawningThreat) && !((SpawningThreat) e.getThreat()).isSpawned()) return;
        if(!(e.getThreat() instanceof Screamer)) return;
        
        ((Screamer) e.getThreat()).scream(e.getTarget().getLocation(), (LivingEntity) e.getTarget());
    }
    
    @EventHandler(ignoreCancelled=true)
    public void blockStealth(EntityTargetLivingEntityEvent e) {
        if(e.getTarget() == null) return;
        if(e.getEntity() == null) return;
        if(!(e.getTarget() instanceof LivingEntity)) return;
        
        ThreatType type = ThreatType.getFromEntityType(e.getEntity().getType());
        if(type == null) return;
        
        if(!this.getMap().isEntityInMap(e.getEntity())) return;
        
        //Teleport a little closer
        Entity en = e.getEntity();
        LivingEntity p = e.getTarget();
        
        //Determine the distance, don't teleport if under a certain distance
        Threat t = this.getMap().getThreat(en);
        if(t == null) return;
        if((t instanceof SpawningThreat) && !((SpawningThreat) t).isSpawned()) return;
        
        boolean is = e.getReason().equals(TargetReason.TARGET_ATTACKED_ENTITY) ||
                e.getReason().equals(TargetReason.PIG_ZOMBIE_TARGET) ||
                e.getReason().equals(TargetReason.TARGET_ATTACKED_OWNER) ||
                e.getReason().equals(TargetReason.OWNER_ATTACKED_TARGET)
        ;
        
        if(is || t.canSee(p)) {
            //Fire Event
            ThreatTargetEvent evt = new ThreatTargetEvent(t, type, this.getMap(), p, e.getReason());
            evt.fireEvent();
            if(!evt.isCancelled()) return;
            t.attractNearby(15.0d, p);
        }
        e.setCancelled(true);
    }
}

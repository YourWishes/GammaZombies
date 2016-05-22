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
import com.domsplace.GammaZombies.Threat.Vomiter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

/**
 *
 * @author Dominic Masters
 */
public class VomiterLogicThread extends PluginThread {
    private final Vomiter vomiter;
    
    public VomiterLogicThread(final Vomiter vomiter) {
        super(0, 3);    //May Make ASYNC..
        this.vomiter = vomiter;
    }
    
    public final Vomiter getVomiter() {return this.vomiter;}
    
    @Override
    public void run() {
        if(!this.vomiter.isSpawned()) return;
        Entity pig = this.vomiter.getEntity();
        if(pig == null) return;
        
        if(!(pig instanceof Creature)) return;
        Creature lilPig = (Creature) pig;
        
        //Do I have a target?
        LivingEntity targ = lilPig.getTarget();
        if(targ != null) {
            //VOMIT        
            if(targ instanceof Player) {
                Player p = (Player) targ;
                if(GameMode.CREATIVE.equals(p.getGameMode())) return;
            }
            
            if(!this.vomiter.canSee(targ)) {
                lilPig.setTarget(null);
                return;
            }
            
            this.vomiter.vomitOn(targ);
            return;
        }
        
        //Determine a suitable target
        double vd = this.vomiter.getThreatType().getViewDistance();
        for(Entity e : pig.getNearbyEntities(vd, vd, vd)) {
            if(e == null) continue;
            if(!(e instanceof LivingEntity)) continue;
            if(!(e instanceof Player)) continue; //Temporarily only target players
            LivingEntity en = (LivingEntity) e;
            if(!this.vomiter.canSee(en)) continue;
            targ = en;
            break;
        }
        
        if(targ == null) return;
        
        EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(lilPig, targ, EntityTargetEvent.TargetReason.CUSTOM);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            lilPig.setTarget(null);
        } else {
            lilPig.setTarget(targ); //Does not fire Event, we need to
            //lilPig.damage(0, targ); //Temporary Work-Around
        }
    }
}

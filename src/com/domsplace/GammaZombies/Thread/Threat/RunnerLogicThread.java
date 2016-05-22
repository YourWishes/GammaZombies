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
import com.domsplace.GammaZombies.Threat.Runner;
import com.domsplace.GammaZombies.Utils.MessagingUtilities;
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
public class RunnerLogicThread extends PluginThread {
    private final Runner runner;
    
    public RunnerLogicThread(final Runner runner) {
        super(0, 3);    //May Make ASYNC..
        this.runner = runner;
    }
    
    public final Runner getRunner() {return this.runner;}
    
    @Override
    public void run() {
        if(!this.runner.isSpawned()) return;
        Entity wolf = this.runner.getEntity();
        if(wolf == null) return;
        
        if(!(wolf instanceof Creature)) return;
        Creature wolfCreature = (Creature) wolf;
        
        //Do I have a target?
        LivingEntity targ = wolfCreature.getTarget();
        if(targ != null) {
            if(targ instanceof Player) {
                Player p = (Player) targ;
                if(GameMode.CREATIVE.equals(p.getGameMode())) return;
            }
            
            if(!this.runner.canSee(targ)) {
                wolfCreature.setTarget(null);
                return;
            }
            
            //this.runner.vomitOn(targ);
            return;
        }
        
        //Determine a suitable target
        double vd = this.runner.getThreatType().getViewDistance();
        for(Entity e : wolf.getNearbyEntities(vd, vd, vd)) {
            if(e == null) continue;
            if(!(e instanceof LivingEntity)) continue;
            if(!(e instanceof Player)) continue; //Temporarily only target players
            LivingEntity en = (LivingEntity) e;
            if(!this.runner.canSee(en)) continue;
            targ = en;
            break;
        }
        
        if(targ == null) return;
        
        EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(wolfCreature, targ, EntityTargetEvent.TargetReason.CUSTOM);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            wolfCreature.setTarget(null);
        } else {
            MessagingUtilities.broadcast("CALLED HERE");
            wolfCreature.setTarget(targ); //Does not fire Event, we need to
            wolfCreature.damage(0, targ); //Temporary Work-Around
        }
    }
}

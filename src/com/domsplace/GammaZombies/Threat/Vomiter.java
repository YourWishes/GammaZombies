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
import com.domsplace.GammaZombies.Thread.Threat.VomiterLogicThread;
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 *
 * @author Dominic Masters
 */
public class Vomiter extends SpawningThreat {
    public static final double MAX_VOMIT_RANGE = 5.0d;
    public static final long VOMIT_COOLDOWN = 15000;    //10 Seconds
    public static final long VOMIT_FREEZE_TIME = 10000;
    
    private long lastVomit;
    private VomiterLogicThread logic;
    
    public Vomiter(ThreatType threatType, Map map) {
        super(threatType, map);
        this.logic = new VomiterLogicThread(this);
    }
    
    public VomiterLogicThread getVomiterLogicThread() {return this.logic;}
    
    @Override
    public void spawn(Location l) {
        super.spawn(l);
    }
    
    public void vomitOn(LivingEntity ent) {
        if(this.getEntity() == null) return;
        long now = System.currentTimeMillis();
        if((now - this.lastVomit) < VOMIT_COOLDOWN) return;
        this.lastVomit = now;
        
        //Vomit
        double xDiff = this.getEntity().getLocation().getX() - ent.getLocation().getX();
        double yDiff = this.getEntity().getLocation().getY() - ent.getLocation().getY();
        double zDiff = this.getEntity().getLocation().getZ() - ent.getLocation().getZ();
        
        double dist = Math.sqrt(xDiff + yDiff + zDiff);
        if(dist > MAX_VOMIT_RANGE) return;
        
        ent.playEffect(EntityEffect.HURT);
        ent.getWorld().playEffect(ent.getLocation(), Effect.POTION_BREAK, PotionType.WEAKNESS.getDamageValue());
        
        PotionEffect pe = new PotionEffect(PotionEffectType.WEAKNESS, (int) PluginThread.secondsToTicks(5), 1);
        ent.addPotionEffect(pe);
        pe = new PotionEffect(PotionEffectType.BLINDNESS, (int) PluginThread.secondsToTicks(5), 1);
        ent.addPotionEffect(pe);
        pe = new PotionEffect(PotionEffectType.SLOW, (int) PluginThread.secondsToTicks(5), 1);
        ent.addPotionEffect(pe);
        pe = new PotionEffect(PotionEffectType.CONFUSION, (int) PluginThread.secondsToTicks(5), 1);
        ent.addPotionEffect(pe);
        pe = new PotionEffect(PotionEffectType.POISON, (int) PluginThread.secondsToTicks(5), 1);
        ent.addPotionEffect(pe);
        
        //Swoosh
        LocationUtilities.playFlames(this.getEntity().getLocation(), false);
        
        //ent.damage(dist / (Vomiter.MAX_VOMIT_RANGE));
        
        //Look at (needs Work)
        this.lookAt((ent instanceof LivingEntity ? ((LivingEntity) ent).getEyeLocation() : ent.getLocation()));
        
        //Freeze
        if(!(this.getEntity() instanceof LivingEntity)) return;
        
        //TODO: Make this a future (Vomit after a few seconds)
        pe = new PotionEffect(PotionEffectType.SLOW, (int) PluginThread.secondsToTicks(Vomiter.VOMIT_FREEZE_TIME/1000), 2);
        ((LivingEntity) this.getEntity()).addPotionEffect(pe);
    }
    
    @Override
    public void remove() {
        super.remove();
        this.logic.stopAndDeregister();
        this.logic = null;
    }
}

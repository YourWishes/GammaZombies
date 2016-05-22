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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Dominic Masters
 */
public class Brute extends SpawningThreat {
    
    public Brute(ThreatType threatType, Map map) {
        super(threatType, map);
    }
    
    @Override
    public void spawn(Location l) {
        super.spawn(l);
        if(this.getEntity() == null) return;
        if(!(this.getEntity() instanceof PigZombie)) return;
        PigZombie pz = (PigZombie) this.getEntity();
        pz.setAngry(true);
        //Give Golden Sword
        pz.getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
        
        //LocationUtilities.playEchoingScream(this.getEntity().getLocation(), 15, 15);  //Example of Scream on Spawn
    }
}

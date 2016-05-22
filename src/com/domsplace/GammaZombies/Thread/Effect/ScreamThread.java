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

package com.domsplace.GammaZombies.Thread.Effect;

import com.domsplace.GammaZombies.Thread.PluginThread;
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

/**
 *
 * @author Dominic Masters
 */
public class ScreamThread extends PluginThread {
    private final Location center;
    
    private final int amount;
    private final int spaced;
    private int playedAmount = 0;
    
    public ScreamThread(final Location center, final int amount, final int spaced) {
        super(0, 5);
        this.center = center;
        this.amount = amount;
        this.spaced = spaced;
    }
    
    public final Location getScreamSource() {return this.center;}
    public final int getAmount() {return this.amount;}
    public final int getSpaced() {return this.spaced;}
    public int getPlayedAmount() {return this.playedAmount;}
    
    @Override
    public void run() {
        if(this.center == null) {
            this.stopAndDeregister();
            return;
        }
        
        List<Location> toPlay = this.getAreasToPlay();
        for(Location l : toPlay) {
            LocationUtilities.playScream(l);
        }
        
        playedAmount++;
        
        if(playedAmount >= this.amount) {
            this.stopAndDeregister();
        }
    }
    
    private List<Location> getAreasToPlay() {
        List<Location> areas = new ArrayList<Location>();
        
        if(this.playedAmount == 0) {
            areas.add(center);
            return areas;
        }
        
        int max = playedAmount * spaced;
        int min = -max;
        
        //TODO: "CIRCULATE" more
        
        Location minX = center.clone().add(min, 0, 0);
        Location maxX = center.clone().add(max, 0, 0);
        Location minZ = center.clone().add(0, 0, min);
        Location maxZ = center.clone().add(0, 0, max);
        
        areas.add(minX);
        areas.add(maxX);
        areas.add(minZ);
        areas.add(maxZ);
        
        return areas;
    }
}

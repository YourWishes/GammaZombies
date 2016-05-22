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

import com.domsplace.GammaZombies.Map.Map;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 *
 * @author Dominic Masters
 */
public class MapServerListener extends MapListener {
    public MapServerListener(final Map map) {
        super(map);
    }
    
    @EventHandler(ignoreCancelled=true)
    public void unloadMapOnWorldUnload(WorldUnloadEvent e) {
        if(!this.getMap().isInMap(e.getWorld())) return;
        this.getMap().unload();
    }
}

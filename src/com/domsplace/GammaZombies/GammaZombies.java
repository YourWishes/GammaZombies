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

package com.domsplace.GammaZombies;

import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Plugin.Plugin;
import com.domsplace.GammaZombies.Plugin.PluginInfo;
import com.domsplace.GammaZombies.Thread.PluginThread;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dominic Masters
 */
public class GammaZombies extends JavaPlugin implements Plugin {
    public static GammaZombies instance;
    
    private boolean enabled;
    private PluginInfo pluginInfo;
    
    @Override
    public void onEnable() {
        instance = this;
        
        this.pluginInfo = new PluginInfo(this);
        
        //DEBUGGING ONLY, CREATE EXAMPLE MAP
        Map map = new Map(this.getServer().getWorlds().get(0));
        map.addPlayer(GammaPlayer.getPlayer("DOMIN8TRIX25"));
        map.addPlayer(GammaPlayer.getPlayer("oxafemble"));
        map.addPlayer(GammaPlayer.getPlayer("Carnage42"));
        map.addPlayer(GammaPlayer.getPlayer("Lady_Penny"));
        map.addPlayer(GammaPlayer.getPlayer("NekoGabby"));
        map.addPlayer(GammaPlayer.getPlayer("Zliffy"));
        
        enabled = true;
        getLogger().log(Level.INFO, "Enabled {0} v{1} successfully!", new Object[] {this.getName(), this.pluginInfo.getPluginVersion()});
        getLogger().log(Level.INFO, "Found {0} Maps!", new Object[] {Map.getMaps().size()});
    }
    
    @Override
    public void onDisable() {
        for(Map map : Map.getMaps()) {
            map.unload();
        }
        PluginThread.stopAllThreads();
        
        if(!enabled) {
            getLogger().log(Level.SEVERE, "Failed to enable {0}", this.getName());
            return;
        }
    }
    
    @Override public boolean isSuccessfullyEnabled() {return this.enabled;}
    @Override public PluginInfo getPluginInfo() {return this.pluginInfo;}
    
    @Override public void setSuccessfullyEnabled(boolean t) {this.enabled = t;}
    
    @Override public void disable() {
        this.enabled = false;
        this.onDisable();
        this.getPluginLoader().disablePlugin(this);
    }
    
    public static void debug(Object o) {
        String msg = "§d[§bDEBUG§d] §a" + o.toString();
        Bukkit.getConsoleSender().sendMessage(msg);
    }
}

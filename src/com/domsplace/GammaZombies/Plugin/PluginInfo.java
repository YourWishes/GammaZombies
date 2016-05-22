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

package com.domsplace.GammaZombies.Plugin;

import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Dominic Masters
 */
public class PluginInfo {
    private final Plugin plugin;    
    private final YamlConfiguration pluginYML;
    
    public PluginInfo(Plugin plugin) {
        this.plugin = plugin;
        
        InputStream pluginYMLStream = plugin.getResource("plugin.yml");
        
        this.pluginYML = YamlConfiguration.loadConfiguration(pluginYMLStream);
        try {pluginYMLStream.close();} catch(IOException e) {}
    }
    
    public Plugin getPlugin() {return this.plugin;}
    public Plugin getDomsPlugin() {
        if(this.plugin instanceof Plugin) return (Plugin) this.plugin;
        return null;
    }
    public YamlConfiguration getPluginYML() {return this.pluginYML;}
    public String getPluginVersion() {return this.pluginYML.getString("version", "unknown");}
}

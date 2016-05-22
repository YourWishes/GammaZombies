/*
 * Copyright 2013 Dominic.
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

package com.domsplace.GammaZombies.Listener;

import com.domsplace.GammaZombies.GammaZombies;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class PluginListener implements Listener {
    private static final List<PluginListener> LISTENERS = new ArrayList<PluginListener>();
    
    protected static void regsiterListener(PluginListener listener) {
        Bukkit.getPluginManager().registerEvents(listener, GammaZombies.instance);
        PluginListener.getListeners().add(listener);
    }
    
    protected static void deRegsiterListener(PluginListener listener) {
        Method[] methods = listener.getClass().getMethods();
        for(Method m : methods) {
            EventHandler ev = m.getAnnotation(EventHandler.class);
            if(ev == null) continue;
            Class<?>[] params = m.getParameterTypes();
            for(Class<?> param : params) {
                if(param == null) continue;
                Class<? extends Event> e = param.asSubclass(Event.class);
                if(e == null) continue;
                try {
                    Method h = e.getMethod("getHandlerList");
                    HandlerList r = (HandlerList) h.invoke(null);
                    r.unregister(listener);
                } catch(Exception ex) {
                    continue;
                }
            }
        }
        PluginListener.getListeners().remove(listener);
    }
    
    public static List<PluginListener> getListeners() {
        return PluginListener.LISTENERS;
    }
    
    //Instance
    public PluginListener() {
        PluginListener.regsiterListener(this);
    }
    
    public void deRegisterListener() {
        PluginListener.deRegsiterListener(this);
    }
}

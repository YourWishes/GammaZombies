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

package com.domsplace.GammaZombies.Thread;

import com.domsplace.GammaZombies.GammaZombies;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class PluginThread implements Runnable {
    public static final boolean ASYNC_DEFAULT = false;
    public static final int TICKS_PER_SECOND = 20;
    
    private static final List<PluginThread> THREADS = new ArrayList<PluginThread>();
    
    //Static
    public static void stopAllThreads() {
        for(PluginThread t : THREADS) {
            if(t == null) continue;
            if(t.getThread() == null) continue;
            try {
                t.stopThread();
            } catch(Exception e) {}
        }
    }
    
    public static final void registerThread(PluginThread thread) {
        PluginThread.getThreads().add(thread);
    }
    
    public static List<PluginThread> getThreads() {return new ArrayList<PluginThread>(PluginThread.THREADS);}
    
    public static final long ticksToSeconds(long ticks) {return ticks / TICKS_PER_SECOND;}
    public static final long secondsToTicks(long seconds) {return seconds * TICKS_PER_SECOND;}
    
    //Instance
    private BukkitTask thread;
    
    public PluginThread(long delay, long repeat) {
        this(delay, repeat, ASYNC_DEFAULT);
    }
    
    public PluginThread(long delay, long repeat, boolean async) {        
        if(async) {
            this.thread = Bukkit.getScheduler().runTaskTimerAsynchronously(GammaZombies.instance, this, delay, repeat);
        } else {
            this.thread = Bukkit.getScheduler().runTaskTimer(GammaZombies.instance, this, delay, repeat);
        }
        
        PluginThread.registerThread(this);
    }
    
    public BukkitTask getThread() {
        return this.thread;
    }
    
    public void stopAndDeregister() {
        this.stopThread();
        this.deRegister();
    }
    
    public void stopThread() {
        if(this.thread == null) return;
        this.getThread().cancel();
    }
    
    public void deRegister() {
        PluginThread.THREADS.remove(this);
    }

    @Override
    public void run() {
    }
}

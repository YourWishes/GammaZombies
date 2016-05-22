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

package com.domsplace.GammaZombies.Utils;

import com.domsplace.GammaZombies.GammaZombies;
import com.domsplace.GammaZombies.Thread.Effect.ScreamThread;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 *
 * @author Dominic Masters
 */
public class LocationUtilities {
    public static final int WATER_Y = 64;
    
    /**
     *
     * @param l trying to get Safe Location for
     * @return Returns a safe Location for Entities, but will return the same location if no safe location exists.
     */
    public static final Location getSafeLocation(Location l) {
        int unsafeY = l.getBlockY();
        if (unsafeY < 0) return null;
        for (int i = unsafeY; i >= 0; i--) {
            if (i < 0) return null;
            Block b = l.getWorld().getBlockAt(l.getBlockX(), i, l.getBlockZ());
            if (b == null) return null;
            if (b.getType().equals(Material.AIR)) continue;
            double safeY = l.getY() - (unsafeY - i);
            return new Location(l.getWorld(), l.getX(), safeY + 1, l.getZ(), l.getYaw(), l.getPitch());
        }
        return l;
    }
    
    public static final void playFlames(Location l, boolean setFireBlock) {
        if(l == null) return;
        if(setFireBlock) {
            Block b = l.getBlock();
            if(b != null && (b.getType() == null || b.getType().equals(Material.AIR))) {
                b.setType(Material.FIRE);
            }
        }
        l.getWorld().playEffect(l, Effect.MOBSPAWNER_FLAMES, null);
    }
    
    public static final void playSwoosh(Location l) {
        l.getWorld().playEffect(l, Effect.ENDER_SIGNAL, null);
        l.getWorld().playEffect(l, Effect.GHAST_SHOOT, null);
        l.getWorld().playEffect(l, Effect.SMOKE, BlockFace.UP);
    }
    
    //Nearby Entites will be drawn to the sound
    public static final void playScream(Location l) {
        if(l == null) return;
        l.getWorld().playEffect(l, Effect.GHAST_SHRIEK, null);
    }
    
    public static final ScreamThread playEchoingScream(Location l, int amount, int spaced) {
        final ScreamThread thread = new ScreamThread(l, amount, spaced);
        return thread;
    }
    
    public static final String locationToHumanString(Location l) {return locationToHumanString(l, false);}
    public static final String locationToHumanString(Location l, boolean showPitchYaw) {
        String x = "";
        x += (int) l.getX() + ", ";
        x += (int) l.getY() + ", ";
        x += (int) l.getZ();
        if(showPitchYaw) {
            x += ", " + l.getPitch() + ", " + l.getYaw();
        }
        return x;
    }
    
    public static final Location lookAt(Location loc, Location lookat) {
        loc = loc.clone();

        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        if(dx != 0) {
            if(dx < 0) {
                loc.setYaw((float)(1.5 * Math.PI));
            } else {
                loc.setYaw((float)(0.5 * Math.PI));
            }
            loc.setYaw((float)loc.getYaw() - (float)Math.atan(dz / dx));
        } else if(dz < 0) {
            loc.setYaw((float)Math.PI);
        }

        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        loc.setPitch((float)-Math.atan(dy / dxz));

        loc.setYaw(-loc.getYaw() * 180f / (float)Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float)Math.PI);

        return loc;
    }
}

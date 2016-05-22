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

package com.domsplace.GammaZombies.Map;

import com.domsplace.GammaZombies.GammaPlayer;
import com.domsplace.GammaZombies.GammaZombies;
import com.domsplace.GammaZombies.Threat.SpawningThreat;
import com.domsplace.GammaZombies.Threat.Threat.ThreatType;
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import static com.domsplace.GammaZombies.Utils.LocationUtilities.WATER_Y;
import com.domsplace.GammaZombies.Utils.RandomUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author Dominic Masters
 */
public class Region {
    public static final int REGION_SIZE = 32;
    
    public enum FirstName {
        BURNT("Burnt")
        ;
        
        public static final FirstName getRandom() {
            return FirstName.values()[new Random().nextInt(FirstName.values().length)];
        }
        
        private final String text;
        
        FirstName(final String text) {
            this.text = text;
        }
        
        public final String getText() {return this.text;}
    };
    
    public enum LastName {
        PLAINS("Plains")
        ;
        
        public static final LastName getRandom() {
            return LastName.values()[new Random().nextInt(LastName.values().length)];
        }
        
        private final String text;
        
        LastName(final String text) {
            this.text = text;
        }
        
        public final String getText() {return this.text;}
    };
    
    public static int translateRegionToCoord(double t) {
        double x = Math.floor(t) * ((double) REGION_SIZE);
        x = Math.floor(x);
        return (int) x;
    }
    
    public static int translateCoordToRegion(double t) {
        double x = t / ((double) REGION_SIZE);
        x = Math.floor(x);
        return (int) x;
    }
    
    //Instance
    private final Map map;
    private final List<Entity> entities;
    private FirstName firstName;
    private LastName lastName;
    private boolean hotspot = false;
    
    private int x;  //REGION BASED X AND Y (x * REGION_SIZE) = COORDS
    private int z;
    
    public Region(final Map map) {
        this(map, FirstName.getRandom(), LastName.getRandom());
    }
    
    public Region(final Map map, FirstName firstName, LastName lastName) {
        this.map = map;
        this.firstName = firstName;
        this.lastName = lastName;
        
        this.entities = new ArrayList<Entity>();
    }
    
    public final Map getMap() {return this.map;}
    public final List<Entity> getEntities() {return new ArrayList<Entity>(this.entities);}
    public FirstName getFirstName() {return this.firstName;}
    public LastName getLastName() {return this.lastName;}public int getX() {return this.x;}
    public int getZ() {return this.z;}
    public int getCoordMinX() {return translateRegionToCoord(x);}
    public int getCoordMinZ() {return translateRegionToCoord(z);}
    public int getCoordMaxX() {return getCoordMinX() + REGION_SIZE - 1;}
    public int getCoordMaxZ() {return getCoordMinZ() + REGION_SIZE - 1;}
    public String getName() {return this.firstName.text + " " + this.lastName.text;}
    @Override public String toString() {return this.getName();}
    public Region getRelative(int x, int z) {return map.getRegion(this.x + x, this.z + z);}
    
    public void setX(int x) {this.x = x;}
    public void setZ(int z) {this.z = z;}
    public void setFirstName(FirstName name) {this.firstName = name;}
    public void setLastName(LastName name) {this.lastName = name;}
    public void setHotspot(boolean t) {this.hotspot = t;}
    
    public boolean isHotspot() {return this.hotspot;}
    
    public void addEntity(Entity e) {this.entities.add(e);}

    public void removeEntity(Entity e) {this.entities.remove(e);}
    
    public boolean isInRegion(int x, int z) {
        return (x >= this.getCoordMinX() && x <= this.getCoordMaxX()) && 
                (z >= this.getCoordMinZ() && z <= this.getCoordMaxZ());
    }
    
    public boolean isInRegion(int x1, int x2, int z1, int z2) {
        return this.isInRegion(Math.min(x1, x2), Math.min(z1, z2)) && 
                this.isInRegion(Math.max(x1, x2), Math.max(z1, z2));
    }
    
    public boolean isInRegion(double x, double z) {return isInRegion((int) x, (int) z);}
    
    public boolean isInRegion(Location l) {return (this.map.getWorld().equals(l.getWorld())) && isInRegion(l.getX(), l.getZ());}
    
    public boolean isInRegion(World w) {
        if(w == null && this.map.getWorld() == null) return true;
        if(w == null) return false;
        return w.equals(this.map.getWorld());
    }
    
    //Complex
    public List<GammaPlayer> getPlayers() {
        List<GammaPlayer> players = new ArrayList<GammaPlayer>();
        for(GammaPlayer p : this.map.getOnlinePlayers()) {
            Player pl = p.getOnlinePlayer();
            if(pl == null) continue;
            if(!this.isInRegion(pl.getLocation())) continue;
            players.add(p);
        }
        return players;
    }
    
    public int getExpectedEntities() {
        //Total = SUM(X) * Y + Z
        //FOR EACH PLAYER IN REGION: x = ceil(plyr_lvl * .5)
        //Y = isHotspot ? DIFFICULTY : DIFFICULTY * NUM_PLAYERS_IN_MAP
        //Z = ceil(NUM_PLAYERS_IN_REGION * .5)
        
        List<GammaPlayer> inRegion = this.getPlayers();
        if(inRegion.size() < 1) return 0;
        double x = 0.0d;
        for(GammaPlayer player : inRegion) {
            x += Math.ceil((double)player.getLevel() * 0.5d);
        }
        
        int diff = this.getMap().getDifficulty().getDifficulty();
        double y = this.isHotspot() ? diff * .5 : diff * inRegion.size() * .5;
        double z = Math.ceil((double) inRegion.size() * 0.5d);
        
        int k = (int)Math.ceil(x) * (int)Math.ceil(y) + (int)Math.ceil(z);
        return k;
    }
    
    public Location getRandomLocation() {
        int rx = RandomUtilities.getRandomIntBetween(this.getCoordMinX(), this.getCoordMaxX());
        int rz = RandomUtilities.getRandomIntBetween(this.getCoordMinZ(), this.getCoordMaxZ());
        Location l = new Location(this.map.getWorld(), rx, WATER_Y, rz);
        if(!this.map.isInMap(l)) return getRandomLocation();    //Make sure the Location is in the Map, MIGHT STACK OVERFLOW
        return LocationUtilities.getSafeLocation(l);
    }

    public SpawningThreat spawnRandomEntity() {
        ThreatType randomType = ThreatType.getRandomThreatType();
        return this.spawnEntityAtRandomLocation(randomType);
    }
    
    public SpawningThreat spawnEntityAtRandomLocation(ThreatType type) {
        return this.spawnEntity(type, this.getRandomLocation());
    }
    
    public SpawningThreat spawnEntity(ThreatType type, Location l) {
        GammaZombies.debug("Spawning " + type.name() + " in " + LocationUtilities.locationToHumanString(LocationUtilities.getSafeLocation(l)));
        SpawningThreat threat = type.createThreat(this.map);
        if(threat == null) return null; //May Occur if ThreatType is Invalid (Check ThreatType.createThreat)
        threat.setToSpawnLocation(l);
        return threat;
    }
}

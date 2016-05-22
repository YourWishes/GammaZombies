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
import com.domsplace.GammaZombies.Listener.Map.MapEntityAlteringListener;
import com.domsplace.GammaZombies.Listener.Map.MapPlayerOutOfBoundsListener;
import com.domsplace.GammaZombies.Listener.Map.MapServerListener;
import com.domsplace.GammaZombies.Listener.Map.MapSpawningListener;
import com.domsplace.GammaZombies.Thread.Map.MapMobSpawningThread;
import com.domsplace.GammaZombies.Thread.Threat.ThreatTeleportingThread;
import com.domsplace.GammaZombies.Threat.SpawningThreat;
import com.domsplace.GammaZombies.Threat.Threat;
import com.domsplace.GammaZombies.Threat.Threat.ThreatType;
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import static com.domsplace.GammaZombies.Utils.LocationUtilities.WATER_Y;
import com.domsplace.GammaZombies.Utils.RandomUtilities;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author Dominic Masters
 */
public class Map {
    public static final int CHUNK_SIZE = 16;
    
    private static final List<Map> MAPS = new ArrayList<Map>();
    
    public static final void addMap(Map map) {MAPS.add(map);}
    public static final void removeMap(Map map) {MAPS.remove(map);}
    public static final List<Map> getMaps() {return new ArrayList<Map>(MAPS);}
    
    //Enums
    public enum Difficulty {
        EASY(1),
        MEDIUM(2),
        HARD(3),
        ULTRA(4);
        
        private final int diff;
        
        Difficulty(final int diff) {
            this.diff = diff;
        };
        
        public int getDifficulty() {return this.diff;}
    };
    
    //Instances
    private final World world;
    private final List<Entity> entities;
    private final List<GammaPlayer> players;
    private final List<Region> regions;
    private final List<SpawningThreat> spawningThreats;
    private final List<Threat> threats;
    
    private Difficulty difficulty;
    
    private MapPlayerOutOfBoundsListener boundListener;
    private MapServerListener serverListener;
    private MapSpawningListener spawningListener;
    private MapEntityAlteringListener alteringListener;
    
    private MapMobSpawningThread spawningThread;
    private ThreatTeleportingThread threatTeleportingThread;
    
    private int x1;
    private int x2;
    private int z1;
    private int z2;
    
    public Map(World world) {
        this(world, Difficulty.EASY);
    }
    
    public Map(World world, Difficulty difficulty) {
        this.world = world;        
        this.entities = new ArrayList<Entity>();
        this.players = new ArrayList<GammaPlayer>();
        this.regions = new ArrayList<Region>();
        this.spawningThreats = new ArrayList<SpawningThreat>();
        this.threats = new ArrayList<Threat>();
        
        this.difficulty = difficulty;
        
        //Setup Region Stuff
        
        //Default Values
        this.x1 = -1000;
        this.z1 = -1000;
        this.x2 = 1000;
        this.z2 = 1000;
        //Check Regions (May Remove Later..)
        this.checkRegions();
        
        this.boundListener = new MapPlayerOutOfBoundsListener(this);
        this.serverListener = new MapServerListener(this);
        this.spawningListener = new MapSpawningListener(this);
        this.alteringListener = new MapEntityAlteringListener(this);
        
        this.spawningThread = new MapMobSpawningThread(this);
        this.threatTeleportingThread = new ThreatTeleportingThread(this);
        
        this.register();
    }
    
    public World getWorld() {return this.world;}
    public List<Entity> getEntities() {return new ArrayList<Entity>(this.entities);}
    public List<GammaPlayer> getPlayers() {return new ArrayList<GammaPlayer>(this.players);}
    public List<Region> getRegions() {return new ArrayList<Region>(this.regions);}
    public List<SpawningThreat> getSpawningThreats() {return new ArrayList<SpawningThreat>(this.spawningThreats);}
    public List<Threat> getThreats() {return new ArrayList<Threat>(this.threats);}
    public Difficulty getDifficulty() {return this.difficulty;}
    public MapPlayerOutOfBoundsListener getBoundListener() {return this.boundListener;}
    public MapServerListener getServerListener() {return this.serverListener;}
    public MapSpawningListener getSpawningListener() {return this.spawningListener;}
    public MapEntityAlteringListener getAlteringListener() {return this.alteringListener;}
    
    public List<GammaPlayer> getOnlinePlayers() {return GammaPlayer.filterOnline(this.players);}
    
    public void setDifficulty(Difficulty difficulty) {this.difficulty = difficulty;}
    
    public void addSpawningThreat(SpawningThreat threat) {this.spawningThreats.add(threat);}
    public void addThreat(Threat threat) {this.threats.add(threat);}
    public void addEntity(Entity e) {this.entities.add(e);}
    public void addPlayer(GammaPlayer p) {this.players.add(p);}
    public void addRegion(Region r) {this.regions.add(r);}
    
    public void removeEntity(Entity e) {
        this.entities.remove(e);
        //May Remove, Just makes sure to remove entities from the Regions
        for(Region r : this.regions) {
            r.removeEntity(e);
        }
        this.threats.remove(this.getThreat(e));
    }
    public void removeSpawningThreat(SpawningThreat threat) {this.spawningThreats.remove(threat);}
    public void removeThreat(Threat threat) {this.threats.remove(threat);}
    public void removePlayer(GammaPlayer p) {this.players.remove(p);}
    public void removeRegion(Region r) {this.regions.remove(r);}
    
    public int getHighestX() {return Math.max(x1, x2);}
    public int getLowestX() {return Math.min(x1, x2);}
    public int getHighestZ() {return Math.max(z1, z2);}
    public int getLowestZ() {return Math.min(z1, z2);}
    
    public Location getRandomLocation() {
        int rx = RandomUtilities.getRandomIntBetween(this.getLowestX(), this.getHighestX());
        int rz = RandomUtilities.getRandomIntBetween(this.getLowestZ(), this.getHighestZ());
        Location l = new Location(this.world, rx, WATER_Y, rz);
        if(!this.isInMap(l)) return getRandomLocation();    //Make sure the Location is in the Map, MIGHT STACK OVERFLOW
        return LocationUtilities.getSafeLocation(l);
    }

    public int getExpectedEntities() {
        int t = 0;
        for(Region r : this.regions) {
            t += r.getExpectedEntities();
        }
        return t;
    }
    
    public Threat getThreat(Entity e) {
        if(e == null) return null;
        if(!this.isEntityInMap(e)) return null;
        for(Threat t : this.threats) {
            if(t.getEntity().equals(e)) return t;
        }
        ThreatType type = ThreatType.getFromEntityType(e.getType());
        if(type == null) return null;
        Threat t = type.createThreat(this);
        if(t == null) return null;
        if(!(t instanceof SpawningThreat)) return null;
        t.setEntity(e);
        this.removeSpawningThreat((SpawningThreat) t);
        SpawningThreat st = (SpawningThreat) t;
        st.getThreadSpawnEffectThread().stopAndDeregister();
        this.threats.add(t);
        return t;
    }
    
    public void setDimensions(int x1, int x2, int z1, int z2) {
        this.x1 = x1; this.x2 = x2; this.z1 = z1; this.z2 = z2;
    }
    
    public boolean isInMap(int x, int z) {
        return (x >= this.getLowestX() && x <= this.getHighestX()) && 
                (z >= this.getLowestZ() && z <= this.getHighestZ());
    }
    
    public boolean isInMap(int x1, int x2, int z1, int z2) {
        return this.isInMap(Math.min(x1, x2), Math.min(z1, z2)) && 
                this.isInMap(Math.max(x1, x2), Math.max(z1, z2));
    }
    
    public boolean isInMap(double x, double z) {return isInMap((int) x, (int) z);}
    
    public boolean isInMap(Location l) {return (l.getWorld().equals(this.world)) && isInMap(l.getX(), l.getZ());}
    
    public boolean isInMap(World w) {
        if(w == null && this.world == null) return true;
        if(w == null) return false;
        return w.equals(this.world);
    }
    
    public boolean isInMap(Region r) {
        return isInMap(r.getCoordMinX(), r.getCoordMaxX(), r.getCoordMinZ(), r.getCoordMaxZ());
    }
    
    /*  Could be CPU intensive..
    public boolean isInMap(Chunk c) {
        int x = c.getX();
        int z = c.getZ();
        int maxX = c.getX() + Map.CHUNK_SIZE;
        int maxZ = c.getZ() + Map.CHUNK_SIZE;
        
        for(int n = x; n < maxX; n++) {
            for(int m = z; m < maxZ; m++) {
                if(isInMap(n,m)) return true;
            }
        }
        
        return false;
    }
    */
    
    //Probably CPU Intensive..
    public boolean isEntityInMap(Entity e) {
        return this.entities.contains(e);
    }

    public boolean isPlayerInMap(Player player) {
        return isPlayerInMap(GammaPlayer.getPlayer(player));
    }
    
    public boolean isPlayerInMap(GammaPlayer player) {
        return this.players.contains(player);
    }
    
    public final void register() {MAPS.add(this);}
    public final void deregister() {MAPS.remove(this);}
    
    public Region getRegion(int x, int z) {
        if(!isInMap(Region.translateRegionToCoord(x), Region.translateRegionToCoord(z))) return null;
        for(Region r : this.regions) {
            if(r.getX() == x && r.getZ() == z) return r;
        }
        Region region = new Region(this);
        region.setX(x);
        region.setZ(z);
        //Maybe Fire Event here
        this.regions.add(region);
        return region;
    }
    
    public Region getRegionFromLocation(Location l) {
        return getRegion(Region.translateCoordToRegion(l.getX()), Region.translateCoordToRegion(l.getZ()));
    }
    
    //Not sure if accurate..
    public final void checkRegions() {
        for(int x = Region.translateCoordToRegion(this.getLowestX()); x <= Region.translateCoordToRegion(this.getHighestX()); x++) {
            for(int z = Region.translateCoordToRegion(this.getLowestZ()); z <= Region.translateCoordToRegion(this.getHighestZ()); z++) {
                Region r = this.getRegion(x, z);
            }
        }
    }
    
    public void unload() {
        //Stop the Threads
        this.spawningThread.stopAndDeregister();
        this.threatTeleportingThread.stopAndDeregister();
        
        //Unload Listeners
        this.boundListener.deRegisterListener();
        this.serverListener.deRegisterListener();
        this.spawningListener.deRegisterListener();
        this.alteringListener.deRegisterListener();
        
        //Deregister
        this.deregister();
        
        for(Threat t : this.threats) {
            t.remove();
        }
        
        for(SpawningThreat st : this.spawningThreats) {
            st.remove();
        }
        
        //Remove Entities
        for(Entity e : this.entities) {
            e.remove();
        }
        
        for(Region r : this.regions) {
            for(Entity e : r.getEntities()) {
                e.remove();
            }
        }
        
        //May want to boot players
    }
}

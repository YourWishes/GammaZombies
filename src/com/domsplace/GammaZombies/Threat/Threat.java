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
import com.domsplace.GammaZombies.Utils.LocationUtilities;
import com.domsplace.GammaZombies.Utils.RandomUtilities;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;

/**
 *
 * @author Dominic Masters
 */
public class Threat {
    public static final double MIN_JUMP_DISTANCE = 10.0d;
    
    public enum ThreatType {
        VOMITER(EntityType.PIG, Vomiter.class, 8, 120, "Vomiter", false, false, 0.0, 0, true, 19.0),
        BRUTE(EntityType.PIG_ZOMBIE, Brute.class, 8, 120, "Brute", true, true, 16.0, 10000, false, 22.0),
        RUNNER(EntityType.WOLF, Runner.class, 40, 30, "Runner", true, false, 25.0, 7500, false, 19.0),
        SCREAMER(EntityType.SKELETON, Screamer.class, 8, 60, "Screamer", true, false, 8.0, 10000, true, 20.0),
        GRUNT(EntityType.ZOMBIE, Grunt.class, 30, 120, "Grunt", true, false, 20.0, 7500, false, 25.0)
        ;
        
        public static ThreatType getRandomThreatType() {
            ThreatType[] threats = ThreatType.values();
            List<ThreatType> cameOut = new ArrayList<ThreatType>();
            for(ThreatType ty : threats) {
                double dice = RandomUtilities.getRandomIntBetween(0, 100) + 1;
                if(dice > ty.spawningPercent) continue;
                cameOut.add(ty);
            }
            if(cameOut.size() < 1) return getRandomThreatType(); //Re-Dice
            return cameOut.get(new Random().nextInt(cameOut.size()));
        }
        
        public static ThreatType getFromEntityType(EntityType ent) {
            for(ThreatType tt : ThreatType.values()) {
                if(tt.entType.equals(ent)) return tt;
            }
            return null;
        }
        
        private final EntityType entType;
        private final Class clazz;
        private final int spawningPercent;
        private final long spawnCooldown;   //In Ticks!
        private final String name;
        private final boolean canTeleport;
        private final boolean announce;
        private final double maxJumpRadius;
        private final long teleportCooldown;
        private final boolean screams;
        private final double viewDistance;
        
        ThreatType(EntityType entityType, Class clazz, int spawningPercent, long spawnCooldown, String name,
        boolean canTeleport, boolean announceOnSpawn, double maxJumpRadius, long teleportCooldown, boolean scream, double view) {
            this.entType = entityType;
            this.clazz = clazz;
            this.spawningPercent = spawningPercent;
            this.spawnCooldown = spawnCooldown;
            this.name = name;
            this.canTeleport = canTeleport;
            this.announce = announceOnSpawn;
            this.maxJumpRadius = maxJumpRadius;
            this.teleportCooldown = teleportCooldown;
            this.screams = scream;
            this.viewDistance = view;
        }
        
        public final EntityType getEntityType() {return this.entType;}
        public final Class getClazz() {return this.clazz;}
        public final int getSpawningPercent() {return this.spawningPercent;}
        public final long getSpawnCooldown() {return this.spawnCooldown;}
        public final String getName() {return this.name;}
        public final boolean getAnnounceOnSpawn() {return this.announce;}
        public final boolean getCanTeleport() {return this.canTeleport;}
        public final boolean getDoesScream() {return this.screams;}
        public final double getMaxJumpRadius() {return this.maxJumpRadius;}
        public final double getViewDistance() {return this.viewDistance;}
        public final long getTeleportCooldown() {return this.teleportCooldown;}
        
        public SpawningThreat createThreat(final Map map) {
            Constructor c = this.clazz.getConstructors()[0];
            try {
                SpawningThreat st = (SpawningThreat) c.newInstance(this, map);
                return st;
                //Will Return Null incase the Constructor is not (ThreatType, Map)
            } catch(InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            } catch (IllegalArgumentException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            } catch(ClassCastException e) {
                return null;
            }
        }
    }
    
    private final ThreatType type;
    private Entity entity;
        
    private long lastTeleportTime;
    
    public Threat(Entity ent) {
        this(ThreatType.getFromEntityType(ent.getType()), ent);
    }
    
    public Threat(ThreatType type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }
    
    public final ThreatType getThreatType() {return this.type;}
    public Entity getEntity() {return this.entity;}
    
    public void setEntity(Entity entity) {this.entity = entity;}
    
    public void spawnBroadcast() {}
    

    public void teleportTowards(LivingEntity p) {
        if(this.entity == null) return;
        if(!this.getThreatType().getCanTeleport()) return;
        if((this instanceof SpawningThreat) && !((SpawningThreat) this).isSpawned()) return;
        if((this instanceof SpawningThreat) && (System.currentTimeMillis() - 
                ((SpawningThreat) this).getSpawnTime()) < 1000) return;    //Cooldown, helps bukkit bug
        if(this.entity.isDead()) return;
        
        long now = System.currentTimeMillis();
        if((now - this.lastTeleportTime) < this.type.getTeleportCooldown()) return;
        this.lastTeleportTime = now;
        
        //Get Direction to Teleport
        double xDiff = this.entity.getLocation().getX() - p.getLocation().getX();
        double zDiff = this.entity.getLocation().getZ() - p.getLocation().getZ();
        double x = Math.cos(Math.atan2(zDiff, xDiff));
        double z = Math.sin(Math.atan2(zDiff, xDiff));
        
        //Determine distance
        double distance = Math.sqrt(xDiff*xDiff + zDiff*zDiff);
        if(distance < Threat.MIN_JUMP_DISTANCE) return;
        if(distance > this.getThreatType().getMaxJumpRadius()) distance = this.getThreatType().getMaxJumpRadius();
        
        x = x * distance;
        z = z * distance;
        
        Location l = this.entity.getLocation().clone();
        l.setX(l.getX() - x);
        l.setZ(l.getZ() - z);
        l = LocationUtilities.getSafeLocation(l);
        LocationUtilities.playSwoosh(this.entity.getLocation());
        this.entity.teleport(l);
        LocationUtilities.playFlames(l, false);
        this.lookAt(p.getEyeLocation());
    }

    public void teleportAwayFrom(LivingEntity p) {
        if(this.entity == null) return;
        if(!this.getThreatType().getCanTeleport()) return;
        if((this instanceof SpawningThreat) && !((SpawningThreat) this).isSpawned()) return;
        if((this instanceof SpawningThreat) && (System.currentTimeMillis() - 
                ((SpawningThreat) this).getSpawnTime()) < 1000) return;    //Cooldown, helps bukkit bug
        if(this.entity.isDead()) return;
        
        long now = System.currentTimeMillis();
        if((now - this.lastTeleportTime) < this.type.getTeleportCooldown()) return;
        this.lastTeleportTime = now;
        
        //Get Direction to Teleport
        double xDiff = this.entity.getLocation().getX() - p.getLocation().getX();
        double zDiff = this.entity.getLocation().getZ() - p.getLocation().getZ();
        double x = Math.cos(Math.atan2(zDiff, xDiff));
        double z = Math.sin(Math.atan2(zDiff, xDiff));
        
        //Determine distance
        double distance = this.getThreatType().getMaxJumpRadius();
        
        x = x * distance;
        z = z * distance;
        
        Location l = this.entity.getLocation().clone();
        l.setX(l.getX() + x);
        l.setZ(l.getZ() + z);
        l = LocationUtilities.getSafeLocation(l);
        LocationUtilities.playSwoosh(this.entity.getLocation());
        this.entity.teleport(l);                                                //TODO: Why is Bukkit not sending the packets..
        LocationUtilities.playFlames(l, false);
        this.lookAt(p.getEyeLocation());
    }

    public boolean canSee(LivingEntity en) {
        if(this.entity == null) return false;
        if(en == null) return false;
        if(!(this.entity instanceof LivingEntity)) return false;
        if(this.type == null || this.type.getViewDistance() <= 0.0d) return false;
        if((this instanceof SpawningThreat) && !((SpawningThreat) this).isSpawned()) return false;
        if((this instanceof SpawningThreat) && (System.currentTimeMillis() - 
                ((SpawningThreat) this).getSpawnTime()) < 1000) return false;    //Cooldown, helps bukkit bug
        if(this.entity.isDead()) return false;
        
        //TODO: Maybe add Smarter "Through wall" checking
        
        //Get Direction to Teleport
        double xDiff = this.entity.getLocation().getX() - en.getLocation().getX();
        double zDiff = this.entity.getLocation().getZ() - en.getLocation().getZ();
        double x = Math.cos(Math.atan2(zDiff, xDiff));
        double z = Math.sin(Math.atan2(zDiff, xDiff));
        
        //Determine distance
        double distance = Math.sqrt(xDiff*xDiff + zDiff*zDiff);
        if(distance > this.type.getViewDistance()) return false;
        
        //HEAD
        LivingEntity e = (LivingEntity) this.entity;
        
        //TODO: Finish head "can see" logic
        return this.isInSeight(en);
    }
    
    //DO NOT REMOVE THE ENTITY
    public void remove() {}
    
    public void lookAt(Location l) {
        if(l == null) return;
        if(this.entity == null) return;
        
        Location changed = LocationUtilities.lookAt(this.entity.getLocation(), l);
        this.entity.teleport(changed);
    }
    
    public boolean isInSeight(Entity compare) {
        if(this.entity == null) return false;
        Location l = this.entity.getLocation();
        
        if(entity instanceof LivingEntity) {
            l = ((LivingEntity) entity).getEyeLocation();
        }
        
        double vd = this.getThreatType().getViewDistance();
        
        BlockIterator iterator = new BlockIterator(entity.getWorld(), entity
                .getLocation().toVector(), l
                .getDirection(), 0, (int)vd);
        
        int VIEW_SIZE = 3;
        
        while (iterator.hasNext()) {
            Block item = iterator.next();
            if(item == null) continue;
            if(item.getType() == null) continue;
            if(item.getType().isSolid()) return false;
            for (Entity ent : entity.getNearbyEntities(vd, vd, vd)) {
                if(ent == null) continue;
                if(!ent.equals(compare)) continue;
                for (int x = -VIEW_SIZE; x <= VIEW_SIZE; x++) {
                    for (int z = -VIEW_SIZE; z <= VIEW_SIZE; z++) {
                        for (int y = -VIEW_SIZE; y <= VIEW_SIZE; y++) {
                            Block b = ent.getLocation().getBlock().getRelative(x, y, z);
                            if(b == null) continue;
                            if(b.getType() == null) continue;
                            if(b.getType().isSolid()) continue;
                            if (b.equals(item)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public Entity getTarget() {
        if(this.entity == null) return null;
        Location l = this.entity.getLocation();
        
        if(entity instanceof LivingEntity) {
            l = ((LivingEntity) entity).getEyeLocation();
        }
        
        double vd = this.getThreatType().getViewDistance();
        
        BlockIterator iterator = new BlockIterator(entity.getWorld(), entity
                .getLocation().toVector(), l
                .getDirection(), 0, (int)vd);
        
        int VIEW_SIZE = 3;
        
        while (iterator.hasNext()) {
            Block item = iterator.next();
            if(item == null) continue;
            if(item.getType() == null) continue;
            if(item.getType().isSolid()) return null;
            for (Entity ent : entity.getNearbyEntities(vd, vd, vd)) {
                if(ent == null) continue;
                //if(!ent.equals(compare)) continue;
                for (int x = -VIEW_SIZE; x <= VIEW_SIZE; x++) {
                    for (int z = -VIEW_SIZE; z <= VIEW_SIZE; z++) {
                        for (int y = -VIEW_SIZE; y <= VIEW_SIZE; y++) {
                            Block b = ent.getLocation().getBlock().getRelative(x, y, z);
                            if(b == null) continue;
                            if(b.getType() == null) continue;
                            if(b.getType().isSolid()) continue;
                            if (b.equals(item)) {
                                return entity;
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    public void attractNearby(double radius, LivingEntity target) {
        if(target == null || this.entity == null) return;
        List<Entity> nearby = this.getEntity().getNearbyEntities(radius, radius, radius);
        for(Entity e : nearby) {
            if(e == null || !(e instanceof Creature)) continue;
            if(e.getType().equals(EntityType.PLAYER)) continue;
            if(e.equals(target) || e.equals(this.getEntity())) continue;
            Creature c = (Creature) e;
            if(c.getTarget() != null) continue;
            c.setTarget(target);
            c.damage(0, target); //Temporary Work-Around
            try {
                Threat t = new Threat(ThreatType.getFromEntityType(c.getType()), c);
                t.lookAt(target.getLocation());
            } catch(Exception ec) {}
            continue;
            /*
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(e, target, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) c.setTarget(null);*/
        }
    }
}

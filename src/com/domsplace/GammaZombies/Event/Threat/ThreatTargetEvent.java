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

package com.domsplace.GammaZombies.Event.Threat;

import com.domsplace.GammaZombies.Event.CancellableEvent;
import com.domsplace.GammaZombies.Map.Map;
import com.domsplace.GammaZombies.Threat.Threat;
import com.domsplace.GammaZombies.Threat.Threat.ThreatType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

/**
 *
 * @author Dominic Masters
 */
public class ThreatTargetEvent extends CancellableEvent {
    private final Threat threat;
    private final ThreatType type;
    private final Map map;
    private final LivingEntity ent;
    private final TargetReason reason;
    
    public ThreatTargetEvent(Threat t, ThreatType type, Map map, LivingEntity ent, TargetReason reason) {
        this.threat = t;
        this.type = type;
        this.map = map;
        this.ent = ent;
        this.reason = reason;
    }
    
    public final Map getMap() {return this.map;}
    public final Threat getThreat() {return this.threat;}
    public final ThreatType getThreatType() {return this.type;}
    public final LivingEntity getTarget() {return this.ent;}
    public final TargetReason getReason() {return this.reason;}
}

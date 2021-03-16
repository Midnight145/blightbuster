package talonos.biomescanner.map.event;

import cpw.mods.fml.common.eventhandler.Event;
import talonos.biomescanner.map.Zone;

import java.util.List;
import java.util.Map;

public class UpdateCompletionEvent extends Event {
    private Map<Zone, Float> updateZones;
    private float completion;

    public UpdateCompletionEvent(Map<Zone, Float> updateZones, float completion) {
        this.updateZones = updateZones;
        this.completion = completion;
    }

    public boolean hasZone(Zone zone) {
        return updateZones.containsKey(zone);
    }

    public float getZoneCompletion(Zone zone) {
        if (hasZone(zone))
            return updateZones.get(zone);
        else
            return 0;
    }

    public float getTotalCompletion() {
        return completion;
    }
}

package nl.mtvehicles.core.events;

import nl.mtvehicles.core.events.interfaces.HasVehicle;
import nl.mtvehicles.core.events.interfaces.IsCancellable;
import nl.mtvehicles.core.infrastructure.models.MTVEvent;

/**
 * On horn use
 */
public class HornUseEvent extends MTVEvent implements IsCancellable, HasVehicle {
    final private String licensePlate;

    public HornUseEvent(String licensePlate){
        this.licensePlate = licensePlate;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String getLicensePlate(){
        return licensePlate;
    }

}

package eu.ha3.presencefootsteps.api;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public final class FootstepTimingEvent extends Event implements ICancellableEvent {
    private final LivingEntity entity;
    private boolean stepRequested;

    public FootstepTimingEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void requestStep() {
        stepRequested = true;
    }

    public boolean isStepRequested() {
        return stepRequested;
    }
}

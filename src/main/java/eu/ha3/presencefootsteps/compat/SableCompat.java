package eu.ha3.presencefootsteps.compat;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


public final class SableCompat {

    @Nullable
    public static BlockState getSubLevelStateAt(Entity entity, BlockPos worldPos) {
        final Level level = entity.level();
        final SableCompanion companion = SableCompanion.INSTANCE;

        final Position probe = Vec3.atCenterOf(worldPos);

        return companion.runIncludingSubLevels(level, probe, true, null, (sl, projectedPos) -> {
            final BlockState state = level.getBlockState(projectedPos);
            return state.isAir() ? null : state;
        });
    }
}
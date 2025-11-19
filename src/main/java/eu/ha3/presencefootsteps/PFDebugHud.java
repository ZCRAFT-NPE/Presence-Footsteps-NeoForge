package eu.ha3.presencefootsteps;

import java.util.*;
import java.util.Map.Entry;

import eu.ha3.presencefootsteps.api.DerivedBlock;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import eu.ha3.presencefootsteps.world.PrimitiveLookup;
import eu.ha3.presencefootsteps.world.SoundsKey;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class PFDebugHud {

    private final SoundEngine engine;
    private final List<String> list = new ArrayList<>();

    public PFDebugHud(SoundEngine engine) {
        this.engine = engine;
    }

    public void render(HitResult blockHit, HitResult fluidHit, List<String> finalList) {
        if (engine == null || engine.getConfig() == null) {
            return;
        }
        Minecraft client = Minecraft.getInstance();

        list.add("");
        String version = ModList.get()
                .getModContainerById("presencefootsteps")
                .map(ModContainer::getModInfo)
                .map(info -> info.getVersion().toString())
                .orElse("unknown");
        list.add(ChatFormatting.UNDERLINE + "Presence Footsteps " + version);

        PFConfig config = engine.getConfig();
        list.add(String.format("Enabled: %s, Multiplayer: %s, Running: %s",
                config.getEnabled(), config.getEnabledMP(), engine.isRunning(client)));
        list.add(String.format("Volume: Global[G: %s%%, W: %s%%, F: %s%%]",
                config.getGlobalVolume(),
                config.wetSoundsVolume,
                config.foliageSoundsVolume));
        list.add(String.format("Entities[H: %s%%, P: %s%%], Players[U: %s%%, T: %s%% ]",
                config.hostileEntitiesVolume,
                config.passiveEntitiesVolume,
                config.clientPlayerVolume,
                config.otherPlayerVolume));
        list.add(String.format("Stepping Mode: %s, Targeting Mode: %s, Footwear: %s",
                config.getLocomotion() == Locomotion.NONE
                        ? String.format("AUTO (%sDETECTED %s%s)", ChatFormatting.BOLD,
                        Locomotion.forPlayer(client.player, Locomotion.NONE), ChatFormatting.RESET)
                        : config.getLocomotion(),
                config.getEntitySelector(),
                config.getEnabledFootwear()));
        list.add(String.format("Data Loaded: B%s P%s G%s",
                engine.getIsolator().blocks().getSubstrates().size(),
                engine.getIsolator().primitives().getSubstrates().size(),
                engine.getIsolator().golems().getSubstrates().size()));
        list.add(String.format("Has Resource Pack: %s%s",
                engine.hasData() ? ChatFormatting.GREEN : ChatFormatting.RED,
                engine.hasData()));

        insertAt(list, finalList, "Targeted Block: ", -1);

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) blockHit).getBlockPos();
            BlockState state = client.level.getBlockState(pos);

            list.add("");
            list.add(ChatFormatting.UNDERLINE + "Targeted Block Sounds Like");
            BlockState base = DerivedBlock.getBaseOf(state);
            if (!base.isAir()) {
                list.add(BuiltInRegistries.BLOCK.getKey(base.getBlock()).toString());
            }
            list.add(String.format(Locale.ENGLISH, "Primitive Key: %s",
                    PrimitiveLookup.getKey(state.getSoundType())));
            BlockPos above = pos.above();
            boolean hasRain = client.level.isRaining()
                    && client.level.getBiome(above).value().getPrecipitationAt(above) == Biome.Precipitation.RAIN;
            boolean hasLava = client.level.getBlockState(above).getFluidState().is(FluidTags.LAVA);
            boolean hasWater = client.level.isRainingAt(above)
                    || state.getFluidState().is(FluidTags.WATER)
                    || client.level.getBlockState(above).getFluidState().is(FluidTags.WATER);
            list.add("Surface Condition: " +
                    (hasLava ? ChatFormatting.RED + "LAVA"
                            : hasWater ? ChatFormatting.BLUE + "WET"
                            : hasRain ? ChatFormatting.GRAY + "SHELTERED"
                            : ChatFormatting.GRAY + "DRY"));
            renderSoundList("Step Sounds[B]",
                    engine.getIsolator().blocks().getAssociations(state), list);
            renderSoundList("Step Sounds[P]",
                    engine.getIsolator().primitives()
                            .getAssociations(state.getSoundType().getStepSound()), list);
            list.add("");

            insertAt(list, finalList, "Targeted Block: ", 1);
        }

        if (client.hitResult instanceof EntityHitResult ehr && ehr.getEntity() != null) {
            list.add(String.format("Targeted Entity Step Mode: %s",
                    engine.getIsolator().locomotions().lookup(ehr.getEntity())));
            renderSoundList("Step Sounds[G]",
                    engine.getIsolator().golems().getAssociations(ehr.getEntity().getType()), list);
            insertAt(list, finalList, "Targeted Entity", 3);
        }
    }

    private static void insertAt(List<String> values, List<String> destination,
                                 String target, int offset) {
        int i = 0;
        for (; i < destination.size(); i++) {
            if (destination.get(i).contains(target)) {
                break;
            }
        }
        destination.addAll(Mth.clamp(i + offset, 0, destination.size()), values);
        values.clear();
    }

    private void renderSoundList(String title, Map<String, SoundsKey> sounds, List<String> list) {
        if (sounds.isEmpty()) return;

        StringBuilder combined = new StringBuilder(ChatFormatting.UNDERLINE + title + ChatFormatting.RESET + ": [ ");
        boolean first = true;
        for (Entry<String, SoundsKey> e : sounds.entrySet()) {
            if (!first) combined.append(" / ");
            first = false;
            if (!e.getKey().isEmpty()) combined.append(e.getKey()).append(':');
            combined.append(e.getValue().raw());
        }
        combined.append(" ]");
        list.add(combined.toString());

        if (list.isEmpty()) {
            if (sounds.isEmpty()) {
                list.add(SoundsKey.UNASSIGNED.raw());
            } else {
                sounds.forEach((k, v) -> list.add((k.isEmpty() ? "default" : k) + ": " + v.raw()));
            }
        }
    }
}
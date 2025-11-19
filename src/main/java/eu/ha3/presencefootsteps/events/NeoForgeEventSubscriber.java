package eu.ha3.presencefootsteps.events;

import com.mojang.blaze3d.platform.InputConstants;
import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;

public final class NeoForgeEventSubscriber {

    private static final PresenceFootsteps PF = PresenceFootsteps.getInstance();

    public static void init(IEventBus modBus, Dist dist) {
        if (dist != Dist.CLIENT) return;
        modBus.addListener(NeoForgeEventSubscriber::onConstruct);
        modBus.addListener(NeoForgeEventSubscriber::onRegisterClientReloadListeners);
        modBus.addListener(NeoForgeEventSubscriber::registerKeyBinding);
        NeoForge.EVENT_BUS.addListener(NeoForgeEventSubscriber::onClientTick);
    }

    private static void onConstruct(final FMLConstructModEvent event) {
        PresenceFootsteps.logger.info("Presence Footsteps starting");
    }

    private static void onRegisterClientReloadListeners(final RegisterClientReloadListenersEvent event) {
        final Path pfFolder = FMLPaths.CONFIGDIR.get().resolve(PresenceFootsteps.MOD_ID);
        PF.config = new PFConfig(pfFolder.resolve("userconfig.json"), PF);
        PF.config.load();
        PF.engine = new SoundEngine(PF.config);
        event.registerReloadListener(PF.engine);
    }

    private static void registerKeyBinding(final RegisterKeyMappingsEvent event) {
        PF.keyBinding = Lazy.of(() ->
                new KeyMapping("key.presencefootsteps.settings",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_F10,
                        "key.categories.misc"));
        event.register(PF.keyBinding.get());
    }

    private static void onClientTick(final ClientTickEvent.Post event) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null && !mc.player.isRemoved()) {
            // if (mc.screen == null && PF.keyBinding.get().isDown()) {
            //     mc.setScreen(new PFOptionsScreen(mc.screen));
            // }
            PF.engine.onFrame(mc, mc.player);
        }
    }
}
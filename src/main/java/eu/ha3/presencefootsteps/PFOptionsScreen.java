package eu.ha3.presencefootsteps;

import eu.ha3.presencefootsteps.config.VolumeOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class PFOptionsScreen extends OptionsSubScreen {
    public PFOptionsScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("menu.pf.title"));
    }

    @Override
    protected void addOptions() {
        PFConfig config = PresenceFootsteps.getInstance().config;
        list.addBig(OptionInstance.createBoolean("menu.pf.disable_mod", config.getDisabled(), config::setDisabled));
        list.addBig(volumeOption("menu.pf.volume", config.getGlobalVolume(), 0, value -> config.setGlobalVolume(value)));
        list.addSmall(
                volumeOption(config.clientPlayerVolume, "player"),
                volumeOption(config.otherPlayerVolume, "other_players"),
                volumeOption(config.hostileEntitiesVolume, "hostile_entities"),
                volumeOption(config.passiveEntitiesVolume, "passive_entities"),
                volumeOption(config.wetSoundsVolume, "wet"),
                volumeOption(config.foliageSoundsVolume, "foliage"));
        list.addBig(volumeOption("menu.pf.volume.running", config.getRunningVolumeIncrease(), -100,
                value -> config.setRunningVolumeIncrease(value)));
    }

    private static OptionInstance<Integer> volumeOption(VolumeOption option, String name) {
        return volumeOption("menu.pf.volume." + name, option.get(), 0, value -> option.set(value));
    }

    private static OptionInstance<Integer> volumeOption(String key, int initialValue, int minimum, Consumer<Integer> onChange) {
        return new OptionInstance<>(key,
                OptionInstance.cachedConstantTooltip(Component.translatable(key + ".tooltip")),
                (caption, value) -> key.equals("menu.pf.volume") && value == 0
                        ? Component.translatable("menu.pf.volume.min")
                        : Component.translatable(key, value),
                new OptionInstance.IntRange(minimum, 100).xmap(value -> {
                    if (value > 97) {
                        return 100;
                    }
                    return minimum == 0 ? (value < 3 ? 0 : value) : (value < -97 ? -100 : value);
                }, value -> value),
                initialValue, onChange);
    }
}

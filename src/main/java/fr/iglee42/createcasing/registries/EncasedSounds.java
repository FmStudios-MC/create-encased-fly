package fr.iglee42.createcasing.registries;

import fr.iglee42.createcasing.CreateCasing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/**
 * Upstream went through Create's SoundEntry builder for its one sound; it is a plain sound event
 * here. The subtitle stays in sounds.json and the lang files.
 */
public class EncasedSounds {
    public static final SoundEvent MLDEG = register("mldeg");

    private static SoundEvent register(String name) {
        Identifier id = CreateCasing.asResource(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void playAt(SoundEvent sound, Level level, BlockPos pos, float volume, float pitch) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, volume, pitch);
    }

    public static void register() {
    }
}

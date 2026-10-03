package fr.iglee42.createcasing.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createcasing.blocks.customs.EncasedCustomCogwheelBlock;
import fr.iglee42.createcasing.blocks.shafts.EncasedCustomShaftBlock;
import fr.iglee42.createcasing.client.EncasedPartialModels;
import fr.iglee42.createcasing.transmissions.TransmissionSet;
import fr.iglee42.createcasing.transmissions.TransmissionSets;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Partial models of the mod's shafts and cogwheels, looked up by block. Upstream derived the set
 * name from the block id on every frame; the lookup is cached here.
 */
public final class TransmissionModels {
    /** Filled from the render thread and from Flywheel's worker threads, hence concurrent. */
    private static final Map<Block, String> NAMES = new ConcurrentHashMap<>();

    private TransmissionModels() {
    }

    private static String nameOf(Block block) {
        if (block instanceof EncasedCustomShaftBlock shaft)
            block = shaft.getShaft();
        else if (block instanceof EncasedCustomCogwheelBlock cog)
            block = cog.getCogwheel();
        return NAMES.computeIfAbsent(block, b -> {
            for (TransmissionSet set : TransmissionSets.getSets())
                if (set.isInSet(b))
                    return set.getName();
            return "";
        });
    }

    public static PartialModel shaft(Block block) {
        return EncasedPartialModels.SHAFT_MODELS.getOrDefault(nameOf(block), AllPartialModels.SHAFT);
    }

    public static PartialModel cog(Block block) {
        return EncasedPartialModels.COGS_MODELS.getOrDefault(nameOf(block), AllPartialModels.COGWHEEL);
    }

    public static PartialModel shaftlessCog(Block block) {
        return EncasedPartialModels.SHAFTLESS_COGS_MODELS.getOrDefault(nameOf(block), AllPartialModels.SHAFTLESS_COGWHEEL);
    }

    public static PartialModel shaftlessLargeCog(Block block) {
        return EncasedPartialModels.SHAFTLESS_LARGE_COGS_MODELS.getOrDefault(nameOf(block), AllPartialModels.SHAFTLESS_LARGE_COGWHEEL);
    }
}

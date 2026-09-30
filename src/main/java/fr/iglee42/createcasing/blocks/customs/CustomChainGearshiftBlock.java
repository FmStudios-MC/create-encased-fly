package fr.iglee42.createcasing.blocks.customs;

import com.zurrtum.create.content.kinetics.chainDrive.ChainGearshiftBlock;

/**
 * Upstream re-implemented the gearshift on top of its chain drive subclass; Create Fly's
 * {@link ChainGearshiftBlock} already is a chain drive, so the port only adds the casing type.
 */
public class CustomChainGearshiftBlock extends ChainGearshiftBlock implements CasingTyped {
    private final String type;

    public CustomChainGearshiftBlock(Properties properties, String type) {
        super(properties);
        this.type = type;
    }

    @Override
    public String getCasingType() {
        return type;
    }
}

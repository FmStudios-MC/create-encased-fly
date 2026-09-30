package fr.iglee42.createcasing.blocks.customs;

import com.zurrtum.create.content.kinetics.chainDrive.ChainDriveBlock;

public class CustomChainDriveBlock extends ChainDriveBlock implements CasingTyped {
    private final String type;

    public CustomChainDriveBlock(Properties properties, String type) {
        super(properties);
        this.type = type;
    }

    @Override
    public String getCasingType() {
        return type;
    }
}

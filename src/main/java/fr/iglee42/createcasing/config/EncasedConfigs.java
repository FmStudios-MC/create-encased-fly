package fr.iglee42.createcasing.config;

import com.zurrtum.create.catnip.config.Builder;
import fr.iglee42.createcasing.CreateCasing;

/**
 * Upstream's common config, written by Create Fly's config system to
 * {@code config/createcasing/common.json}. Registered after the blocks, since the stress section
 * lists them.
 */
public class EncasedConfigs {
    private static CCCommon common;

    public static CCCommon common() {
        return common;
    }

    public static void register() {
        common = Builder.create(CCCommon::new, CreateCasing.MODID, "common");
    }
}

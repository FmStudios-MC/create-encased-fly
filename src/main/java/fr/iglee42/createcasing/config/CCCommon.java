package fr.iglee42.createcasing.config;

import com.zurrtum.create.catnip.config.ConfigBase;

public class CCCommon extends ConfigBase {
    public final CCKinetics kinetics = nested(0, CCKinetics::new, Comments.kinetics);

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String kinetics = "Modify Create Encased blocks comportements";
    }
}

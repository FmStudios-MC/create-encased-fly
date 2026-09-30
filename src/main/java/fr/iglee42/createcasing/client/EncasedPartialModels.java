package fr.iglee42.createcasing.client;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.catnip.lang.Lang;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour.AttachmentTypes.ComponentPartials;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Partial models. The per-set ones (mixer heads, drill heads, belt covers, ...) are handed to
 * {@link CasingSetVisuals} and {@link FluidSetVisuals}; the shaft and cogwheel maps are keyed by
 * transmission set name, as upstream.
 * <p>
 * Upstream's chain conveyor "wheel" partial is gone: Create Fly draws the bull wheel as part of
 * the conveyor's shaft model.
 */
public class EncasedPartialModels {
    public static final PartialModel GLASS_SHAFT = block("shaft/glass");
    public static final PartialModel MLDEG_SHAFT = block("shaft/mldeg");

    public static final Map<String, PartialModel> SHAFT_MODELS = new HashMap<>();
    public static final Map<String, PartialModel> COGS_MODELS = new HashMap<>();
    public static final Map<String, PartialModel> SHAFTLESS_COGS_MODELS = new HashMap<>();
    public static final Map<String, PartialModel> LARGE_COGS_MODELS = new HashMap<>();
    public static final Map<String, PartialModel> SHAFTLESS_LARGE_COGS_MODELS = new HashMap<>();
    /** Per fluid set: attachment partials by kind and face, and the pipe casing. */
    public static final Map<String, Map<ComponentPartials, Map<Direction, PartialModel>>> PIPE_ATTACHMENTS = new HashMap<>();
    public static final Map<String, PartialModel> PIPE_CASINGS = new HashMap<>();

    static {
        for (String w : new String[]{"oak", "birch", "acacia", "jungle", "warped", "dark_oak", "crimson", "mangrove", "cherry", "bamboo", "brass", "copper", "zinc", "andesite"}) {
            if (!w.equals("andesite"))
                SHAFT_MODELS.put(w, block("shaft/" + w));
            COGS_MODELS.put(w, block("cogwheel/" + w));
            SHAFTLESS_COGS_MODELS.put(w, block("cogwheel_shaftless/" + w));
            LARGE_COGS_MODELS.put(w, block("large_cogwheel/" + w));
            SHAFTLESS_LARGE_COGS_MODELS.put(w, block("large_cogwheel_shaftless/" + w));
        }
        SHAFT_MODELS.put("spruce", block("shaft/spruce"));
        SHAFT_MODELS.put("mldeg", MLDEG_SHAFT);
        SHAFT_MODELS.put("glass", GLASS_SHAFT);

        for (FluidSet set : FluidSets.getSets()) {
            if (!set.generates(FluidSet.Part.FLUID_PIPE))
                continue;
            Map<ComponentPartials, Map<Direction, PartialModel>> partialMap = new EnumMap<>(ComponentPartials.class);
            for (ComponentPartials type : ComponentPartials.values()) {
                Map<Direction, PartialModel> map = new EnumMap<>(Direction.class);
                for (Direction d : Iterate.directions)
                    map.put(d, block("fluid_pipe/" + set.getName() + "/" + Lang.asId(type.name()) + "/" + Lang.asId(d.getSerializedName())));
                partialMap.put(type, map);
            }
            PIPE_ATTACHMENTS.put(set.getName(), partialMap);
            PIPE_CASINGS.put(set.getName(), block("fluid_pipe/" + set.getName() + "/casing"));
        }
    }

    public static PartialModel block(String path) {
        return PartialModel.of(CreateCasing.asResource("block/" + path));
    }

    /** Partial models must exist before models bake; called from the client entrypoint. */
    public static void init() {
    }
}

package fr.iglee42.createcasing.mixins;

import com.mojang.serialization.Codec;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Adds a belt casing type for every casing set that makes belt casings, as upstream did. Create
 * Fly saves the casing through a codec built from the enum's values in the same static
 * initialiser, so the codec is rebuilt once the new values exist.
 */
@Mixin(BeltBlockEntity.CasingType.class)
public class BeltCasingTypeMixin {
    @Shadow
    @Final
    @Mutable
    private static BeltBlockEntity.CasingType[] $VALUES;

    @Shadow
    @Final
    @Mutable
    public static Codec<BeltBlockEntity.CasingType> CODEC;

    @Invoker("<init>")
    public static BeltBlockEntity.CasingType createcasing$create(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void createcasing$addCasings(CallbackInfo ci) {
        List<BeltBlockEntity.CasingType> values = new ArrayList<>(Arrays.asList($VALUES));
        for (CasingSet set : CasingSets.getSets()) {
            if (!set.doesGenerateBelt())
                continue;
            BeltBlockEntity.CasingType type = createcasing$create(set.getName().toUpperCase(Locale.ROOT), values.size());
            values.add(type);
            set.setBeltCasingType(type);
        }
        $VALUES = values.toArray(new BeltBlockEntity.CasingType[0]);
        CODEC = StringRepresentable.fromEnum(BeltBlockEntity.CasingType::values);
    }
}

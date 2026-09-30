package fr.iglee42.createcasing.client.compat.jei;

import com.zurrtum.create.client.compat.jei.JeiClientPlugin;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.casings.CasingSet;
import fr.iglee42.createcasing.casings.CasingSet.Part;
import fr.iglee42.createcasing.casings.CasingSets;
import fr.iglee42.createcasing.fluids.FluidSet;
import fr.iglee42.createcasing.fluids.FluidSets;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import fr.iglee42.createcasing.registries.EncasedItems;
import net.minecraft.world.item.ItemStack;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

/**
 * The mod's machines as catalysts of Create's recipe categories, as upstream added them through a
 * mixin into Create's JEI plugin. Found through the {@code jei_mod_plugin} entrypoint.
 */
public class EncasedJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return CreateCasing.asResource("jei");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemLike[] mixers = casing(Part.MIXER), presses = casing(Part.PRESS), deployers = casing(Part.DEPLOYER),
            saws = casing(Part.SAW), fans = casing(Part.ENCASED_FAN);
        for (IRecipeType<?> type : new IRecipeType<?>[]{JeiClientPlugin.MIXING, JeiClientPlugin.AUTOMATIC_SHAPELESS, JeiClientPlugin.AUTOMATIC_BREWING})
            registration.addCraftingStation(type, mixers);
        for (IRecipeType<?> type : new IRecipeType<?>[]{JeiClientPlugin.PRESSING, JeiClientPlugin.PACKING, JeiClientPlugin.AUTOMATIC_PACKING})
            registration.addCraftingStation(type, presses);
        registration.addCraftingStation(JeiClientPlugin.DEPLOYING, deployers);
        registration.addCraftingStation(JeiClientPlugin.SAWING, saws);
        registration.addCraftingStation(JeiClientPlugin.BLOCK_CUTTING, saws);
        for (IRecipeType<?> type : new IRecipeType<?>[]{JeiClientPlugin.FAN_BLASTING, JeiClientPlugin.FAN_HAUNTING, JeiClientPlugin.FAN_SMOKING, JeiClientPlugin.FAN_WASHING})
            registration.addCraftingStation(type, fans);
        registration.addCraftingStation(JeiClientPlugin.DRAINING, fluid(FluidSet.Part.ITEM_DRAIN));
        registration.addCraftingStation(JeiClientPlugin.SPOUT_FILLING, fluid(FluidSet.Part.SPOUT));
    }

    /** Encased shaft and cogwheel items only come from encasing, as upstream's JEI plugin hid them. */
    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
            EncasedItems.HIDDEN.stream().map(ItemStack::new).toList());
    }

    private static ItemLike[] casing(Part part) {
        List<ItemLike> list = new ArrayList<>();
        for (CasingSet set : CasingSets.getSets())
            if (set.generates(part))
                list.add(set.get(part));
        return list.toArray(ItemLike[]::new);
    }

    private static ItemLike[] fluid(FluidSet.Part part) {
        List<ItemLike> list = new ArrayList<>();
        for (FluidSet set : FluidSets.getSets())
            if (set.generates(part))
                list.add(set.get(part));
        return list.toArray(ItemLike[]::new);
    }
}

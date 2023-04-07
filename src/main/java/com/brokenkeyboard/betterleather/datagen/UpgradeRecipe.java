package com.brokenkeyboard.betterleather.datagen;

import com.brokenkeyboard.betterleather.BetterLeather;
import com.brokenkeyboard.betterleather.Config;
import com.brokenkeyboard.betterleather.item.LeatherArmor;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

public class UpgradeRecipe extends net.minecraft.world.item.crafting.UpgradeRecipe {
    final Ingredient input;
    final Ingredient ingredient;
    final ItemStack result;

    public UpgradeRecipe(ResourceLocation location, Ingredient input, Ingredient ingredient, ItemStack result) {
        super(location, input, ingredient, result);
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(Container container, Level level) {
        boolean result = super.matches(container, level);
        ItemStack stack = container.getItem(0);
        boolean kitMax = !(container.getItem(1).isDamaged());
        boolean notMax = LeatherArmor.getBonusArmor(stack) < (int)(stack.getMaxDamage() * (Config.KIT_BASE.get() + Config.KIT_BONUS.get()));
        boolean damaged = (BetterLeather.kitRepair == 0 || stack.isDamaged());
        return result && kitMax && (notMax || damaged);
    }

    @NotNull
    @Override
    public ItemStack assemble(Container container) {
        ItemStack result = super.assemble(container);
        int amount = (int)(result.getMaxDamage() * (Config.KIT_BASE.get() + Config.KIT_BONUS.get()));
        int repairCount = (int)(result.getMaxDamage() * Config.KIT_REPAIR.get());
        LeatherArmor.setBonusArmor(result, amount);
        LeatherArmor.setBonusArmorMax(result, amount);
        result.setDamageValue(result.getDamageValue() - repairCount);
        return result;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<UpgradeRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public UpgradeRecipe fromJson(ResourceLocation location, JsonObject object) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(object, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getAsJsonObject(object, "addition"));
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(object, "result"));
            return new UpgradeRecipe(location, ingredient, ingredient1, itemstack);
        }

        @Override
        public UpgradeRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf object) {
            Ingredient ingredient = Ingredient.fromNetwork(object);
            Ingredient ingredient1 = Ingredient.fromNetwork(object);
            ItemStack itemstack = object.readItem();
            return new UpgradeRecipe(location, ingredient, ingredient1, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf location, UpgradeRecipe recipe) {
            recipe.input.toNetwork(location);
            recipe.ingredient.toNetwork(location);
            location.writeItem(recipe.result);
        }
    }
}
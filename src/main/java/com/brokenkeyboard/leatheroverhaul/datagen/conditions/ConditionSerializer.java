package com.brokenkeyboard.leatheroverhaul.datagen.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.function.Supplier;

public record ConditionSerializer<T extends ICondition>(ResourceLocation id, Supplier<T> supplier) implements IConditionSerializer<T> {

    @Override
    public void write(JsonObject json, T value) {
    }

    @Override
    public T read(JsonObject json) {
        return supplier.get();
    }

    @Override
    public ResourceLocation getID() {
        return id;
    }
}

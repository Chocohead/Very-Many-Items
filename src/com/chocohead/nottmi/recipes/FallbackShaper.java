package com.chocohead.nottmi.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

import com.chocohead.nottmi.NotTMILog;

public enum FallbackShaper implements IRecipeShaper {
	INSTANCE;

	@Override
	public Ingredient[] shape(IRecipe recipe) {
		NotTMILog.warn("Unknown recipe type: " + recipe);
		return recipe.getIngredients().toArray(new Ingredient[0]);
	}
}
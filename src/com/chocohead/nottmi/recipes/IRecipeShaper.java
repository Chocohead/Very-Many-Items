package com.chocohead.nottmi.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

public interface IRecipeShaper {
	/** Convert the given recipe into an up to 3x3 grid of ingredients */
	Ingredient[] shape(IRecipe recipe);

	/** The size of the output stack */
	default int amount(IRecipe recipe) {
		return recipe.getRecipeOutput().getCount();
	}
}
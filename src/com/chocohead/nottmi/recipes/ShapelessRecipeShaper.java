package com.chocohead.nottmi.recipes;

import java.util.List;

import com.google.common.collect.Iterables;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

public class ShapelessRecipeShaper implements IRecipeShaper {
	@Override
	public Ingredient[] shape(IRecipe recipe) {
		List<Ingredient> ingredients = recipe.getIngredients();
		int size = ingredients.size();

		if (size > 4) {
			return ingredients.toArray(new Ingredient[0]);
		} else if (size > 1) {
			Ingredient[] out = new Ingredient[size > 2 ? size + 1 : size];

			for (int i = 0, head = 0; i < size; i++) {
				if (i == 2) {
					out[head++] = Ingredient.EMPTY;
				}
				out[head++] = ingredients.get(i);
			}

			return out;
		} else {
			Ingredient[] out = new Ingredient[5];

			System.arraycopy(ShapedRecipeShaper.EMPTY_4, 0, out, 0, 4);
			out[4] = Iterables.getOnlyElement(ingredients);

			return out;
		}
	}
}
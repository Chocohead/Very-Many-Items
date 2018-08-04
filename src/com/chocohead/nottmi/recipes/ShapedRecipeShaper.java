package com.chocohead.nottmi.recipes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;

public class ShapedRecipeShaper implements IRecipeShaper {
	static final Ingredient[] EMPTY_4 = Stream.generate(() -> Ingredient.EMPTY).limit(4).toArray(Ingredient[]::new);

	@Override
	public Ingredient[] shape(IRecipe recipe) {
		List<Ingredient> ingredients = recipe.getIngredients();
		ShapedRecipe shaped = (ShapedRecipe) recipe;

		switch (shaped.getHeight() << 2 | shaped.getWidth()) {
		case 5: {//1x1
			Ingredient[] out = new Ingredient[5];

			System.arraycopy(EMPTY_4, 0, out, 0, 4);
			out[4] = Iterables.getOnlyElement(ingredients);

			return out;
		}

		case 9: //1x2
		case 13: {//1x3
			Ingredient[] out = new Ingredient[ingredients.size() == 2 ? 5 : 8];
			Arrays.fill(out, Ingredient.EMPTY);
			
			int head = 1 - 3;
			for (Ingredient ingredient : ingredients) {
				out[head += 3] = ingredient;
			}

			return out;
		}

		case 6: //2x1
		case 7: //3x1
		case 11: //3x2
		case 15: //3x3
			return ingredients.toArray(new Ingredient[0]);

		case 10: //2x2
		case 14: {//2x3
			int size = ingredients.size();
			Ingredient[] out = new Ingredient[size == 2 ? 5 : 8];

			for (int i = 0, head = 0; i < size; i++) {
				if (i % 2 == 0 && i > 0) {
					out[head++] = Ingredient.EMPTY;
				}
				out[head++] = ingredients.get(i);
			}

			return out;
		}

		default:
			throw new IllegalStateException("Unknown recipe size: " + shaped.getWidth() + ", " + shaped.getHeight());
		}
	}
}
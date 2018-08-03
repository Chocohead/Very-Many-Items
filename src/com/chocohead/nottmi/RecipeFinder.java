package com.chocohead.nottmi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;

public class RecipeFinder {
	public static class RecipeResult {
		private final Ingredient[] ingredients;
		private final IRecipe recipe;

		public RecipeResult(IRecipe recipe) {
			this.recipe = recipe;
			ingredients = recipe.getIngredients().toArray(new Ingredient[0]);
		}

		public List<Ingredient> getIngredients() {
			if (recipe instanceof ShapedRecipe) {
				ShapedRecipe shaped = (ShapedRecipe) recipe;

				switch (shaped.getHeight() << 2 | shaped.getWidth()) {
				case 5: //1x1
					return Stream.concat(Stream.generate(() -> Ingredient.EMPTY).limit(4), Arrays.stream(ingredients)).collect(Collectors.toList());

				case 9: //1x2
				case 13: {//1x3
					List<Ingredient> out = new ArrayList<>(8);
					out.add(Ingredient.EMPTY);

					for (Ingredient ingredient : ingredients) {
						out.add(ingredient);
						out.add(Ingredient.EMPTY);
						out.add(Ingredient.EMPTY);
					}

					return out;
				}

				case 6: //2x1
				case 7: //3x1
				case 11: //3x2
				case 15: //3x3
					return Arrays.asList(ingredients);

				case 10: //2x2
				case 14: {//2x3
					List<Ingredient> out = new ArrayList<>(8);

					for (int i = 0; i < ingredients.length; i++) {
						if (i % 2 == 0 && i > 0) {
							out.add(Ingredient.EMPTY);
						}
						out.add(ingredients[i]);
					}

					return out;
				}

				default:
					throw new IllegalStateException("Unknown recipe size: " + shaped.getWidth() + ", " + shaped.getHeight());
				}			
			} else if (recipe instanceof ShapelessRecipe) {
				int size = ingredients.length;

				if (size > 4) {
					return Arrays.asList(ingredients);
				} else if (size > 1) {
					List<Ingredient> out = new ArrayList<>(size + 1);

					for (int i = 0; i < ingredients.length; i++) {
						if (i == 2) {
							out.add(Ingredient.EMPTY);
						}
						out.add(ingredients[i]);
					}

					return out;
				} else {
					return Stream.concat(Stream.generate(() -> Ingredient.EMPTY).limit(4), Arrays.stream(ingredients)).collect(Collectors.toList());
				}
			} else {
				System.out.println("Unknown recipe type: " + recipe);
				return Arrays.asList(ingredients);
			}
		}

		public int amount() {
			return recipe.getRecipeOutput().getCount();
		}

		@Override
		public String toString() {
			return "Recipe<" + recipe + ": " + Arrays.toString(ingredients) + '>';
		}
	}

	private static boolean isValidStack(ItemStack stack) {
		return stack != null && !stack.isEmpty();
	}
	
	private static boolean matches(ItemStack stackA, ItemStack stackB) {
		if (stackA == stackB) return true;

		if (!isValidStack(stackA)) return !isValidStack(stackB);
		if (!isValidStack(stackB)) return false;

		return stackA.getItem() == stackB.getItem() && Objects.equals(stackA.getTagCompound(), stackB.getTagCompound());
	}
	
	public static List<IRecipe> findRecipes(ItemStack target) {
		if (!isValidStack(target)) throw new IllegalArgumentException("Invalid stack: " + target);
		List<IRecipe> recipes = new ArrayList<>();

		for (IRecipe recipe : Minecraft.getMinecraft().world.func_199532_z().func_199510_b()) {
			if (matches(recipe.getRecipeOutput(), target)) {
				recipes.add(recipe);
			}
		}

		return recipes;
	}

	public static List<RecipeResult> findIngredients(ItemStack stack) {
		return findRecipes(stack).stream().map(RecipeResult::new).collect(Collectors.toList());
	}
}
package com.chocohead.nottmi.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;

public class RecipeFinder {
	public static class RecipeResult {
		public static final Map<Class<? extends IRecipe>, IRecipeShaper> SHAPERS = new IdentityHashMap<>();
		static {
			SHAPERS.put(ShapedRecipe.class, new ShapedRecipeShaper());
			SHAPERS.put(ShapelessRecipe.class, new ShapelessRecipeShaper());
		}

		private final Ingredient[] ingredients;
		private final int outputSize;
		private final IRecipe recipe;

		public RecipeResult(IRecipe recipe) {
			this.recipe = recipe;

			IRecipeShaper shaper = SHAPERS.getOrDefault(recipe.getClass(), FallbackShaper.INSTANCE);
			ingredients = shaper.shape(recipe);
			outputSize = shaper.amount(recipe);
		}

		public List<Ingredient> getIngredients() {
			return Arrays.asList(ingredients);
		}

		public int amount() {
			return outputSize;
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
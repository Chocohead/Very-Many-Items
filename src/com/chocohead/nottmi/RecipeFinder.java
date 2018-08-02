package com.chocohead.nottmi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

public class RecipeFinder {
	public static boolean isValidStack(ItemStack stack) {
		return stack != null && !stack.isEmpty();
	}
	
	public static boolean matches(ItemStack stackA, ItemStack stackB) {
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

	public static List<List<Ingredient>> findIngredients(ItemStack stack) {
		return findRecipes(stack).stream().map(IRecipe::getIngredients).collect(Collectors.toList());
	}

	public static List<List<ItemStack[]>> viewIngredients(ItemStack target) {
		List<List<ItemStack[]>> view = new ArrayList<>();

		for (IRecipe recipe : findRecipes(target)) {
			view.add(recipe.getIngredients().stream().map(Ingredient::getMatchingStacks).collect(Collectors.toList()));
		}

		return view;
	}
}
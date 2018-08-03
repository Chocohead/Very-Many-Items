package com.chocohead.nottmi;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.chocohead.nottmi.RecipeFinder.RecipeResult;

public class RecipeInventory implements IInventory {
	private interface StackDisplay {
		ItemStack getStack();
	}
	private static final StackDisplay EMPTY_DISPLAY = () -> ItemStack.EMPTY;
	private class DisplayIngredient implements StackDisplay {
		private final Ingredient ingredient;

		public DisplayIngredient(Ingredient ingredient) {
			this.ingredient = ingredient;
		}

		public ItemStack getStack() {
			//It's GhostRecipe's GhostIngredient logic, because it's pretty swell
			ItemStack[] stacks = ingredient.getMatchingStacks();
            return stacks[MathHelper.floor(time / 30F) % stacks.length];
		}
	}
	private final StackDisplay[] ingredients = new StackDisplay[9];
	private final List<RecipeResult> recipes;
	private final ItemStack output;
	private int index = 0;
	float time;

	public RecipeInventory(ItemStack target) {
		output = target.copy();

		recipes = RecipeFinder.findIngredients(target);
		if (!recipes.isEmpty()) updateRecipe();		
	}
	
	private void updateRecipe() {
		RecipeResult recipe = recipes.get(index);
		System.out.println("Switching to recipe: " + recipe);
		List<Ingredient> ingredients = recipe.getIngredients();

		for (int y = 0, limit = ingredients.size(); y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (x + y * 3 < limit) {
					int index = x + y * 3;
					Ingredient ingredient = ingredients.get(index);

					if (!ingredient.func_203189_d()) {
						this.ingredients[index] = new DisplayIngredient(ingredient);
					} else {
						this.ingredients[index] = EMPTY_DISPLAY;
					}
				} else {
					Arrays.fill(this.ingredients, limit, 9, EMPTY_DISPLAY);
					break;
				}
			}
		}
		output.setCount(recipe.amount());
	}

	public void next() {
		index = Math.floorMod(index + 1, recipes.size());
		updateRecipe();
	}

	public void previous() {
		index = Math.floorMod(index - 1, recipes.size());
		updateRecipe();
	}

	public void tick(float partialTicks) {
		if (!GuiScreen.isCtrlKeyDown()) {
			time += partialTicks;
		}
	}

	/** getName */
	@Override
	public ITextComponent func_200200_C_() {
		return new TextComponentString("Recipes");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	/** getCustomName */
	@Override
	public ITextComponent func_200201_e() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 10;
	}

	@Override
	public boolean isEmpty() {
		return Arrays.stream(ingredients).allMatch(Objects::isNull);
	}

	@Override
	public void clear() {
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return index >= 9 ? output : ingredients[index].getStack(); 
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}
	
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}
}
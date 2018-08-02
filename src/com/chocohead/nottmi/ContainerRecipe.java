package com.chocohead.nottmi;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRecipe extends Container {
	public static class RecipeSlot extends Slot {
		public RecipeSlot(IInventory inventory, int index, int xPosition, int yPosition) {
			super(inventory, index, xPosition, yPosition);
		}

		@Override
		public boolean isEnabled() {
			return getHasStack();
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}

		@Override
		public boolean canTakeStack(EntityPlayer player) {
			return false;
		}
	}
	public ContainerRecipe(RecipeInventory recipe, int xOffset, int yOffset) {
		addSlotToContainer(new Slot(recipe, 9, xOffset + 94, yOffset + 18));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlotToContainer(new Slot(recipe, x + y * 3, xOffset + x * 18, yOffset + y * 18));
			}
		}
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		if (clickType == ClickType.PICKUP) {
			Slot slot = inventorySlots.get(slotId);

			if (slot.getHasStack()) {
				NotTMI.tryOpenRecipes(slot.getStack());
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
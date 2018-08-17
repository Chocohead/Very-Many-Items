package com.chocohead.nottmi;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.chocohead.nottmi.bits.Button;
import com.chocohead.nottmi.bits.ItemList;

public enum NotTMI {
	INSTANCE;

	private GuiContainer container;
	private NotTMIOverlay overlay;
	private Button prevButton, nextButton;
	private GuiRecipeBook book;

	public void bind(GuiScreen screen) {
		if (screen instanceof GuiContainer) {
			NotTMILog.info("Binding " + screen);
			this.container = (GuiContainer) screen;
			overlay = new NotTMIOverlay();

			prevButton = new Button(() -> "Previous (" + (ItemList.page + 1) + '/' + ItemList.INSTANCE.numPages + ')');
			nextButton = new Button("Next");

			if (screen instanceof IRecipeShownListener) {
				book = ((IRecipeShownListener) screen).func_194310_f();
			} else {
				book = null;
			}
		}
	}

	public void setSize(int xSize, int ySize, int guiLeft, int guiTop) {
		NotTMILog.info("Corner of " + container + " with size [" + xSize + ", " + ySize + "] set to [" + guiLeft + ", " + guiTop + ']');
		overlay.resize(guiLeft, guiTop);
		int left = guiLeft;
		int freeWidth = container.width - (left + xSize);
		System.out.print("Normal: " + freeWidth);
		left = 177 + (container.width - xSize - 200) / 2;
		freeWidth = container.width - (left + xSize);
		//System.out.println("Normal: " + guiLeft + ", Open: " + (177 + (container.width - xSize - 200) / 2));
		System.out.println(", Open: " + freeWidth);
		/*boolean open = book.isVisible();
		int openOffset = open ? 177 + (container.width - xSize - 200) / 2 : guiLeft;*/
		ItemList.INSTANCE.resize(container.width, container.height, xSize, ySize, guiLeft, guiTop);

		prevButton.x = guiLeft + xSize;
		prevButton.y = 0;
		prevButton.width = guiLeft;
		prevButton.height = 28;

		nextButton.x = guiLeft + xSize;
		nextButton.y = container.height - 28;
		nextButton.width = guiLeft;
		nextButton.height = 28;

		@SuppressWarnings("unchecked") //Mojang probably deliberately choose the signature they did to prevent adding
		List<IGuiEventListener> awkwardGenerics = (List<IGuiEventListener>) container.func_195074_b();
		awkwardGenerics.add(0, new IGuiEventListener() {
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				NotTMILog.info("Clicked " + button + " at " + mouseX + ", " + mouseY);
				if (ItemList.INSTANCE.hoverItem != null) {
					switch (button) {
					case GLFW.GLFW_MOUSE_BUTTON_LEFT:
						Minecraft.getMinecraft().player.sendChatMessage("/give @s " + Item.REGISTRY.getNameForObject(ItemList.INSTANCE.hoverItem.getItem()) + " 64");
						return true;

					case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
						Minecraft.getMinecraft().player.sendChatMessage("/give @s " + Item.REGISTRY.getNameForObject(ItemList.INSTANCE.hoverItem.getItem()));
						return true;

					default:
						return false;
					}
				} else if (prevButton.contains((int) mouseX, (int) mouseY)) {
					ItemList.page = Math.floorMod(ItemList.page - 1, ItemList.INSTANCE.numPages);
					prevButton.onClick();

					return true;
				} else if (nextButton.contains((int) mouseX, (int) mouseY)) {
					ItemList.page = Math.floorMod(ItemList.page + 1, ItemList.INSTANCE.numPages);
					nextButton.onClick();

					return true;
				}
				return false;
			}
		});
	}

	public void onDraw(int mouseX, int mouseY) {
		//NotTMILog.info("Drawing in " + container + " with mouse at " + mouseX + ", " + mouseY);

		if (book != null && book.isVisible()) {
			
		}

		prevButton.draw(overlay, mouseX, mouseY);
		nextButton.draw(overlay, mouseX, mouseY);
		ItemList.INSTANCE.draw(overlay, mouseX, mouseY);
	}

	public boolean onKey(int key, int scanCode, int modifiers) {
		//Naming from https://github.com/LWJGL/lwjgl3-wiki/wiki/2.6.3-Input-handling-with-GLFW
		/*NotTMILog.info("Key entered: " + key + " (" + InputMappings.Type.KEYSYM.func_197944_a(key).func_197936_a() + ") " +
				scanCode + " (" + InputMappings.Type.SCANCODE.func_197944_a(scanCode).func_197936_a() + ") " + modifiers);*/
		NotTMILog.info("Key pressed: " + InputMappings.func_197954_a(key, scanCode).func_197936_a());

		if (GLFW.GLFW_KEY_R == key && ItemList.INSTANCE.hoverItem != null) {
			tryOpenRecipes(ItemList.INSTANCE.hoverItem);
		}
		return false;
	}

	public void release(GuiContainer container) {
		if (this.container == container) {
			NotTMILog.info("Releasing " + container);
			container = null;
		}
	}

	public static void tryOpenRecipes(ItemStack target) {
		RecipeInventory recipes = new RecipeInventory(target);

		if (!recipes.isEmpty()) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiRecipe(recipes));				
		}
	}
}
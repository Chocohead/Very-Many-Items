package com.chocohead.nottmi;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiRecipe extends GuiContainer {
	private static final ResourceLocation CRAFTING_GUI = new ResourceLocation("textures/gui/container/crafting_table.png");
	private static final Deque<GuiScreen> STACK = new ArrayDeque<>();
	private interface SlotSupplier {
		Slot get(GuiContainer gui, double mouseX, double mouseY);
	}
	private static final SlotSupplier hoveredSlot;
	static {
		Method method = null;

		String target = Util.inDev() ? "func_195360_a" : "a";
		for (Method m : GuiContainer.class.getDeclaredMethods()) {
			if (target.equals(m.getName()) && m.getReturnType() == Slot.class) {
				m.setAccessible(true);
				method = m;
				break;
			}
		}

		if (method == null) throw new IllegalStateException("Unable to find " + target);
		try {
			MethodHandle handle = MethodHandles.lookup().unreflect(method);
			hoveredSlot = (gui, mouseX, mouseY) -> {
				try {
					return (Slot) handle.invokeExact(gui, mouseX, mouseY);
				} catch (Throwable t) {
					throw new RuntimeException("Error getting currently hovered slot in " + gui, t);
				}
			};
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Error making handle for hovered slot method", e);
		}
	}
	private final RecipeInventory recipe;

	public GuiRecipe(RecipeInventory recipe) {
		super(new ContainerRecipe(recipe, 5, 6));

		xSize = 124;
		ySize = 63;

		assert !recipe.isEmpty();
		this.recipe = recipe;

		STACK.add(Minecraft.getMinecraft().currentScreen);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(CRAFTING_GUI);

		int j = width - xSize >> 1; //151
		int k = height - ySize >> 1; //88

		drawTexturedModalRect(j, k, 0, 0, 120, 4);
		drawTexturedModalRect(j + 120, k, 172, 0, 4, 59);
		drawTexturedModalRect(j, k + 4, 0, 107, 4, 59);
		drawTexturedModalRect(j + 4, k + 59, 56, 162, 120, 4);
		drawTexturedModalRect(j + 4, k + 4, 29, 15, 116, 55);
		
		recipe.tick(partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCenteredString(fontRenderer, "Test", xSize - guiLeft / 2, -6, 4210752);
		fontRenderer.func_211126_b(I18n.format("container.crafting"), 28F, 6F, 4210752);	
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		//TODO: Check if not over TMI screen
		switch (button) {
		case 0:
			Slot slot = hoveredSlot.get(this, mouseX, mouseY);
			if (slot != null && slot.isEnabled()) {
				assert slot.getHasStack();
				NotTMI.tryOpenRecipes(slot.getStack());
			} else {
				recipe.next();
			}
			return true;

		case 1:
			recipe.previous();
			return true;

		default:
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		switch(key) {
		case GLFW.GLFW_KEY_RIGHT:
			recipe.next();
			return true;

		case GLFW.GLFW_KEY_LEFT:
			recipe.previous();
			return true;

		case GLFW.GLFW_KEY_BACKSPACE:
			mc.displayGuiScreen(STACK.removeLast());
			return true;

		case GLFW.GLFW_KEY_ESCAPE:
			mc.player.closeScreen();
			return true;

		default:
			if (mc.gameSettings.keyBindInventory.func_197976_a(key, scanCode)) {
				mc.displayGuiScreen(STACK.removeFirst());
				STACK.clear();
				return true;
			} else {
				return super.keyPressed(key, scanCode, modifiers);
			}
		}
	}
}
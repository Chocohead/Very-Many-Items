package com.chocohead.nottmi;

import java.util.List;
import java.util.OptionalInt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

public class NotTMIOverlay {
	private final ItemRenderer renderer = Minecraft.getMinecraft().getRenderItem();
	private final FontRenderer font = Minecraft.getMinecraft().fontRenderer;
	private int left, top;

	public void resize(int left, int top) {
		this.left = left;
		this.top = top;
	}

	public void drawItem(ItemStack stack, int x, int y) {
		//hardSetFlatMode(false);

		renderer.renderItemIntoGUI(stack, x - left, y - top);
		/*Gui.drawRect(x, y, x + 50, y + 50, ~0x000000);
		Gui.drawRect(0, 0, 400, 200, ~0x000000);*/
		//renderer.renderItemIntoGUI(this.window.fontRenderer, this.window.mc.p, paramItemStack, paramInt1 - this.windowX, paramInt2 - this.windowY);
	}

	public void drawRect(int x, int y, int width, int height, int colour) {
		Gui.drawRect(x - left, y - top, x + width - left, y + height - top, colour);
	}

	public void drawText(String text, int x, int y, int colour) {
		font.drawStringWithShadow(text, x - left, y - top, colour);
	}

	public void drawTextCentered(String text, int x, int y, int width, int height, int colour) {
		drawText(text, x + (width - getTextWidth(text)) / 2, y + (height - 8) / 2, colour);
	}

	public int getTextWidth(String paramString) {
		return font.getStringWidth(paramString);
	}

	public void drawTooltip(ItemStack stack, int x, int y) {
		List<String> lines = Minecraft.getMinecraft().currentScreen.getItemToolTip(stack);

		OptionalInt maybeMax = lines.stream().mapToInt(font::getStringWidth).max();
		if (maybeMax.isPresent()) {
			int max = maybeMax.getAsInt();

			if (x + max + 12 > Minecraft.getMinecraft().currentScreen.width) {
				x -= 16 + max;
			}

			int tooltipHeight = lines.size() > 1 ? lines.size() * 10 : 8;
			int height = Minecraft.getMinecraft().currentScreen.height;
			if (y + tooltipHeight > height) {
				y = height - tooltipHeight;
			}

			Minecraft.getMinecraft().currentScreen.drawHoveringText(lines, x - left, y - top);
		}

		/*//GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int i = 3;
		int j = paramInt1 + 12;
		int k = paramInt2 - 15;
		if (k < 0) {
			k = 0;
		}
		int m = getTextWidth(paramString) + i * 2;
		int width = Minecraft.getMinecraft().currentScreen.width;
		if (j + m > width) {
			j -= j + m - width;
		}
		drawRect(j, k, m, 8 + i * 2, -301989888);
		drawText(j + i, k + i, paramString, -1);*/
	}
}
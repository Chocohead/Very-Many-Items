package com.chocohead.nottmi;

import java.util.List;
import java.util.OptionalInt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
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
		renderer.renderItemIntoGUI(stack, x - left, y - top);
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
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		List<String> lines = screen.getItemToolTip(stack);

		OptionalInt maybeMax = lines.stream().mapToInt(font::getStringWidth).max();
		if (maybeMax.isPresent()) {
			int max = maybeMax.getAsInt();

			if (x + max + 12 > screen.width) {
				x -= 16 + max;
			}

			int tooltipHeight = lines.size() > 1 ? lines.size() * 10 : 8;
			int height = screen.height;
			if (y + tooltipHeight > height) {
				y = height - tooltipHeight;
			}

			screen.drawHoveringText(lines, x - left, y - top);
		}
	}
}
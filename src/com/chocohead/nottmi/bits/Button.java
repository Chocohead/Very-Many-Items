package com.chocohead.nottmi.bits;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import com.chocohead.nottmi.NotTMIOverlay;

public class Button {
	private final Supplier<String> text;
	public int x;
	public int y;
	public int z;
	public int width;
	public int height;

	public Button(String text) {
		this(Suppliers.ofInstance(text));
	}

	public Button(Supplier<String> text) {
		this.text = text;
	}

	public boolean contains(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public void draw(NotTMIOverlay overlay, int mouseX, int mouseY) {
		overlay.drawRect(x, y, width, height, contains(mouseX, mouseY) ? -297791480 : -301989888);
		overlay.drawTextCentered(text.get(), x, y, width, height, -1);
	}

	public void onClick() {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
	}
}
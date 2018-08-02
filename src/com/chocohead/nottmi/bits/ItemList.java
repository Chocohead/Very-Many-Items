package com.chocohead.nottmi.bits;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;

import com.chocohead.nottmi.NotTMILog;
import com.chocohead.nottmi.NotTMIOverlay;

public enum ItemList {
	INSTANCE;

	public int x;
	public int y;
	public int z;
	public int width;
	public int height;
	private List<ItemStack> items = getItems();//new ArrayList<>(Arrays.asList(new ItemStack(Items.APPLE), new ItemStack(Blocks.STONE)));
	public static int page = 0;
	public int numPages = 0;
	public static final int SPACING = 18;
	public ItemStack hoverItem = null;
	private int marginLeft;
	private int marginTop;
	private int cols;
	private int rows;
	private int itemsPerPage;

	private static List<ItemStack> getItems() {
		NonNullList<ItemStack> list = NonNullList.create();
		Map<Item, Collection<ItemStack>> transformer = new IdentityHashMap<>();

		transformer.put(Items.AIR, Collections.emptyList());
		transformer.put(Blocks.MOB_SPAWNER.func_199767_j(), Collections.singleton(new ItemStack(Blocks.MOB_SPAWNER)));
		transformer.put(Blocks.DRAGON_EGG.func_199767_j(), Collections.singleton(new ItemStack(Blocks.DRAGON_EGG)));
		transformer.put(Blocks.COMMAND_BLOCK.func_199767_j(), Collections.singleton(new ItemStack(Blocks.COMMAND_BLOCK)));
		transformer.put(Blocks.BARRIER.func_199767_j(), Collections.singleton(new ItemStack(Blocks.BARRIER)));
		transformer.put(Blocks.REPEATING_COMMAND_BLOCK.func_199767_j(), Collections.singleton(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK)));
		transformer.put(Blocks.CHAIN_COMMAND_BLOCK.func_199767_j(), Collections.singleton(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK)));
		transformer.put(Blocks.STRUCTURE_VOID.func_199767_j(), Collections.singleton(new ItemStack(Blocks.STRUCTURE_VOID)));
		transformer.put(Blocks.STRUCTURE_BLOCK.func_199767_j(), Collections.singleton(new ItemStack(Blocks.STRUCTURE_BLOCK)));
		transformer.put(Items.FILLED_MAP, Collections.emptyList());
		transformer.put(Items.WRITTEN_BOOK, Collections.emptyList());
		transformer.put(Items.ENCHANTED_BOOK, Collections.emptyList()); //TODO: Maybe show all the enchanted books?
		transformer.put(Items.COMMAND_BLOCK_MINECART, Collections.singleton(new ItemStack(Items.COMMAND_BLOCK_MINECART)));
		transformer.put(Items.KNOWLEDGE_BOOK, Collections.singleton(new ItemStack(Items.KNOWLEDGE_BOOK)));
		transformer.put(Items.field_196180_eI, Collections.singleton(new ItemStack(Items.field_196180_eI)));

		for (Item item : Item.REGISTRY) {
			if (item.getCreativeTab() == null) {
				Collection<ItemStack> real = transformer.get(item);

				if (real != null) {
					list.addAll(real);
				} else {
					NotTMILog.info("Item " + item.getTranslationKey() + (item instanceof ItemBlock ? " (" + ((ItemBlock) item).getBlock().getTranslationKey() + ')' : "") + " has no creative tab");
				}
			} else {
				item.getSubItems(ItemGroup.SEARCH, list);
			}
		}

		return list;
	}

	public void resize(int screenWidth, int screenHeight, int width, int height, int left, int top) {
		//if (items.size() < 3) items.addAll(Stream.generate(() -> new ItemStack(Items.APPLE)).limit(150).collect(Collectors.toList()));
		x = left + width + 5;
		y = 30;
		this.width = screenWidth - 3 - x;
		this.height = screenHeight - 30 - y;

		marginLeft = x + this.width % 18 / 2;
		marginTop = y + this.height % 18 / 2;
		cols = this.width / 18;
		rows = this.height / 18;
		itemsPerPage = rows * cols;
		numPages = MathHelper.ceil((float) items.size() / itemsPerPage);

		page = MathHelper.clamp(page, 0, numPages - 1);
	}

	public void draw(NotTMIOverlay overlay, int mouseX, int mouseY) {
		ItemStack hoverItem = null;

		RenderHelper.enableGUIStandardItemLighting();
		for (int index = page * itemsPerPage, row = 0, col = 0; index < itemsPerPage * (page + 1) && index < items.size(); index++) {
			ItemStack stack = items.get(index);

			int xBorder = marginLeft + col * 18;
			int yBorder = marginTop + row * 18;
			if (mouseX >= xBorder && mouseX < xBorder + 18 && mouseY >= yBorder && mouseY < yBorder + 18) {
				hoverItem = stack;

				overlay.drawRect(xBorder - 1, yBorder - 1, 18, 18, -296397483);
			}

			overlay.drawItem(stack, xBorder, yBorder);

			if (++col == cols) {
				col = 0;
				row++;
			}
		}

		this.hoverItem = hoverItem;
		if (hoverItem != null) {
			overlay.drawTooltip(hoverItem, mouseX, mouseY);
		}

		RenderHelper.disableStandardItemLighting();
	}
}
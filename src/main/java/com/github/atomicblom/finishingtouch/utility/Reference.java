package com.github.atomicblom.finishingtouch.utility;

import com.github.atomicblom.finishingtouch.ItemLibrary;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("UtilityClass")
public final class Reference
{
	public static final String MOD_ID = "finishingtouch";
	public static final String NAME = "Finishing Touch";
	public static final String VERSION = "1.0";

	public static final CreativeTabs CreativeTab = new CreativeTabs(MOD_ID + ".tab_label") {
		private ItemStack _stack = null;

		@Override
		public ItemStack getTabIconItem()
		{
			return _stack != null
					? _stack
					: (_stack = new ItemStack(ItemLibrary.decal_wand));
		}
	};

	public static final class Items {

		public static final ResourceLocation DecalWand = resource("decal_wand");

		private Items() {}
	}

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	private Reference() {}
}

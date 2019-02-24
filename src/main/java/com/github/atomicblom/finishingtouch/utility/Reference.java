package com.github.atomicblom.finishingtouch.utility;

import com.github.atomicblom.finishingtouch.ItemLibrary;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("UtilityClass")
public final class Reference
{
	public static final String MOD_ID = "finishingtouch";
	public static final String NAME = "Finishing Touch";
	public static final String VERSION = "1.0";
	public static final String GUI_ID = resource("decal_selector").toString();


	public static final class Stats {

		public static final String DecalsAdded = MOD_ID + ":DecalsAdded";
		public static final String DecalsRemoved = MOD_ID + ":DecalsRemoved";

		private Stats() {}
	}

	public static final class Language {

		public static final String DecalsAdded = "stats." +MOD_ID + ":decals_added";
		public static final String DecalsRemoved = "stats." + MOD_ID + ":decals_removed";

		private Language() {}
	}

	public static final class Items {

		public static final ResourceLocation DecalWand = resource("decal_wand");

		private Items() {}
	}

	public static final class NBT {

		public static final String AuthorName = "AuthorName";
		public static final String DecalName = "DecalName";
		public static final String DecalType = "DecalType";
		public static final String DecalLocation = "DecalLocation";
		public static final String DecalOriginX = "X";
		public static final String DecalOriginY = "Y";
		public static final String DecalOriginZ = "Z";
		public static final String DecalAngle = "Angle";
		public static final String DecalScale = "Scale";
		public static final String DecalOrientation = "Orientation";

		public static final String ChunkDecals = "FinishingTouch";


		private NBT() {}
	}

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	private Reference() {}
}

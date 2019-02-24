package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraftforge.registries.ObjectHolder;
import javax.annotation.Nonnull;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class ItemLibrary
{
	@Nonnull
	public static final DecalWandItem decal_wand;

	static {
		decal_wand = null;
	}
}

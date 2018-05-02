package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.gui.GuiDecalSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	public static final int DECAL_SELECTOR = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == DECAL_SELECTOR) {
			return new GuiDecalSelector();
		}

		return null;
	}
}


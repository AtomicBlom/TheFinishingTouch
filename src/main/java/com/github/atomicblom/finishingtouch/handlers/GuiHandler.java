package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.ItemLibrary;
import com.github.atomicblom.finishingtouch.gui.GuiDecalSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
			ItemStack heldItemMainhand = player.getHeldItemMainhand();
			if (heldItemMainhand.getItem() != ItemLibrary.decal_wand) {
				boolean found = false;
				for (final ItemStack itemStack : player.inventory.mainInventory)
				{
					if (itemStack.getItem() == ItemLibrary.decal_wand) {
						found = true;
						heldItemMainhand = itemStack;
						break;
					}
				}
				if (!found) return null;
			}

			return new GuiDecalSelector(heldItemMainhand);
		}

		return null;
	}
}


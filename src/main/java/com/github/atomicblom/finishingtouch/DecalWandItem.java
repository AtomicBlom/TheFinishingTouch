package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.handlers.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class DecalWandItem extends Item
{
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if (player.isSneaking()) {
			player.openGui(TheFinishingTouch.INSTANCE, GuiHandler.DECAL_SELECTOR, world, (int)player.posX, (int)player.posY, (int)player.posZ);
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
		}

		return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(handIn));
	}
}

package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.gui.GuiDecalSelector;
import com.github.atomicblom.finishingtouch.handlers.DecalPositioningHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DecalWandItem extends Item
{
	public DecalWandItem(Properties p_i48487_1_)
	{
		super(p_i48487_1_);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn)
	{
		if (player.isSneaking()) {
			if (world.isRemote && !DecalPositioningHandler.isPlacing()) {
				Minecraft.getInstance().displayGuiScreen(new GuiDecalSelector(player.getHeldItem(handIn)));
				return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(handIn));
			}
		}

		return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(handIn));
	}
}

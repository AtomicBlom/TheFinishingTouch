package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.gui.GuiDecalSelector;
import com.github.atomicblom.finishingtouch.handlers.DecalPositioningHandler;
import com.github.atomicblom.finishingtouch.handlers.GuiHandler;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import javax.annotation.Nullable;

public class DecalWandItem extends Item
{
	public DecalWandItem(Properties p_i48487_1_)
	{
		super(p_i48487_1_);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if (player.isSneaking()) {
			if (world.isRemote && !DecalPositioningHandler.isPlacing()) {
				Minecraft.getInstance().displayGuiScreen(new GuiDecalSelector(player.getHeldItem(handIn)));
				return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
			}
		}

		return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(handIn));
	}
}

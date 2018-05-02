package com.github.atomicblom.finishingtouch.network;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetWandDecalMessageHandler implements IMessageHandler<SetWandDecalMessage, IMessage>
{
	@Override
	public IMessage onMessage(SetWandDecalMessage message, MessageContext ctx)
	{
		NetHandlerPlayServer serverHandler = ctx.getServerHandler();
		InventoryPlayer inventory = serverHandler.player.inventory;
		ItemStack currentItem = inventory.getCurrentItem();
		currentItem.setTagCompound(message.getTagCompound());
		inventory.markDirty();

		return null;
	}
}

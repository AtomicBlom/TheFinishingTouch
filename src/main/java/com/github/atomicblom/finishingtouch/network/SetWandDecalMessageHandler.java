package com.github.atomicblom.finishingtouch.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.impl.TagCommand;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.function.Supplier;

public class SetWandDecalMessageHandler
{

	public static SetWandDecalMessage fromBytes(ByteBuf buf)
	{
		PacketBuffer buffer = new PacketBuffer(buf);

		NBTTagCompound tagCompound = buffer.readCompoundTag();
		return new SetWandDecalMessage(tagCompound);

	}

	public static void toBytes(SetWandDecalMessage message, ByteBuf buf)
	{
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeCompoundTag(message.tagCompound);
	}

	public static void handle(SetWandDecalMessage message, Supplier<NetworkEvent.Context> ctxSupplier)
	{
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			InventoryPlayer inventory = ctx.getSender().inventory;
			ItemStack currentItem = inventory.getCurrentItem();
			currentItem.setTag(message.getTagCompound());
			inventory.markDirty();
		});

		ctx.setPacketHandled(true);
	}
}

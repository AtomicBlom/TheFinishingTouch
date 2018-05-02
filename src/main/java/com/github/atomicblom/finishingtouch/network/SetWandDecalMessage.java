package com.github.atomicblom.finishingtouch.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SetWandDecalMessage implements IMessage
{
	public SetWandDecalMessage(NBTTagCompound tagCompound)
	{
		this.tagCompound = tagCompound;
	}

	public SetWandDecalMessage()
	{
	}

	private NBTTagCompound tagCompound;

	public NBTTagCompound getTagCompound()
	{
		return tagCompound;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		tagCompound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, tagCompound);
	}
}

package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NotifyDecalAddedMessage implements IMessage
{
	private Decal decal;

	public NotifyDecalAddedMessage() {}

	public NotifyDecalAddedMessage(Decal decal)
	{
		this.decal = decal;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		decal = Decal.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		Decal.toBytes(buf, decal);
	}

	public Decal getDecal()
	{
		return decal;
	}
}

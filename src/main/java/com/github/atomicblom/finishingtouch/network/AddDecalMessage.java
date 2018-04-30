package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AddDecalMessage implements IMessage
{
	private Decal decal;

	public AddDecalMessage(Decal decal)
	{
		this.decal = decal;
	}

	public AddDecalMessage() {}

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

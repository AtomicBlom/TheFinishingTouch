package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DecalMessage implements IMessage
{
	private Decal decal;
	private DecalAction action;

	public DecalMessage(Decal decal, DecalAction action)
	{
		this.decal = decal;
		this.action = action;
	}

	public DecalMessage() {}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		action = buf.readBoolean() ? DecalAction.ADDING : DecalAction.REMOVING;
		decal = Decal.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(action == DecalAction.ADDING);
		Decal.toBytes(buf, decal);
	}

	public Decal getDecal()
	{
		return decal;
	}

	public DecalAction getAction()
	{
		return action;
	}
}

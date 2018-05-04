package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.List;

public class NotifyDecalAddedMessage implements IMessage
{
	private List<Decal> decals = Lists.newArrayList();

	public NotifyDecalAddedMessage() {}

	public NotifyDecalAddedMessage(Decal decal)
	{
		decals.add(decal);
	}

	public NotifyDecalAddedMessage(List<Decal> decals) {
		this.decals = decals;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int decalsInBuffer = buf.readInt();
		for (int i = 0; i < decalsInBuffer; i++) {
			decals.add(Decal.fromBytes(buf));
		}

	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(decals.size());
		for (Decal decal : decals) {
			Decal.toBytes(buf, decal);
		}
	}

	public List<Decal> getDecals()
	{
		return decals;
	}
}

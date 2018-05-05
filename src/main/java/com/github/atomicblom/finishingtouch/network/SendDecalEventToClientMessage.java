package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.List;

public class SendDecalEventToClientMessage implements IMessage
{
	private DecalAction action;
	private List<Decal> decals = Lists.newArrayList();

	public SendDecalEventToClientMessage() {}

	public SendDecalEventToClientMessage(Decal decal, DecalAction action)
	{
		this.action = action;
		decals.add(decal);
	}

	public SendDecalEventToClientMessage(List<Decal> decals) {
		this.decals = decals;
		this.action = DecalAction.ADDING;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		action = buf.readBoolean() ? DecalAction.ADDING : DecalAction.REMOVING;
		int decalsInBuffer = buf.readInt();
		for (int i = 0; i < decalsInBuffer; i++) {
			decals.add(Decal.fromBytes(buf));
		}

	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(action == DecalAction.ADDING);
		buf.writeInt(decals.size());
		for (Decal decal : decals) {
			Decal.toBytes(buf, decal);
		}
	}

	public List<Decal> getDecals()
	{
		return decals;
	}

	public DecalAction getAction()
	{
		return action;
	}
}

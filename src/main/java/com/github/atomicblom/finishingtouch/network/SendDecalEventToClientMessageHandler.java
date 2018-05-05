package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class SendDecalEventToClientMessageHandler implements IMessageHandler<SendDecalEventToClientMessage, IMessage>
{
	@Override
	public IMessage onMessage(SendDecalEventToClientMessage message, MessageContext ctx)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		Chunk chunk = player.world.getChunkFromChunkCoords(player.chunkCoordX, player.chunkCoordZ);
		List<Decal> decalList = message.getDecals();
		DecalAction action = message.getAction();

		if (action == DecalAction.ADDING)
		{
			for (Decal decal : decalList)
			{
				ClientDecalStore.addDecal(chunk, decal);
			}
		} else {
			for (Decal decal : decalList)
			{
				ClientDecalStore.removeDecal(chunk, decal);
			}
		}


		return null;
	}
}

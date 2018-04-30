package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.RenderableDecalStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class NotifyDecalAddedMessageHandler implements IMessageHandler<NotifyDecalAddedMessage, IMessage>
{
	@Override
	public IMessage onMessage(NotifyDecalAddedMessage message, MessageContext ctx)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		Chunk chunk = player.world.getChunkFromChunkCoords(player.chunkCoordX, player.chunkCoordZ);
		Decal decal = message.getDecal();

		RenderableDecalStore.addDecal(chunk, decal);

		return null;
	}
}

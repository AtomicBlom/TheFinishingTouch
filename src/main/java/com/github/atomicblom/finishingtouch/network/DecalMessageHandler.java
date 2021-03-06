package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.NonRegistryLibrary;
import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.decals.ServerDecalStore;
import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DecalMessageHandler implements IMessageHandler<DecalMessage, IMessage>

{
	@Override
	public IMessage onMessage(DecalMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		Chunk chunk = player.world.getChunkFromBlockCoords(player.getPosition());
		Decal decal = message.getDecal();
		DecalAction action = message.getAction();
		if (chunk.isLoaded()) {
			if (action == DecalAction.ADDING)
			{
				ServerDecalStore.addDecal(chunk, decal);
				player.addStat(NonRegistryLibrary.decalsAdded, 1);

			} else {
				ServerDecalStore.removeDecal(chunk, decal);
				player.addStat(NonRegistryLibrary.decalsRemoved, 1);
			}
			LogHelper.info(decal);
		}

		TheFinishingTouch.CHANNEL.sendToDimension(
				new SendDecalEventToClientMessage(decal, action),
				player.dimension
		);

		return null;
	}
}

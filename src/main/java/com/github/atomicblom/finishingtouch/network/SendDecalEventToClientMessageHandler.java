package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public final class SendDecalEventToClientMessageHandler
{
	public static SendDecalEventToClientMessage fromBytes(ByteBuf buf)
	{
		final int dimension = buf.readInt();
		final DecalAction action = buf.readBoolean() ? DecalAction.ADDING : DecalAction.REMOVING;
		final int decalsInBuffer = buf.readInt();
		final List<Decal> decals = Lists.newArrayList();
		for (int i = 0; i < decalsInBuffer; i++) {
			decals.add(Decal.fromBytes(buf));
		}

		SendDecalEventToClientMessage message = new SendDecalEventToClientMessage();
		message.dimension = dimension;
		message.action = action;
		message.decals = decals;
		return message;
	}

	public static void toBytes(SendDecalEventToClientMessage message, ByteBuf buf)
	{
		buf.writeInt(message.dimension);
		buf.writeBoolean(message.action == DecalAction.ADDING);
		buf.writeInt(message.decals.size());
		for (Decal decal : message.decals) {
			Decal.toBytes(buf, decal);
		}
	}

	public static void handle(SendDecalEventToClientMessage message, Supplier<NetworkEvent.Context> ctxSupplier)
	{
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			EntityPlayerSP player = Minecraft.getInstance().player;
			World world = player.world;
			if (world.getWorldInfo().getDimension() != message.dimension) return;

			Chunk chunk = world.getChunk(player.chunkCoordX, player.chunkCoordZ);
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
		});

		ctx.setPacketHandled(true);
	}
}

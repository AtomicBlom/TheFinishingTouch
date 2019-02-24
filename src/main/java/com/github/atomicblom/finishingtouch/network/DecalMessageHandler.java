package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.ServerDecalStore;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import java.util.function.Supplier;


public class DecalMessageHandler
{
	public static DecalMessage fromBytes(ByteBuf buf)
	{
		DecalAction action = buf.readBoolean() ? DecalAction.ADDING : DecalAction.REMOVING;
		Decal decal = Decal.fromBytes(buf);
		return new DecalMessage(decal, action);
	}

	public static void toBytes(DecalMessage message, ByteBuf buf)
	{
		buf.writeBoolean(message.action == DecalAction.ADDING);
		Decal.toBytes(buf, message.decal);
	}

	public static void handle(DecalMessage message, Supplier<Context> ctxSupplier)
	{
		final Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			EntityPlayerMP player = ctx.getSender();
			World world = player.world;
			Chunk chunk = world.getChunk(player.getPosition());
			Decal decal = message.getDecal();
			DecalAction action = message.getAction();
			if (chunk.isLoaded()) {
				if (action == DecalAction.ADDING)
				{
					ServerDecalStore.addDecal(chunk, decal);
					//FIXME: Reenable this
					//player.addStat(NonRegistryLibrary.decalsAdded, 1);

				} else {
					ServerDecalStore.removeDecal(chunk, decal);
					//FIXME: Reenable this
					//player.addStat(NonRegistryLibrary.decalsRemoved, 1);
				}
				LogHelper.info(decal);
			}

			TheFinishingTouch.CHANNEL.sendTo(
					new SendDecalEventToClientMessage(world.getWorldInfo().getDimension(), decal, action),
					player.connection.getNetworkManager(),
					NetworkDirection.PLAY_TO_CLIENT
			);
		});

		ctx.setPacketHandled(true);
	}
}

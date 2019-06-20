package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.DecalList;
import com.github.atomicblom.finishingtouch.decals.ServerDecalStore;
import com.github.atomicblom.finishingtouch.network.SendDecalEventToClientMessage;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.utility.Reference.NBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkDataEvent.Load;
import net.minecraftforge.event.world.ChunkDataEvent.Save;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public final class ChunkHandler
{
	@SubscribeEvent
	public static void chunkLoad(Load event) {
		final CompoundNBT data = event.getData();
		if (!data.contains(NBT.ChunkDecals)) return;

		final ListNBT tagList = data.getList(NBT.ChunkDecals, Constants.NBT.TAG_COMPOUND);
		for (final INBT nbtBase : tagList) {
			if (!(nbtBase instanceof CompoundNBT)) {
				LogHelper.error("Wrong NBT Type serialized, not loading further decals from chunk");
				return;
			}

			final Decal decal = Decal.fromNBT((CompoundNBT) nbtBase);
			ServerDecalStore.addDecal(event.getChunk(), decal);
		}

		//DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(event.getChunk(), false);
		//int numberOfDecalsInChunk = decalsInChunk == null ? 0 : decalsInChunk.decals.size();
		//LogHelper.info("Loading Chunk {},{} - {} decals", event.getChunk().x, event.getChunk().z, numberOfDecalsInChunk);
	}

	@SubscribeEvent
	public static void chunkSave(Save event) {
		final IChunk chunk = event.getChunk();
		if (event.getWorld() == null) {
			LogHelper.info("Not saving Saving Chunk {},{} - {}", chunk.getPos().x, chunk.getPos().z, chunk.getStatus());
			return;
		}

		final DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(chunk, false);
		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;

		final ListNBT decalData = new ListNBT();
		for (final Decal decal : decalsInChunk.decals) {
			decalData.add(Decal.asNBT(decal));
		}

		event.getData().put(NBT.ChunkDecals, decalData);

		//LogHelper.info("Saving Chunk {},{} - {} decals", chunk.x, chunk.z, decalsInChunk.decals.size());
	}

	@SubscribeEvent
	public static void chunkUnload(Unload event) {

		IChunk chunk = event.getChunk();
		ServerDecalStore.releaseChunk(chunk);
	}

	@SubscribeEvent
	public static void chunkWatch(Watch event) {
		ChunkPos pos = event.getPos();
		;
		final IChunk chunk = event.getWorld().getChunk(pos.x, pos.z);
		final DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(chunk, false);

		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;
		//LogHelper.info("Player watching chunk Chunk {},{} - {} decals", chunk.x, chunk.z, decalsInChunk.decals.size());

		TheFinishingTouch.CHANNEL.sendTo(
				new SendDecalEventToClientMessage(event.getWorld().getWorldInfo().getDimension(), decalsInChunk.decals),
				event.getPlayer().connection.getNetworkManager(),
				NetworkDirection.PLAY_TO_CLIENT
		);
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		if (!event.getWorld().isRemote())
		{
			int dimension = event.getWorld().getWorldInfo().getDimension();
			ServerDecalStore.clearAllDecalsForDimension(dimension);
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote())
		{
			int dimension = event.getWorld().getWorldInfo().getDimension();
			ServerDecalStore.clearAllDecalsForDimension(dimension);
		}
	}
}

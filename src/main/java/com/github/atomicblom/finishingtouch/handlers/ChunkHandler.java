package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.DecalList;
import com.github.atomicblom.finishingtouch.decals.ServerDecalStore;
import com.github.atomicblom.finishingtouch.network.SendDecalEventToClientMessage;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.utility.Reference.NBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkDataEvent.Load;
import net.minecraftforge.event.world.ChunkDataEvent.Save;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public final class ChunkHandler
{
	@SubscribeEvent
	public static void chunkLoad(Load event) {
		final NBTTagCompound data = event.getData();
		if (!data.hasKey(NBT.ChunkDecals)) return;

		final NBTTagList tagList = data.getTagList(NBT.ChunkDecals, Constants.NBT.TAG_COMPOUND);
		for (final NBTBase nbtBase : tagList) {
			if (!(nbtBase instanceof NBTTagCompound)) {
				LogHelper.error("Wrong NBT Type serialized, not loading further decals from chunk");
				return;
			}

			final Decal decal = Decal.fromNBT((NBTTagCompound) nbtBase);
			ServerDecalStore.addDecal(event.getChunk(), decal);
		}

		DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(event.getChunk(), false);
		int numberOfDecalsInChunk = decalsInChunk == null ? 0 : decalsInChunk.decals.size();
		if (numberOfDecalsInChunk > 0)
		{
			LogHelper.info("Loading Chunk {},{} - {} decals", event.getChunk().x, event.getChunk().z, numberOfDecalsInChunk);
		}

	}

	@SubscribeEvent
	public static void chunkSave(Save event) {
		final Chunk chunk = event.getChunk();
		final DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(chunk, false);
		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;

		final NBTTagList decalData = new NBTTagList();
		for (final Decal decal : decalsInChunk.decals) {
			decalData.appendTag(Decal.asNBT(decal));
		}

		event.getData().setTag(NBT.ChunkDecals, decalData);

		if (!decalsInChunk.decals.isEmpty())
		{
			LogHelper.info("Saving Chunk {},{} - {} decals", chunk.x, chunk.z, decalsInChunk.decals.size());
		}
	}

	@SubscribeEvent
	public static void chunkUnload(Unload event) {
		if (!event.getWorld().isRemote)
		{
			Chunk chunk = event.getChunk();
			ServerDecalStore.releaseChunk(chunk);
		}
	}

	@SubscribeEvent
	public static void chunkWatch(Watch event) {
		final Chunk chunk = event.getChunkInstance();
		final DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(chunk, false);

		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;
		//LogHelper.info("Player watching chunk Chunk {},{} - {} decals", chunk.x, chunk.z, decalsInChunk.decals.size());

		TheFinishingTouch.CHANNEL.sendTo(new SendDecalEventToClientMessage(decalsInChunk.decals), event.getPlayer());
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		if (!event.getWorld().isRemote)
		{
			int dimension = event.getWorld().provider.getDimension();
			ServerDecalStore.clearAllDecalsForDimension(dimension);
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote)
		{
			int dimension = event.getWorld().provider.getDimension();
			ServerDecalStore.clearAllDecalsForDimension(dimension);
		}
	}
}

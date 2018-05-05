package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
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
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.SERVER)
public final class ChunkHandler
{
	@SubscribeEvent
	public static void chunkLoad(Load event) {
		final NBTTagCompound data = event.getData();
		if (!data.hasKey(NBT.ChunkDecals)) return;

		final NBTTagList tagList = data.getTagList(NBT.ChunkDecals, Constants.NBT.TAG_COMPOUND);
		int decalsLoaded = 0;
		for (final NBTBase nbtBase : tagList) {
			if (!(nbtBase instanceof NBTTagCompound)) {
				LogHelper.error("Wrong NBT Type serialized, not loading further decals from chunk");
				return;
			}

			final Decal decal = Decal.fromNBT((NBTTagCompound) nbtBase);
			ServerDecalStore.addDecal(event.getChunk(), decal);
			decalsLoaded++;
		}

		LogHelper.info("Loading Chunk {},{} (IsRemote:{}) - {} decals", event.getChunk().x, event.getChunk().z, event.getWorld().isRemote, decalsLoaded);
	}

	@SubscribeEvent
	public static void chunkSave(Save event) {
		final Chunk chunk = event.getChunk();
		final DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(chunk);
		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;

		final NBTTagList decalData = new NBTTagList();
		for (final Decal decal : decalsInChunk.decals) {
			decalData.appendTag(Decal.asNBT(decal));
		}

		event.getData().setTag(NBT.ChunkDecals, decalData);

		LogHelper.info("Saving Chunk {},{} (IsRemote:{}) - {} decals", chunk.x, chunk.z, chunk.getWorld().isRemote, decalsInChunk.decals.size());
	}

	@SubscribeEvent
	public static void chunkUnload(Unload event) {
		ServerDecalStore.releaseChunk(event.getChunk());
	}

	@SubscribeEvent
	public static void chunkWatch(Watch event) {
		final Chunk chunk = event.getChunkInstance();
		final DecalList decalsInChunk = ServerDecalStore.getDecalsInChunk(chunk);
		LogHelper.info("Player watching chunk Chunk {},{} (IsRemote:{}) - {} decals", chunk.x, chunk.z, chunk.getWorld().isRemote, decalsInChunk.decals.size());
		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;



		TheFinishingTouch.CHANNEL.sendTo(new SendDecalEventToClientMessage(decalsInChunk.decals), event.getPlayer());
	}

	@SubscribeEvent
	public static void onWorldUnload(Unload event) {
		//ServerDecalStore.clearAllDecals();
	}
}

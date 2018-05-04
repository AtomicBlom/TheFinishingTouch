package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.DecalList;
import com.github.atomicblom.finishingtouch.decals.DecalStore;
import com.github.atomicblom.finishingtouch.network.NotifyDecalAddedMessage;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ChunkHandler
{
	@SubscribeEvent
	public static void chunkLoad(ChunkDataEvent.Load event) {
		NBTTagCompound data = event.getData();
		if (data.hasKey(Reference.NBT.ChunkDecals)) return;

		LogHelper.info("Loading Chunk {},{} (IsRemote:{})", event.getChunk().x, event.getChunk().z, event.getWorld().isRemote);

		NBTTagList tagList = data.getTagList(Reference.NBT.ChunkDecals, Constants.NBT.TAG_LIST);
		for (NBTBase nbtBase : tagList) {
			if (!(nbtBase instanceof NBTTagCompound)) {
				LogHelper.error("Wrong NBT Type serialized, not loading further decals from chunk");
				return;
			}

			Decal decal = Decal.fromNBT((NBTTagCompound) nbtBase);
			DecalStore.addDecal(event.getChunk(), decal);
		}
	}

	@SubscribeEvent
	public static void chunkSave(ChunkDataEvent.Save event) {
		DecalList decalsInChunk = DecalStore.getDecalsInChunk(event.getChunk());
		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;

		LogHelper.info("Saving Chunk {},{} (IsRemote:{})", event.getChunk().x, event.getChunk().z, event.getWorld().isRemote);

		NBTTagList decalData = new NBTTagList();
		for (Decal decal : decalsInChunk.decals) {
			decalData.appendTag(Decal.asNBT(decal));
		}

		event.getData().setTag(Reference.NBT.ChunkDecals, decalData);
	}

	@SubscribeEvent
	public static void chunkUnload(ChunkEvent.Unload event) {
		DecalStore.releaseChunk(event.getChunk());
	}

	@SubscribeEvent
	public static void chunkWatch(ChunkWatchEvent.Watch event) {
		DecalList decalsInChunk = DecalStore.getDecalsInChunk(event.getChunkInstance());
		if (decalsInChunk == null || decalsInChunk.decals.isEmpty()) return;

		TheFinishingTouch.CHANNEL.sendTo(new NotifyDecalAddedMessage(decalsInChunk.decals), event.getPlayer());
	}
}

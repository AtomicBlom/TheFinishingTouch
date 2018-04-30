package com.github.atomicblom.finishingtouch.handlers;

import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ChunkHandler
{
	@SubscribeEvent
	public static void chunkLoad(ChunkDataEvent.Load event) {
		//LogHelper.info("Loading Chunk {},{} (IsRemote:{})", event.getChunk().x, event.getChunk().z, event.getWorld().isRemote);
	}

	@SubscribeEvent
	public static void chunkSave(ChunkDataEvent.Save event) {
		//LogHelper.info("Saving Chunk {},{} (IsRemote:{})", event.getChunk().x, event.getChunk().z, event.getWorld().isRemote);
	}

	@SubscribeEvent
	public static void chunkUnload(ChunkEvent.Unload event) {
		//LogHelper.info("Unload Chunk {},{} (IsRemote:{})", event.getChunk().x, event.getChunk().z, event.getWorld().isRemote);
	}


}

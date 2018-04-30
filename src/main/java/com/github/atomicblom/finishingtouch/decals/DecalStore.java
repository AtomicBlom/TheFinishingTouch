package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.google.common.collect.Maps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import java.util.Map;

public class DecalStore
{
	private static final Map<Integer, Map<Long, DecalList>> decalStore = Maps.newHashMap();

	public static void addDecal(Chunk chunk, Decal decal)
	{
		if (!chunk.isLoaded()) return;
		LogHelper.info("Decal added to server store: {}", decal);

		final int dimension = chunk.getWorld().provider.getDimension();

		final Map<Long, DecalList> dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> Maps.newHashMap());

		final long chunkPos = ChunkPos.asLong(chunk.x, chunk.z);
		final DecalList decalList = dimensionChunkMap.computeIfAbsent(chunkPos, k -> new DecalList(chunk.x, chunk.z));

		decalList.add(decal);
	}
}

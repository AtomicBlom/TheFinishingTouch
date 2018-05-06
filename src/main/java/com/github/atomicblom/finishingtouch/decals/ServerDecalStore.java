package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.google.common.collect.Maps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import javax.annotation.Nullable;
import java.util.Map;

public final class ServerDecalStore
{
	private static final Map<Integer, Map<Long, DecalList>> decalStore = Maps.newHashMap();

	private ServerDecalStore() {}

	public static void addDecal(Chunk chunk, Decal decal)
	{
		final DecalList decalList = getDecalsInChunk(chunk, true);
		if (decalList == null) return;
		//LogHelper.info("Decal added to server store: {}", decal);

		decalList.add(decal);
		chunk.markDirty();
	}

	public static void removeDecal(Chunk chunk, Decal decal)
	{
		final DecalList decalList = getDecalsInChunk(chunk, false);
		if (decalList == null) return;
		//LogHelper.info("Decal removed from server store: {}", decal);
		decalList.decals.removeIf(decalInList -> decalInList.Is(decal));
		chunk.markDirty();
	}

	@Nullable
	public static DecalList getDecalsInChunk(Chunk chunk, boolean createIfNeccessary) {
		if (chunk == null) return null;

		final int dimension = chunk.getWorld().provider.getDimension();

		final Map<Long, DecalList> dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> Maps.newHashMap());

		final long chunkPos = ChunkPos.asLong(chunk.x, chunk.z);

		if (createIfNeccessary)
		{
			return dimensionChunkMap.computeIfAbsent(chunkPos, key -> {
				//LogHelper.info("Created DecalList for {},{}", chunk.x, chunk.z);
				return new DecalList(chunk.x, chunk.z);
			});
		} else {
			return dimensionChunkMap.get(chunkPos);
		}
	}

	public static void releaseChunk(Chunk chunk) {
		final int dimension = chunk.getWorld().provider.getDimension();

		final Map<Long, DecalList> dimensionChunkMap = decalStore.get(dimension);
		if (dimensionChunkMap != null)
		{
			final long chunkPos = ChunkPos.asLong(chunk.x, chunk.z);
			dimensionChunkMap.remove(chunkPos);
		}
	}

	public static void clearAllDecalsForDimension(int dimension)
	{
		decalStore.remove(dimension);
	}
}

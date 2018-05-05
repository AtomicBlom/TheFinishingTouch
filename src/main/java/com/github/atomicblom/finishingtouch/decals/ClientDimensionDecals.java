package com.github.atomicblom.finishingtouch.decals;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.math.ChunkPos;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClientDimensionDecals
{
	private final Map<Long, DecalList> chunkDecalList = Maps.newHashMap();
	private boolean needsUpdating = false;

	public void setNeedsUpdating() {
		needsUpdating = true;
	}

	public DecalList getDecalListForChunk(int x, int z)
	{
		final long chunkPos = ChunkPos.asLong(x, z);
		return chunkDecalList.computeIfAbsent(chunkPos, key -> {
			needsUpdating = true;
			return new DecalList(x, z);
		});
	}

	private final Cache<Long, CompletableFuture<List<DecalList>>> decalListCache = CacheBuilder.newBuilder()
			.maximumSize(15)
			.build();

	public Iterable<DecalList> getDecalListsForNearChunk(int chunkX, int chunkZ)
	{
		if (needsUpdating) {
			decalListCache.invalidateAll();
			needsUpdating = false;
		}

		try
		{
			final long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
			final CompletableFuture<List<DecalList>> recentDecalLists = decalListCache.get(chunkPos, () ->
					CompletableFuture.supplyAsync(() -> FindChunksNear(chunkX, chunkZ))
			);
			return recentDecalLists.isDone() ? recentDecalLists.get() : new ArrayList<>(0);

		} catch (final Exception e) {
			throw new RuntimeException("Error while finding decal chunks");
		}
	}

	private List<DecalList> FindChunksNear(int chunkX, int chunkZ)
	{
		synchronized (chunkDecalList) {
			final List<DecalList> returnedList = Lists.newArrayList();
			for (final DecalList decalList : chunkDecalList.values())
			{
				final double distanceSq = StrictMath.pow(chunkX - decalList.chunkX, 2) + StrictMath.pow(chunkZ - decalList.chunkZ, 2);
				if (distanceSq < 40) {
					returnedList.add(decalList);
				}
			}
			return returnedList;
		}
	}
}

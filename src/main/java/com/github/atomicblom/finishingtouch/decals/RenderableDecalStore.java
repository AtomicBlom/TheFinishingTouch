package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RenderableDecalStore
{
	private static Map<Integer, DimensionDecals> decalStore = Maps.newHashMap();

	public static void addDecal(Chunk chunk, Decal decal)
	{
		if (!chunk.isLoaded()) return;
		LogHelper.info("Decal added to server store: {}", decal);

		final int dimension = chunk.getWorld().provider.getDimension();

		final DimensionDecals dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> new DimensionDecals());


		final DecalList decalList = dimensionChunkMap.getDecalListForChunk(chunk.x, chunk.z);

		decalList.add(decal);
		dimensionChunkMap.setNeedsUpdating();
		//FIXME: start preparing the decal display list
	}

	public static Iterable<DecalList> getDecalsAround(int dimension, BlockPos position)
	{
		int chunkX = position.getX() >> 4;
		int chunkZ = position.getZ() >> 4;

		DimensionDecals dimensionChunkMap = decalStore.get(dimension);
		if (dimensionChunkMap == null) return new ArrayList<>(0);

		return dimensionChunkMap.getDecalListsForNearChunk(chunkX, chunkZ);
	}

	public static void clearAllDecals()
	{
		decalStore.clear();
	}
}

class DimensionDecals {
	Map<Long, DecalList> chunkDecalList = Maps.newHashMap();
	private boolean needsUpdating = false;

	public void setNeedsUpdating() {
		needsUpdating = true;
	}

	public DecalList getDecalListForChunk(int x, int z)
	{
		final long chunkPos = ChunkPos.asLong(x, z);
		return chunkDecalList.computeIfAbsent(chunkPos, k -> {
			needsUpdating = true;
			return new DecalList(x, z);
		});
	}

	Cache<Long, CompletableFuture<List<DecalList>>> decalListCache = CacheBuilder.newBuilder()
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
			CompletableFuture<List<DecalList>> recentDecalLists = decalListCache.get(chunkPos, () ->
					CompletableFuture.supplyAsync(() -> FindChunksNear(chunkX, chunkZ))
			);
			if (!recentDecalLists.isDone()) {
				return new ArrayList<>(0);
			} else {
				return recentDecalLists.get();
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while finding decal chunks");
		}
	}

	private List<DecalList> FindChunksNear(int chunkX, int chunkZ)
	{
		synchronized (chunkDecalList) {
			List<DecalList> returnedList = Lists.newArrayList();
			for (final DecalList decalList : chunkDecalList.values())
			{
				double distanceSq = StrictMath.pow(chunkX - decalList.chunkX, 2) + StrictMath.pow(chunkZ - decalList.chunkZ, 2);
				if (distanceSq < 40) {
					returnedList.add(decalList);
				}
			}
			return returnedList;
		}
	}
}

class RecentDecalLists {
	public final long chunkPosHash;
	public final CompletableFuture<List<DecalList>> lists;

	public RecentDecalLists(long chunkPosHash, CompletableFuture<List<DecalList>> lists) {

		this.chunkPosHash = chunkPosHash;
		this.lists = lists;
	}
}

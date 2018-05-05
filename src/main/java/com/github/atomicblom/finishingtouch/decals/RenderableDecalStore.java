package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RenderableDecalStore
{
	private static Map<Integer, ClientDimensionDecals> decalStore = Maps.newHashMap();

	public static void addDecal(Chunk chunk, Decal decal)
	{
		int chunkX = chunk.x;
		int chunkZ = chunk.z;

		if (chunk instanceof EmptyChunk) {
			chunkX = ((int)decal.getOrigin().x) >> 4;
			chunkZ = ((int)decal.getOrigin().z) >> 4;
		} else if (!chunk.isLoaded()) return;

		final int dimension = chunk.getWorld().provider.getDimension();

		final ClientDimensionDecals dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> new ClientDimensionDecals());
		final DecalList decalList = dimensionChunkMap.getDecalListForChunk(chunkX, chunkZ);

		decalList.add(decal);
		dimensionChunkMap.setNeedsUpdating();
		LogHelper.info("Decal removed from client store: {}", decal);

		//FIXME: start preparing the decal display list
	}

	public static void removeDecal(Chunk chunk, Decal decal)
	{
		int chunkX = chunk.x;
		int chunkZ = chunk.z;

		if (chunk instanceof EmptyChunk) {
			chunkX = ((int)decal.getOrigin().x) >> 4;
			chunkZ = ((int)decal.getOrigin().z) >> 4;
		} else if (!chunk.isLoaded()) return;

		final int dimension = chunk.getWorld().provider.getDimension();

		final ClientDimensionDecals dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> new ClientDimensionDecals());
		final DecalList decalList = dimensionChunkMap.getDecalListForChunk(chunkX, chunkZ);

		decalList.decals.removeIf(d -> d.Is(decal));

		dimensionChunkMap.setNeedsUpdating();
		LogHelper.info("Decal removed from client store: {}", decal);
	}

	public static DecalList getDecalsInChunk(Chunk chunk) {
		if (!chunk.isLoaded()) return null;

		final int dimension = chunk.getWorld().provider.getDimension();
		final ClientDimensionDecals dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> new ClientDimensionDecals());
		return dimensionChunkMap.getDecalListForChunk(chunk.x, chunk.z);
	}

	public static Iterable<DecalList> getDecalsAround(int dimension, BlockPos position)
	{
		int chunkX = position.getX() >> 4;
		int chunkZ = position.getZ() >> 4;

		ClientDimensionDecals dimensionChunkMap = decalStore.get(dimension);
		if (dimensionChunkMap == null) return new ArrayList<>(0);

		return dimensionChunkMap.getDecalListsForNearChunk(chunkX, chunkZ);
	}

	public static void clearAllDecals()
	{
		decalStore.clear();
	}
}

class ClientDimensionDecals
{
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

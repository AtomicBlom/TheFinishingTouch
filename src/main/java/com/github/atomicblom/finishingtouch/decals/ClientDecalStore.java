package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.google.common.collect.Maps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import java.util.ArrayList;
import java.util.Map;

public final class ClientDecalStore
{
	private static final Map<Integer, ClientDimensionDecals> decalStore = Maps.newHashMap();

	private ClientDecalStore() {}

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
		//LogHelper.info("Decal added to client store: {}", decal);

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

		decalList.decals.removeIf(decalInList -> decalInList.Is(decal));

		dimensionChunkMap.setNeedsUpdating();
		//LogHelper.info("Decal removed from client store: {}", decal);
	}

	public static DecalList getDecalsInChunk(Chunk chunk) {
		if (!chunk.isLoaded()) return null;

		final int dimension = chunk.getWorld().provider.getDimension();
		final ClientDimensionDecals dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> new ClientDimensionDecals());
		return dimensionChunkMap.getDecalListForChunk(chunk.x, chunk.z);
	}

	public static Iterable<DecalList> getDecalsAround(int dimension, BlockPos position)
	{
		final int chunkX = position.getX() >> 4;
		final int chunkZ = position.getZ() >> 4;

		final ClientDimensionDecals dimensionChunkMap = decalStore.get(dimension);
		if (dimensionChunkMap == null) return new ArrayList<>(0);

		return dimensionChunkMap.getDecalListsForNearChunk(chunkX, chunkZ);
	}

	public static void clearAllDecals()
	{
		decalStore.clear();
	}
}


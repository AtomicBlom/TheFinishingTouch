package com.github.atomicblom.finishingtouch.decals;

import com.google.common.collect.Maps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.storage.WorldInfo;
import javax.annotation.Nullable;
import java.util.Map;

public final class ServerDecalStore
{
	private static final Map<Integer, Map<Long, DecalList>> decalStore = Maps.newHashMap();

	private ServerDecalStore() {}

	public static void addDecal(IChunk chunk, Decal decal)
	{
		final DecalList decalList = getDecalsInChunk(chunk, true);
		if (decalList == null) return;
		//LogHelper.info("Decal added to server store: {}", decal);

		decalList.add(decal);
		//FIXME: Is this appropriate?
		chunk.setLastSaveTime(0);
		//chunk.markDirty();
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
	public static DecalList getDecalsInChunk(IChunk chunk, boolean createIfNeccessary) {
		if (chunk == null) return null;

		IWorld worldForge = chunk.getWorldForge();
		WorldInfo worldInfo = worldForge.getWorldInfo();
		final int dimension = worldInfo.getDimension();

		final Map<Long, DecalList> dimensionChunkMap = decalStore.computeIfAbsent(dimension, k -> Maps.newHashMap());

		final ChunkPos chunkPos = chunk.getPos();

		if (createIfNeccessary)
		{
			return dimensionChunkMap.computeIfAbsent(chunkPos.asLong(), key -> {
				//LogHelper.info("Created DecalList for {},{}", chunk.x, chunk.z);
				return new DecalList(chunkPos.x, chunkPos.z);
			});
		} else {
			return dimensionChunkMap.get(chunkPos.asLong());
		}
	}

	public static void releaseChunk(IChunk chunk) {
		final int dimension = chunk.getWorldForge().getWorldInfo().getDimension();

		final Map<Long, DecalList> dimensionChunkMap = decalStore.get(dimension);
		if (dimensionChunkMap != null)
		{
			dimensionChunkMap.remove(chunk.getPos().asLong());
		}
	}

	public static void clearAllDecalsForDimension(int dimension)
	{
		decalStore.remove(dimension);
	}
}

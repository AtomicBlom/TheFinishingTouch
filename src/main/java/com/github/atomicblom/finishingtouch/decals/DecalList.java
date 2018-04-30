package com.github.atomicblom.finishingtouch.decals;

import com.google.common.collect.Lists;
import java.util.List;

public class DecalList
{
	public final int chunkX;
	public final int chunkZ;

	public final List<Decal> decals = Lists.newArrayList();

	public DecalList(int chunkX, int chunkZ)
	{
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public void add(Decal decal)
	{
		decals.add(decal);
	}
}

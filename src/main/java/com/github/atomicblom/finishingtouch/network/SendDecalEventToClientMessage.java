package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.google.common.collect.Lists;

import java.util.List;

public class SendDecalEventToClientMessage
{
	DecalAction action;
	int dimension;
	List<Decal> decals = Lists.newArrayList();

	public SendDecalEventToClientMessage() {}

	public SendDecalEventToClientMessage(int dimension, Decal decal, DecalAction action)
	{
		this.dimension = dimension;
		this.action = action;
		decals.add(decal);
	}

	public SendDecalEventToClientMessage(int dimension, List<Decal> decals) {
		this.dimension = dimension;
		this.decals = decals;
		this.action = DecalAction.ADDING;
	}

	public List<Decal> getDecals()
	{
		return decals;
	}

	public DecalAction getAction()
	{
		return action;
	}

	public int getDimension() { return dimension; }
}

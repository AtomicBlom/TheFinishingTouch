package com.github.atomicblom.finishingtouch.network;

import com.github.atomicblom.finishingtouch.decals.Decal;

public class DecalMessage
{
	Decal decal;
	DecalAction action;

	public DecalMessage(Decal decal, DecalAction action)
	{
		this.decal = decal;
		this.action = action;
	}

	public DecalMessage() {}

	public Decal getDecal()
	{
		return decal;
	}

	public DecalAction getAction()
	{
		return action;
	}
}

package com.github.atomicblom.finishingtouch.network;

import net.minecraft.nbt.CompoundNBT;

public class SetWandDecalMessage
{
	public SetWandDecalMessage(CompoundNBT tagCompound)
	{
		this.tagCompound = tagCompound;
	}

	public SetWandDecalMessage()
	{
	}

	CompoundNBT tagCompound;

	public CompoundNBT getTagCompound()
	{
		return tagCompound;
	}
}

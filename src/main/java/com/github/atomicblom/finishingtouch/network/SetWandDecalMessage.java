package com.github.atomicblom.finishingtouch.network;

import net.minecraft.nbt.NBTTagCompound;

public class SetWandDecalMessage
{
	public SetWandDecalMessage(NBTTagCompound tagCompound)
	{
		this.tagCompound = tagCompound;
	}

	public SetWandDecalMessage()
	{
	}

	NBTTagCompound tagCompound;

	public NBTTagCompound getTagCompound()
	{
		return tagCompound;
	}
}

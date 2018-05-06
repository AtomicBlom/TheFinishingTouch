package com.github.atomicblom.finishingtouch.handlers;

import net.minecraft.util.EnumFacing;

public class RenderHelp
{
	//Simply for context
	private final EnumFacing facing;
	public final double rotation;
	public final boolean invertedRotation;
	public final boolean flipTexture;

	RenderHelp(EnumFacing facing, double rotation, boolean invertedRotation, boolean flipTexture) {

		this.facing = facing;
		this.rotation = rotation;
		this.invertedRotation = invertedRotation;
		this.flipTexture = flipTexture;
	}
}

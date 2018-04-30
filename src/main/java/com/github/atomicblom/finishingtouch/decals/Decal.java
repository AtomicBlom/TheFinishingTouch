package com.github.atomicblom.finishingtouch.decals;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Decal
{
	private final Vec3d origin;
	private final EnumFacing orientation;
	private final double angle;
	private final double scale;

	public Decal(Vec3d origin, EnumFacing orientation, double angle, double scale)
	{
		this.origin = origin;
		this.orientation = orientation;
		this.angle = angle;
		this.scale = scale;
	}

	public Vec3d getOrigin()
	{
		return origin;
	}

	public EnumFacing getOrientation()
	{
		return orientation;
	}

	public double getAngle()
	{
		return angle;
	}

	public double getScale()
	{
		return scale;
	}

	public static Decal fromBytes(ByteBuf buf)
	{
		return new Decal(
				new Vec3d(
						buf.readDouble(),
						buf.readDouble(),
						buf.readDouble()
				),
				EnumFacing.VALUES[buf.readByte()],
				buf.readDouble(),
				buf.readDouble()
		);
	}

	public static void toBytes(ByteBuf buf, Decal decal)
	{
		buf.writeDouble(decal.origin.x);
		buf.writeDouble(decal.origin.y);
		buf.writeDouble(decal.origin.z);
		buf.writeByte(decal.orientation.getIndex());
		buf.writeDouble(decal.angle);
		buf.writeDouble(decal.scale);
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("origin", origin)
				.append("orientation", orientation)
				.append("angle", angle)
				.append("scale", scale)
				.build();
	}
}

package com.github.atomicblom.finishingtouch.decals;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Decal
{
	private final Vec3d origin;
	private final EnumFacing orientation;
	private final double angle;
	private final double scale;
	private final EnumDecalType type;
	private final String location;

	public Decal(Vec3d origin, EnumFacing orientation, double angle, double scale, EnumDecalType decalType, String decalLocation)
	{
		this.origin = origin;
		this.orientation = orientation;
		this.angle = angle;
		this.scale = scale;
		this.type = decalType;
		this.location = decalLocation;
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
				buf.readDouble(),
                EnumDecalType.values()[buf.readInt()],
				ByteBufUtils.readUTF8String(buf));
	}

	public static void toBytes(ByteBuf buf, Decal decal)
	{
		buf.writeDouble(decal.origin.x);
		buf.writeDouble(decal.origin.y);
		buf.writeDouble(decal.origin.z);
		buf.writeByte(decal.orientation.getIndex());
		buf.writeDouble(decal.angle);
		buf.writeDouble(decal.scale);
		buf.writeInt(decal.type.ordinal());
		ByteBufUtils.writeUTF8String(buf, decal.location);
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("origin", origin)
				.append("orientation", orientation)
				.append("angle", angle)
				.append("scale", scale)
				.append("type", type)
				.append("location", location)
				.build();
	}

	public String getLocation() {
		return location;
	}

	public EnumDecalType getType() {
		return type;
	}
}

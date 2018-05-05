package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.Reference.NBT;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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

	public static NBTTagCompound asNBT(Decal decal) {
		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble(NBT.DecalOriginX, decal.origin.x);
		nbt.setDouble(NBT.DecalOriginY, decal.origin.y);
		nbt.setDouble(NBT.DecalOriginZ, decal.origin.z);
		nbt.setByte(NBT.DecalOrientation, (byte)decal.orientation.getIndex());
		nbt.setDouble(NBT.DecalAngle, decal.angle);
		nbt.setDouble(NBT.DecalScale, decal.scale);
		nbt.setByte(NBT.DecalType, (byte)decal.type.ordinal());
		nbt.setString(NBT.DecalLocation, decal.location);
		return nbt;
	}

	public static Decal fromNBT(NBTTagCompound nbt) {

		return new Decal(
				new Vec3d(
						nbt.getDouble(NBT.DecalOriginX),
						nbt.getDouble(NBT.DecalOriginY),
						nbt.getDouble(NBT.DecalOriginZ)
				),
				EnumFacing.VALUES[nbt.getByte(NBT.DecalOrientation)],
				nbt.getDouble(NBT.DecalAngle),
				nbt.getDouble(NBT.DecalScale),
				EnumDecalType.values()[nbt.getByte(NBT.DecalType)],
				nbt.getString(NBT.DecalLocation)
		);
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

	public boolean Is(Decal decal)
	{
		return orientation == decal.orientation &&
				origin.x == decal.origin.x &&
				origin.y == decal.origin.y &&
				origin.z == decal.origin.z &&
				angle == decal.angle &&
				scale == decal.scale &&
				location.equals(decal.location);
	}
}

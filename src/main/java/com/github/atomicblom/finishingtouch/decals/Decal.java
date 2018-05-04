package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.Reference;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
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

	public static NBTTagCompound asNBT(Decal decal) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble(Reference.NBT.DecalOriginX, decal.origin.x);
		nbt.setDouble(Reference.NBT.DecalOriginY, decal.origin.y);
		nbt.setDouble(Reference.NBT.DecalOriginZ, decal.origin.z);
		nbt.setByte(Reference.NBT.DecalOrientation, (byte)decal.orientation.getIndex());
		nbt.setDouble(Reference.NBT.DecalAngle, decal.angle);
		nbt.setDouble(Reference.NBT.DecalScale, decal.scale);
		nbt.setByte(Reference.NBT.DecalType, (byte)decal.type.ordinal());
		nbt.setString(Reference.NBT.DecalLocation, decal.location);
		return nbt;
	}

	public static Decal fromNBT(NBTTagCompound nbt) {

		return new Decal(
				new Vec3d(
						nbt.getDouble(Reference.NBT.DecalOriginX),
						nbt.getDouble(Reference.NBT.DecalOriginY),
						nbt.getDouble(Reference.NBT.DecalOriginZ)
				),
				EnumFacing.VALUES[nbt.getByte(Reference.NBT.DecalOrientation)],
				nbt.getDouble(Reference.NBT.DecalAngle),
				nbt.getDouble(Reference.NBT.DecalScale),
				EnumDecalType.values()[nbt.getByte(Reference.NBT.DecalType)],
				nbt.getString(Reference.NBT.DecalLocation)
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
}

package com.github.atomicblom.finishingtouch.decals;

import com.github.atomicblom.finishingtouch.utility.Reference.NBT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Decal
{
	private final Vec3d origin;
	private final Direction orientation;
	private final double angle;
	private final double scale;
	private final EnumDecalType type;
	private final String location;

	public Decal(Vec3d origin, Direction orientation, double angle, double scale, EnumDecalType decalType, String decalLocation)
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

	public Direction getOrientation()
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
		final PacketBuffer packetBuffer = new PacketBuffer(buf);
		return new Decal(
				new Vec3d(
						packetBuffer.readDouble(),
						packetBuffer.readDouble(),
						packetBuffer.readDouble()
				),
				Direction.byIndex(packetBuffer.readByte()),
				packetBuffer.readDouble(),
				packetBuffer.readDouble(),
                EnumDecalType.values()[packetBuffer.readInt()],
				packetBuffer.readString(32767));
	}

	public static void toBytes(ByteBuf buf, Decal decal)
	{
		final PacketBuffer packetBuffer = new PacketBuffer(buf);
		packetBuffer.writeDouble(decal.origin.x);
		packetBuffer.writeDouble(decal.origin.y);
		packetBuffer.writeDouble(decal.origin.z);
		packetBuffer.writeByte(decal.orientation.getIndex());
		packetBuffer.writeDouble(decal.angle);
		packetBuffer.writeDouble(decal.scale);
		packetBuffer.writeInt(decal.type.ordinal());
		packetBuffer.writeString(decal.location);
	}

	public static CompoundNBT asNBT(Decal decal) {
		final CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble(NBT.DecalOriginX, decal.origin.x);
		nbt.putDouble(NBT.DecalOriginY, decal.origin.y);
		nbt.putDouble(NBT.DecalOriginZ, decal.origin.z);
		nbt.putByte(NBT.DecalOrientation, (byte)decal.orientation.getIndex());
		nbt.putDouble(NBT.DecalAngle, decal.angle);
		nbt.putDouble(NBT.DecalScale, decal.scale);
		nbt.putByte(NBT.DecalType, (byte)decal.type.ordinal());
		nbt.putString(NBT.DecalLocation, decal.location);
		return nbt;
	}

	public static Decal fromNBT(CompoundNBT nbt) {

		return new Decal(
				new Vec3d(
						nbt.getDouble(NBT.DecalOriginX),
						nbt.getDouble(NBT.DecalOriginY),
						nbt.getDouble(NBT.DecalOriginZ)
				),
				Direction.byIndex(nbt.getByte(NBT.DecalOrientation)),
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

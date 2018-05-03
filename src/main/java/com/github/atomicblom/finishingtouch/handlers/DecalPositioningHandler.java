package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.ItemLibrary;
import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.decals.EnumDecalType;
import com.github.atomicblom.finishingtouch.network.AddDecalMessage;
import com.github.atomicblom.finishingtouch.utility.PlaneProjection;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public final class DecalPositioningHandler
{
	private static KeyBinding keyBindUseItem = null;
	private static KeyBinding keyBindSneak = null;
	private static EntityPlayerSP player = null;
	private static boolean isPlacing = false;
	private static final Minecraft minecraft = Minecraft.getMinecraft();

	private static EnumFacing orientation = EnumFacing.UP;

	private static Vec3d origin = Vec3d.ZERO;
	private static Vec3d placeReference = Vec3d.ZERO;
	private static double angle = 0;
	private static double scale = 1;
	private static EnumDecalType decalType;
	private static String decalLocation;

	public static boolean isPlacing() {
		return isPlacing && placeReference != null;
	}

	public static EnumFacing getDecalOrientation() {
		return orientation;
	}

	public static Vec3d getOrigin() {
		return origin;
	}

	public static Vec3d getDecalPlaceReference()
	{
		return placeReference;
	}

	public static double getAngle() {
		return angle;
	}

	public static double getScale() {
		return scale;
	}

	@SubscribeEvent
	public static void onRenderTick(RenderTickEvent renderTickEvent) {
		if (renderTickEvent.phase != Phase.START) return;
		if (!canDoDecalCheck()) {
			isPlacing = false;
			return;
		}

		final boolean isCurrentlyHeld = keyBindUseItem.isKeyDown();
		final boolean snap = keyBindSneak.isKeyDown();

		final RayTraceResult objectMouseOver = minecraft.objectMouseOver;

		if (!isPlacing && isCurrentlyHeld && objectMouseOver.typeOfHit == Type.BLOCK) {
			orientation = objectMouseOver.sideHit;
			origin = objectMouseOver.hitVec;
			ItemStack decalWand = player.getHeldItemMainhand();
			NBTTagCompound tagCompound = decalWand.getTagCompound();
			decalLocation = tagCompound.getString(Reference.NBT.DecalLocation);
			decalType = (EnumDecalType.values()[tagCompound.getInteger(Reference.NBT.DecalType)]);

			isPlacing = true;
		}

		if (isPlacing) {
			placeReference = PlaneProjection.calculateProjectedPoint(origin, orientation, player);
			if (isCurrentlyHeld)
			{
				calculateScale();
				calculateAngle(snap);
			} else
			{
				isPlacing = false;
				finalizeDecal();
			}
		}
	}

	private static boolean canDoDecalCheck()
	{
		if (keyBindUseItem == null || keyBindSneak == null || player == null)
		{
			keyBindUseItem = minecraft.gameSettings.keyBindUseItem;
			keyBindSneak = minecraft.gameSettings.keyBindSneak;
			player = minecraft.player;
			if (player == null || keyBindSneak == null || keyBindUseItem == null) return false;
		}

		ItemStack heldItemMainhand = player.getHeldItemMainhand();
		ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
		if (heldItemMainhand.getItem() != ItemLibrary.decal_wand) {
			return false;
		}

		return true;
	}

	private static void calculateAngle(boolean snap)
	{
		final Axis axis = orientation.getAxis();
		switch (axis)
		{
			case X:
				angle = Math.toDegrees(StrictMath.atan2(origin.y - placeReference.y, origin.z - placeReference.z));
				break;
			case Y:
				angle = Math.toDegrees(StrictMath.atan2(origin.x - placeReference.x, origin.z - placeReference.z));
				break;
			case Z:
				angle = Math.toDegrees(StrictMath.atan2(origin.y - placeReference.y, origin.x - placeReference.x));
				break;
		}

		if (snap)
		{
			angle /= 45;
			angle = (int) angle * 45;
		}
	}

	private static void calculateScale()
	{
		scale = MathHelper.sqrt(
				StrictMath.pow(origin.x - placeReference.x, 2) +
						StrictMath.pow(origin.y - placeReference.y, 2) +
						StrictMath.pow(origin.z - placeReference.z, 2)
		) * 2;
	}

	private static void finalizeDecal()
	{
		minecraft.addScheduledTask(
				() -> TheFinishingTouch.CHANNEL.sendToServer(new AddDecalMessage(
						new Decal(
								origin,
								orientation,
								angle,
								scale,
								decalType,
								decalLocation
						)
				)));
	}

	public static void reset()
	{
		player = null;
	}

	public static EnumDecalType getDecalType()
	{
		return decalType;
	}

	public static String getDecalLocation()
	{
		return decalLocation;
	}
}

package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.NonRegistryLibrary;
import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.ItemLibrary;
import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.decals.DecalList;
import com.github.atomicblom.finishingtouch.decals.EnumDecalType;
import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
import com.github.atomicblom.finishingtouch.network.DecalAction;
import com.github.atomicblom.finishingtouch.network.DecalMessage;
import com.github.atomicblom.finishingtouch.utility.PlaneProjection;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import javax.annotation.Nullable;

@EventBusSubscriber(Side.CLIENT)
public final class DecalPositioningHandler
{
	private static EntityPlayerSP player = null;
	private static final Minecraft minecraft = Minecraft.getMinecraft();

	private static boolean isPlacing = false;
	private static boolean isRemoving = false;

	private static EnumFacing orientation = EnumFacing.UP;

	private static Vec3d origin = Vec3d.ZERO;
	private static Vec3d placeReference = Vec3d.ZERO;
	private static double angle = 0;
	private static double scale = 1;
	private static EnumDecalType decalType;
	private static String decalLocation;
	@Nullable
	private static Decal decalToRemove = null;
	private static boolean awaitingFinalRemoveConfirmation = false;


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

	public static Decal getDecalToRemove() {
		return decalToRemove;
	}

	@SubscribeEvent
	public static void onRenderTick(RenderTickEvent renderTickEvent) {
		if (renderTickEvent.phase != Phase.START) return;
		if (!canDoDecalCheck()) {
			isPlacing = false;
			return;
		}

		final RayTraceResult objectMouseOver = minecraft.objectMouseOver;
		final NBTTagCompound wandNBT = player.getHeldItemMainhand().getTagCompound();

		final boolean isRequestingDecalAction = NonRegistryLibrary.keyBindUseItem.isKeyDown();
		final boolean isRequestingRemoveDecal = NonRegistryLibrary.removeDecalBinding.isKeyDown();
		final boolean snap = NonRegistryLibrary.keyBindSneak.isKeyDown();

		if (isRequestingRemoveDecal) {
			decalToRemove = checkHighlightedDecal(objectMouseOver, decalToRemove);
			isRemoving = true;
		} else {
			decalToRemove = null;
			isRemoving = false;
		}

		//Removal has precidence.
		if (isRemoving) {
			if (isRequestingDecalAction)
			{
				awaitingFinalRemoveConfirmation = true;
			} else if (awaitingFinalRemoveConfirmation)
			{
				removeDecal();
				decalToRemove = null;
				isRemoving = false;
				awaitingFinalRemoveConfirmation = false;
			}
		} else {
			final boolean hasDecal = wandNBT != null && !wandNBT.hasNoTags();

			if (!isPlacing && isRequestingDecalAction && objectMouseOver.typeOfHit == Type.BLOCK)
			{
				orientation = objectMouseOver.sideHit;
				origin = objectMouseOver.hitVec;
				if (hasDecal)
				{
					decalLocation = wandNBT.getString(Reference.NBT.DecalLocation);
					decalType = (EnumDecalType.values()[wandNBT.getInteger(Reference.NBT.DecalType)]);
					isPlacing = true;
				} else {

					minecraft.ingameGUI.setOverlayMessage(
							new TextComponentTranslation("gui.finishingtouch:decal_wand.howtoopen"), false
					);
				}
			}

			if (isPlacing)
			{
				placeReference = PlaneProjection.calculateProjectedPoint(origin, orientation, player);
				if (isRequestingDecalAction)
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
	}

	private static Decal checkHighlightedDecal(RayTraceResult objectMouseOver, Decal currentlyHighligtedDecal)
	{
		if (currentlyHighligtedDecal != null && checkDecalHit(objectMouseOver, currentlyHighligtedDecal)) {
			return currentlyHighligtedDecal;
		}

		final ChunkPos chunkPos = new ChunkPos(objectMouseOver.getBlockPos());

		Decal hitDecal = checkDecalsInChunk(chunkPos.x, chunkPos.z, objectMouseOver);
		if (hitDecal != null) return hitDecal;

		for (int z = -1; z <= 1; ++z) {
			for (int x = -1; x <= 1; ++x) {
				if (z == 0 && x == 0) continue;
				hitDecal = checkDecalsInChunk(x, z, objectMouseOver);
				if (hitDecal != null) return hitDecal;
			}
		}

		return hitDecal;
	}

	private static boolean checkDecalHit(RayTraceResult raytrace, Decal decal)
	{
		if (decal.getOrientation() != raytrace.sideHit) return false;
		final Vec3d pointOnPlane = PlaneProjection.calculateProjectedPoint(origin, orientation, player);
//		LogHelper.info("Checking removal of decal: {}", decal);
//		LogHelper.info("Point on plane: {}", pointOnPlane);
		if (pointOnPlane != null) {
			if (pointOnPlane.distanceTo(raytrace.hitVec) < decal.getScale()) {
				return true;
			}
		}
		return false;
	}

	private static Decal checkDecalsInChunk(int x, int z, RayTraceResult raytrace)
	{
		final Chunk chunkFromChunkCoords = player.world.getChunkFromChunkCoords(x, z);
		final DecalList decalsInChunk = ClientDecalStore.getDecalsInChunk(chunkFromChunkCoords);
		if (decalsInChunk == null) return null;

		for (final Decal decal : decalsInChunk.decals)
		{
			if (checkDecalHit(raytrace, decal)) {
				return decal;
			}
		}
		return null;
	}

	private static boolean canDoDecalCheck()
	{
		if (player == null)
		{
			player = minecraft.player;
			if (player == null) return false;
		}

		final ItemStack heldItemMainhand = player.getHeldItemMainhand();
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

		if (scale > 16) {
			scale = 16;
		}
	}

	private static void removeDecal() {
		minecraft.addScheduledTask(
				() -> TheFinishingTouch.CHANNEL.sendToServer(new DecalMessage(
						decalToRemove,
						DecalAction.REMOVING
				)));
	}

	private static void finalizeDecal()
	{
		minecraft.addScheduledTask(
				() -> {
					TheFinishingTouch.CHANNEL.sendToServer(new DecalMessage(
							new Decal(
									origin,
									orientation,
									angle,
									scale,
									decalType,
									decalLocation
							),
							DecalAction.ADDING
					));
				});
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

	public static boolean isRemoving()
	{
		return decalToRemove != null;
	}
}

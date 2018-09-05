package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.DecalList;
import com.github.atomicblom.finishingtouch.decals.EnumDecalType;
import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.fml.common.Mod.*;

@EventBusSubscriber(Side.CLIENT)
public final class WorldRenderHandler
{
	private static final VertexFormat DecalVertexFormat = new VertexFormat()
		.addElement(DefaultVertexFormats.POSITION_3F)
		.addElement(DefaultVertexFormats.TEX_2F)
		.addElement(DefaultVertexFormats.NORMAL_3B)
		.addElement(DefaultVertexFormats.TEX_2S);

	static final RenderHelp[] EnumFacingFixes = {
			new RenderHelp(EnumFacing.DOWN, 0, false, true),
			new RenderHelp(EnumFacing.UP, 0, true, false),
			new RenderHelp(EnumFacing.NORTH, 90, false, false),
			new RenderHelp(EnumFacing.SOUTH, -90, true, true),
			new RenderHelp(EnumFacing.WEST, -90, true, false),
			new RenderHelp(EnumFacing.EAST, 90  , false, true),
	};

	@SubscribeEvent
	public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final EntityPlayerSP player = minecraft.player;
		final float partialTicks = event.getPartialTicks();
		final TextureManager textureManager = minecraft.getTextureManager();

		final double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
		final double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
		final double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

		final Iterable<DecalList> decalsAround = ClientDecalStore.getDecalsAround(player.dimension, player.getPosition());

		GlStateManager.pushAttrib();
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(false);
		GlStateManager.resetColor();
		GlStateManager.enableTexture2D();

		minecraft.entityRenderer.enableLightmap();
		for (final DecalList decalList : decalsAround)
		{
			for (final Decal decal : decalList.decals)
			{
				renderFromDecal(textureManager, player.world, playerX, playerY, playerZ, decal);
			}
		}
		minecraft.entityRenderer.disableLightmap();

		if (DecalPositioningHandler.isRemoving()) {
			GlStateManager.color(1, 0, 0, 0.8f);

			renderFromDecal(textureManager, player.world, playerX, playerY, playerZ, DecalPositioningHandler.getDecalToRemove());
		} else if (DecalPositioningHandler.isPlacing()) {
			final Tessellator tessellator = Tessellator.getInstance();

			final Vec3d origin = DecalPositioningHandler.getOrigin();
			final EnumFacing orientation = DecalPositioningHandler.getDecalOrientation();
			final double angle = DecalPositioningHandler.getAngle();
			final double scale = DecalPositioningHandler.getScale();
			final double decalOffset = -0.01;
			final String location = DecalPositioningHandler.getDecalLocation();
			final EnumDecalType decalRenderType = DecalPositioningHandler.getDecalType();

			final Vec3i normal = orientation.getOpposite().getDirectionVec();
			final double x = origin.x - playerX + normal.getX() * decalOffset;
			final double y = origin.y - playerY + normal.getY() * decalOffset;
			final double z = origin.z - playerZ + normal.getZ() * decalOffset;

			render(textureManager, tessellator, decalRenderType, orientation, angle, scale, normal, x, y, z, -1, location);

			final Vec3d decalPlaceReference = DecalPositioningHandler.getDecalPlaceReference();
			final double minX = origin.x - playerX + normal.getX() * decalOffset;
			final double minY = origin.y - playerY + normal.getY() * decalOffset;
			final double minZ = origin.z - playerZ + normal.getZ() * decalOffset;
			final double maxX = decalPlaceReference.x - playerX + normal.getX() * decalOffset;
			final double maxY = decalPlaceReference.y - playerY + normal.getY() * decalOffset;
			final double maxZ = decalPlaceReference.z - playerZ + normal.getZ() * decalOffset;

			GlStateManager.glLineWidth(2.5f);
			final BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

			bufferbuilder.pos(minX, minY, minZ).color(1, 0, 0, 0.0F).endVertex();
			bufferbuilder.pos(maxX, maxY, maxZ).color(1, 0, 0, 0.0F).endVertex();

			tessellator.draw();
		}

		GlStateManager.depthMask(true);
		GlStateManager.popAttrib();
	}

	private static void renderFromDecal(TextureManager textureManager, IBlockAccess world, double playerX, double playerY, double playerZ, Decal decal)
	{
		if (decal == null) return;

		final Tessellator tessellator = Tessellator.getInstance();
		final EnumDecalType decalRenderType = decal.getType();
		final EnumFacing orientation = decal.getOrientation();
		final Vec3d origin = decal.getOrigin();
		final double angle = decal.getAngle();
		final double scale = decal.getScale();
		final double decalOffset = -0.01;
		final String location = decal.getLocation();

		final Vec3i normal = orientation.getOpposite().getDirectionVec();
		final double x = origin.x - playerX + normal.getX() * decalOffset;
		final double y = origin.y - playerY + normal.getY() * decalOffset;
		final double z = origin.z - playerZ + normal.getZ() * decalOffset;

		final BlockPos pos = new BlockPos(origin);
		final IBlockState blockState = world.getBlockState(pos);
		final int brightness = blockState.getPackedLightmapCoords(world, pos);

		render(textureManager, tessellator, decalRenderType, orientation, angle, scale, normal, x, y, z, brightness, location);
	}

	private static void render(TextureManager textureManager, Tessellator tessellator, EnumDecalType decalRenderType, EnumFacing orientation, double angle, double scale, Vec3i normal, double x, double y, double z, int brightness, String location)
	{
		if (decalRenderType == EnumDecalType.Loose) {
			textureManager.bindTexture(new ResourceLocation(location));
		}

		final RenderHelp enumFixes = EnumFacingFixes[orientation.getIndex()];
		final BufferBuilder bufferbuilder = tessellator.getBuffer();

		bufferbuilder.begin(GL11.GL_QUADS, DecalVertexFormat);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		if (enumFixes.invertedRotation) {
			angle = -angle;
		}

		final Axis axis = orientation.getAxis();
		GlStateManager.rotate((float)( angle - 45 - enumFixes.rotation), normal.getX(), normal.getY(), normal.getZ());
		switch (axis) {
			case X:
				GlStateManager.rotate(90, 0, 1, 0);
				break;
			case Y:
				GlStateManager.rotate(90, 1, 0, 0);
				break;
		}

		if (enumFixes.flipTexture) {
			GlStateManager.rotate(180, 0, 1, 0);
		}
		GlStateManager.scale(scale, scale, scale);

		final int brightnessY = brightness & 0xFFFF;
		final int brightnessX = (brightness >> 16) & 0xFFFF;
		bufferbuilder.pos(0.5, 0.5, 0).tex(1, 1).normal(normal.getX(), normal.getY(), normal.getZ()).lightmap(brightnessX, brightnessY).endVertex();
		bufferbuilder.pos(0.5, -0.5, 0).tex(1, 0).normal(normal.getX(), normal.getY(), normal.getZ()).lightmap(brightnessX, brightnessY).endVertex();
		bufferbuilder.pos(-0.5, -0.5, 0).tex(0, 0).normal(normal.getX(), normal.getY(), normal.getZ()).lightmap(brightnessX, brightnessY).endVertex();
		bufferbuilder.pos(-0.5, 0.5, 0).tex(0, 1).normal(normal.getX(), normal.getY(), normal.getZ()).lightmap(brightnessX, brightnessY).endVertex();

		tessellator.draw();

		GlStateManager.popMatrix();
	}
}

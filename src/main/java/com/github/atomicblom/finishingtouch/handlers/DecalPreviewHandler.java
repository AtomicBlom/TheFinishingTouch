package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.EnumDecalType;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(Side.CLIENT)
public final class DecalPreviewHandler
{
	private static final RenderHelp[] EnumFacingFixes = {
			new RenderHelp(EnumFacing.DOWN, -90, true, true),
			new RenderHelp(EnumFacing.UP, -90, false, false),
			new RenderHelp(EnumFacing.NORTH, 180, true, false),
			new RenderHelp(EnumFacing.SOUTH, 0, false, true),
			new RenderHelp(EnumFacing.WEST, 0, false, false),
			new RenderHelp(EnumFacing.EAST, 180, true, true),
	};

	@SubscribeEvent
	public static void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
		EntityPlayer player = event.getPlayer();
		float partialTicks = event.getPartialTicks();
		double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
		double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
		double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

		if (DecalPositioningHandler.isRemoving())
		{
			event.setCanceled(true);

			final Decal decalToRemove = DecalPositioningHandler.getDecalToRemove();
			if (decalToRemove == null) return;

			final Vec3d origin = decalToRemove.getOrigin();
			final EnumFacing orientation = decalToRemove.getOrientation();
			final Vec3i normal = orientation.getDirectionVec();
			double angle = decalToRemove.getAngle();
			final double scale = decalToRemove.getScale();
			final RenderHelp enumFixes = EnumFacingFixes[orientation.getIndex()];
			final double decalOffset = 0.02;

			if (DecalPositioningHandler.getDecalType() == EnumDecalType.Loose) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(DecalPositioningHandler.getDecalLocation()));
			}

			final double minX = origin.x - playerX + normal.getX() * decalOffset;
			final double minY = origin.y - playerY + normal.getY() * decalOffset;
			final double minZ = origin.z - playerZ + normal.getZ() * decalOffset;

			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			GlStateManager.translate(minX, minY, minZ);

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.color(1, 0, 0, 0.8f);
			GlStateManager.enableTexture2D();

			if (enumFixes.invertedRotation) {
				angle = -angle;
			}

			final Axis axis = orientation.getAxis();
			GlStateManager.rotate((float)(angle - 45 - enumFixes.rotation), normal.getX(), normal.getY(), normal.getZ());
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

			bufferbuilder.pos(0.5, 0.5, 0).tex(1, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(0.5, -0.5, 0).tex(1, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, -0.5, 0).tex(0, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, 0.5, 0).tex(0, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();

			tessellator.draw();
			GlStateManager.disableBlend();
			GlStateManager.color(1, 1, 1, 1f);
			GlStateManager.disableTexture2D();
			GlStateManager.popMatrix();
			GlStateManager.popAttrib();

		} else if (DecalPositioningHandler.isPlacing()) {
			event.setCanceled(true);

			final Minecraft minecraft = Minecraft.getMinecraft();
			final TextureManager textureManager = minecraft.getTextureManager();

			final EnumFacing orientation = DecalPositioningHandler.getDecalOrientation();
			final RenderHelp enumFixes = EnumFacingFixes[orientation.getIndex()];

			final Vec3d origin = DecalPositioningHandler.getOrigin();
			final Vec3d placeReferencePoint = DecalPositioningHandler.getDecalPlaceReference();
			double angle = DecalPositioningHandler.getAngle();
			final double scale = DecalPositioningHandler.getScale();

			final Vec3i normal = orientation.getDirectionVec();
			final double decalOffset = 0.01;

			final double minX = origin.x - playerX + normal.getX() * decalOffset;
			final double minY = origin.y - playerY + normal.getY() * decalOffset;
			final double minZ = origin.z - playerZ + normal.getZ() * decalOffset;
			final double maxX = placeReferencePoint.x - playerX + normal.getX() * decalOffset;
			final double maxY = placeReferencePoint.y - playerY + normal.getY() * decalOffset;
			final double maxZ = placeReferencePoint.z - playerZ + normal.getZ() * decalOffset;

			if (DecalPositioningHandler.getDecalType() == EnumDecalType.Loose) {
				textureManager.bindTexture(new ResourceLocation(DecalPositioningHandler.getDecalLocation()));
			}

			GlStateManager.pushAttrib();
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.depthMask(true);
			GlStateManager.glLineWidth(2.5f);

			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

			bufferbuilder.pos(minX, minY, minZ).color(1, 0, 0, 0.0F).endVertex();
			bufferbuilder.pos(maxX, maxY, maxZ).color(1, 0, 0, 0.0F).endVertex();

			tessellator.draw();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

			GlStateManager.enableTexture2D();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

			GlStateManager.pushMatrix();
			GlStateManager.translate(minX, minY, minZ);

			if (enumFixes.invertedRotation) {
				angle = -angle;
			}

			final Axis axis = orientation.getAxis();
			GlStateManager.rotate((float)(angle - 45 - enumFixes.rotation), normal.getX(), normal.getY(), normal.getZ());
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

			bufferbuilder.pos(0.5, 0.5, 0).tex(1, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(0.5, -0.5, 0).tex(1, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, -0.5, 0).tex(0, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, 0.5, 0).tex(0, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();

			tessellator.draw();

			GlStateManager.popMatrix();
			GlStateManager.popAttrib();
		}
	}

	public static class RenderHelp {
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
}

package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.EnumDecalType;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(Dist.CLIENT)
public final class DecalPreviewHandler
{
	private static final RenderHelp[] DirectionFixes = {
			new RenderHelp(Direction.DOWN, -90, true, true),
			new RenderHelp(Direction.UP, -90, false, false),
			new RenderHelp(Direction.NORTH, 180, true, false),
			new RenderHelp(Direction.SOUTH, 0, false, true),
			new RenderHelp(Direction.WEST, 0, false, false),
			new RenderHelp(Direction.EAST, 180, true, true),
	};

	@SubscribeEvent
	public static void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
		PlayerEntity player = Minecraft.getInstance().player;
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
			final Direction orientation = decalToRemove.getOrientation();
			final Vec3i normal = orientation.getDirectionVec();
			double angle = decalToRemove.getAngle();
			final double scale = decalToRemove.getScale();
			final RenderHelp enumFixes = DirectionFixes[orientation.getIndex()];
			final double decalOffset = 0.04;

			if (DecalPositioningHandler.getDecalType() == EnumDecalType.Loose) {
				Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(DecalPositioningHandler.getDecalLocation()));
			}

			final double minX = origin.x - playerX + normal.getX() * decalOffset;
			final double minY = origin.y - playerY + normal.getY() * decalOffset - player.getEyeHeight();
			final double minZ = origin.z - playerZ + normal.getZ() * decalOffset;

			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

			GlStateManager.pushMatrix();
			GlStateManager.pushLightingAttributes();
			GlStateManager.translated(minX, minY, minZ);

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.color4f(1, 0, 0, 0.8f);
			GlStateManager.enableTexture();

			if (enumFixes.invertedRotation) {
				angle = -angle;
			}

			final Axis axis = orientation.getAxis();
			GlStateManager.rotatef((float)(angle - 45 - enumFixes.rotation), normal.getX(), normal.getY(), normal.getZ());
			switch (axis) {
				case X:
					GlStateManager.rotatef(90, 0, 1, 0);
					break;
				case Y:
					GlStateManager.rotatef(90, 1, 0, 0);
					break;
			}
			if (enumFixes.flipTexture) {
				GlStateManager.rotatef(180, 0, 1, 0);
			}
			GlStateManager.scaled(scale, scale, scale);

			bufferbuilder.pos(0.5, 0.5, 0).tex(1, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(0.5, -0.5, 0).tex(1, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, -0.5, 0).tex(0, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, 0.5, 0).tex(0, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();

			tessellator.draw();
			//GlStateManager.disableBlend();
			//GlStateManager.color(1, 1, 1, 1f);
			//GlStateManager.disableTexture2D();
			GlStateManager.popMatrix();
			GlStateManager.popAttributes();

		} else if (DecalPositioningHandler.isPlacing()) {
			event.setCanceled(true);

			final Minecraft minecraft = Minecraft.getInstance();
			final TextureManager textureManager = minecraft.getTextureManager();

			final Direction orientation = DecalPositioningHandler.getDecalOrientation();
			final RenderHelp enumFixes = DirectionFixes[orientation.getIndex()];

			final Vec3d origin = DecalPositioningHandler.getOrigin();
			final Vec3d placeReferencePoint = DecalPositioningHandler.getDecalPlaceReference();
			double angle = DecalPositioningHandler.getAngle();
			final double scale = DecalPositioningHandler.getScale();

			final Vec3i normal = orientation.getDirectionVec();
			final double decalOffset = 0.01;

			final double minX = origin.x - playerX + normal.getX() * decalOffset;
			final double minY = origin.y - playerY + normal.getY() * decalOffset - player.getEyeHeight();
			final double minZ = origin.z - playerZ + normal.getZ() * decalOffset;
			final double maxX = placeReferencePoint.x - playerX + normal.getX() * decalOffset;
			final double maxY = placeReferencePoint.y - playerY + normal.getY() * decalOffset - player.getEyeHeight();
			final double maxZ = placeReferencePoint.z - playerZ + normal.getZ() * decalOffset;

			if (DecalPositioningHandler.getDecalType() == EnumDecalType.Loose) {
				textureManager.bindTexture(new ResourceLocation(DecalPositioningHandler.getDecalLocation()));
			}

			GlStateManager.pushLightingAttributes();
			GlStateManager.disableTexture();
			GlStateManager.enableBlend();
			GlStateManager.depthMask(true);
			GlStateManager.lineWidth(2.5f);

			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

			bufferbuilder.pos(minX, minY, minZ).color(1, 0, 0, 0.0F).endVertex();
			bufferbuilder.pos(maxX, maxY, maxZ).color(1, 0, 0, 0.0F).endVertex();

			tessellator.draw();

			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableTexture();

			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

			GlStateManager.pushMatrix();
			GlStateManager.translated(minX, minY, minZ);

			if (enumFixes.invertedRotation) {
				angle = -angle;
			}

			final Axis axis = orientation.getAxis();
			GlStateManager.rotatef((float)(angle - 45 - enumFixes.rotation), normal.getX(), normal.getY(), normal.getZ());
			switch (axis) {
				case X:
					GlStateManager.rotatef(90, 0, 1, 0);
					break;
				case Y:
					GlStateManager.rotatef(90, 1, 0, 0);
					break;
			}
			if (enumFixes.flipTexture) {
				GlStateManager.rotatef(180, 0, 1, 0);
			}
			GlStateManager.scaled(scale, scale, scale);

			bufferbuilder.pos(0.5, 0.5, 0).tex(1, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(0.5, -0.5, 0).tex(1, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, -0.5, 0).tex(0, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			bufferbuilder.pos(-0.5, 0.5, 0).tex(0, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();

			tessellator.draw();

			GlStateManager.popMatrix();
			GlStateManager.popAttributes();
		}
	}

}

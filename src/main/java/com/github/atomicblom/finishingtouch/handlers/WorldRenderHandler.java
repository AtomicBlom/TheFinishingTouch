package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.DecalList;
import com.github.atomicblom.finishingtouch.decals.EnumDecalType;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public final class WorldRenderHandler
{
	@SubscribeEvent
	public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientPlayerEntity player = minecraft.player;
		final float partialTicks = event.getPartialTicks();

		final double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
		final double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
		final double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

		final Iterable<DecalList> decalsAround = ClientDecalStore.getDecalsAround(player.dimension.getId(), player.getPosition());

		GlStateManager.pushLightingAttributes();
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthMask(false);
		GlStateManager.clearCurrentColor();
		GlStateManager.enableTexture();

		final RenderHelp[] DirectionFixes = {
				new RenderHelp(Direction.DOWN, 0, false, true),
				new RenderHelp(Direction.UP, 0, true, false),
				new RenderHelp(Direction.NORTH, 90, false, false),
				new RenderHelp(Direction.SOUTH, -90, true, true),
				new RenderHelp(Direction.WEST, -90, true, false),
				new RenderHelp(Direction.EAST, 90  , false, true),
		};

		for (final DecalList decalList : decalsAround)
		{
			for (final Decal decal : decalList.decals)
			{
				if (decal.getType() == EnumDecalType.Loose) {
					minecraft.getTextureManager().bindTexture(new ResourceLocation(decal.getLocation()));
				}

				final Direction orientation = decal.getOrientation();
				final RenderHelp enumFixes = DirectionFixes[orientation.getIndex()];

				final Vec3d origin = decal.getOrigin();
				double angle = decal.getAngle();
				final double scale = decal.getScale();

				final Vec3i normal = orientation.getOpposite().getDirectionVec();
				final double decalOffset = -0.01;

				final Tessellator tessellator = Tessellator.getInstance();
				final BufferBuilder bufferbuilder = tessellator.getBuffer();

				final double minX = origin.x - playerX + normal.getX() * decalOffset;
				final double minY = origin.y - playerY + normal.getY() * decalOffset;
				final double minZ = origin.z - playerZ + normal.getZ() * decalOffset;


				bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

				GlStateManager.pushMatrix();
				GlStateManager.translated(minX, minY, minZ);

				if (enumFixes.invertedRotation) {
					angle = -angle;
				}

				final Axis axis = orientation.getAxis();
				GlStateManager.rotatef((float)( angle - 45 - enumFixes.rotation), normal.getX(), normal.getY(), normal.getZ());
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

			}
		}

		GlStateManager.popAttributes();
	}
}

package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.decals.Decal;
import com.github.atomicblom.finishingtouch.decals.DecalList;
import com.github.atomicblom.finishingtouch.decals.EnumDecalType;
import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.fml.common.Mod.*;

@EventBusSubscriber(Side.CLIENT)
public final class WorldRenderHandler
{
	@SubscribeEvent
	public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final EntityPlayerSP player = minecraft.player;
		final float partialTicks = event.getPartialTicks();

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

		final RenderHelp[] EnumFacingFixes = {
				new RenderHelp(EnumFacing.DOWN, 0, false, true),
				new RenderHelp(EnumFacing.UP, 0, true, false),
				new RenderHelp(EnumFacing.NORTH, 90, false, false),
				new RenderHelp(EnumFacing.SOUTH, -90, true, true),
				new RenderHelp(EnumFacing.WEST, -90, true, false),
				new RenderHelp(EnumFacing.EAST, 90  , false, true),
		};

		for (final DecalList decalList : decalsAround)
		{
			for (final Decal decal : decalList.decals)
			{
				if (decal.getType() == EnumDecalType.Loose) {
					minecraft.getTextureManager().bindTexture(new ResourceLocation(decal.getLocation()));
				}

				final EnumFacing orientation = decal.getOrientation();
				final RenderHelp enumFixes = EnumFacingFixes[orientation.getIndex()];

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
				GlStateManager.translate(minX, minY, minZ);

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

				bufferbuilder.pos(0.5, 0.5, 0).tex(1, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
				bufferbuilder.pos(0.5, -0.5, 0).tex(1, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
				bufferbuilder.pos(-0.5, -0.5, 0).tex(0, 0).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
				bufferbuilder.pos(-0.5, 0.5, 0).tex(0, 1).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();

				tessellator.draw();

				GlStateManager.popMatrix();

			}
		}

		GlStateManager.popAttrib();
	}
}

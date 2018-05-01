package com.github.atomicblom.finishingtouch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

class LooseTextureRenderableSlotType extends RenderableSlotTypeBase {

    private final ResourceLocation resourceLocation;
    private int zLevel = 0;

    public LooseTextureRenderableSlotType(ResourceLocation resourceLocation)
    {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public void render(int renderX, int renderY)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        if (!isTextureSizeKnown()) {
            int width = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int height = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
            setTextureSize(width, height);
        }

        drawTexturedModalRect(renderX, renderY, 16, 16,0, 0, 1, 1);
    }

    /**
     * Draws a texture rectangle using the texture currently bound to the TextureManager
     */
    private void drawTexturedModalRect(int xCoord, int yCoord, int widthIn, int heightIn, float minU, float minV, int maxU, int maxV)
    {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((xCoord + 0), (yCoord + heightIn), zLevel).tex(minU, maxV).endVertex();
        bufferbuilder.pos((xCoord + widthIn), (yCoord + heightIn), zLevel).tex(maxU, maxV).endVertex();
        bufferbuilder.pos((xCoord + widthIn), (yCoord + 0), zLevel).tex(maxU, minV).endVertex();
        bufferbuilder.pos((xCoord + 0), (yCoord + 0), zLevel).tex(minU, minV).endVertex();
        tessellator.draw();
    }
}

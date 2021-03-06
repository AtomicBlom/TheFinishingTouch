package com.github.atomicblom.finishingtouch.gui;

import com.github.atomicblom.finishingtouch.decals.EnumDecalType;

abstract class RenderableSlotTypeBase
{
    private boolean shouldRender = false;

    private int textureWidth = -1;
    private int textureHeight = -1;
    boolean isTextureSizeKnown = false;

    public boolean isAvailable()
    {
        return true;
    }

    abstract void render(int renderX, int renderY, int width, int height);

    protected void setTextureSize(int width, int height)
    {
        this.textureWidth = width;
        this.textureHeight = height;
        this.isTextureSizeKnown = true;
    }

    public boolean isTextureSizeKnown()
    {
        return isTextureSizeKnown;
    }

    public int getTextureWidth()
    {
        return textureWidth;
    }

    public int getTextureHeight()
    {
        return textureHeight;
    }

	public abstract EnumDecalType getType();

    public abstract String getTextureLocation();
}

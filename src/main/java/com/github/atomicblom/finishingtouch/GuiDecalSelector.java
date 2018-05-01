package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.model.Artist;
import com.github.atomicblom.finishingtouch.utility.Reference;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiDecalSelector extends GuiScreen {

    private static final ResourceLocation DECAL_SELECTOR_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/decal_selector.png");

    /** The X size of the inventory window in pixels. */
    protected int xSize = 234;
    /** The Y size of the inventory window in pixels. */
    protected int ySize = 140;

    protected int guiLeft;
    protected int guiTop;
    private List<RenderableSlot> builtinDecals;

    private int page = 0;

    final int itemsPerRow = 9;
    final int itemsPerColumn = 5;
    final int itemsPerPage = itemsPerRow * itemsPerColumn;
    final int iconWidth = 16;
    final int iconHeight = 16;
    final int iconPadding = 2;

    final int slotOffsetX = 66;
    final int slotOffsetY = 36;

    private RenderableSlot selectedDecal;

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;

        final IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        final ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "textures/decals/decals.json");
        try (final IResource resource = resourceManager.getResource(location))
        {
            final Reader reader = new InputStreamReader(resource.getInputStream());
            final Gson gson = new Gson();

            builtinDecals = Arrays.stream(gson.fromJson(reader, Artist[].class)).map(a -> Arrays.stream(a.getDecals()).map(d -> {
                RenderableSlot renderableSlot = new RenderableSlot();
                renderableSlot.authorName = a.getName();
                renderableSlot.authorUrl = a.getSite();
                renderableSlot.authorSiteName = a.getSiteName();
                renderableSlot.decalName = d.getName();
                String decalLocation = d.getLocation();
                if (!decalLocation.endsWith(".png")) {
                    decalLocation = decalLocation + ".png";
                }

                if (decalLocation.startsWith("http://") || decalLocation.startsWith("https://")) {
                    //Community renderer
                } else if (false) {
                    //Stitched texture
                } else {
                    renderableSlot.renderableSlotType = new LooseTextureRenderableSlotType(new ResourceLocation(decalLocation));
                }

                return renderableSlot;
            })).flatMap(s -> s).collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final int i = guiLeft;
        final int j = guiTop;
        drawGuiContainerBackgroundLayer();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(i, j, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        page = 0;

        final int itemsPerPage = itemsPerRow * itemsPerColumn;
        int startItem = page * itemsPerPage;
        final List<RenderableSlot> decalList = this.builtinDecals;
        final int totalDecals = decalList.size();
        if (startItem > totalDecals) {
            startItem = totalDecals - itemsPerPage;
        }
        if (startItem < 0) {
            startItem = 0;
        }

        final int adjustedMouseX = mouseX - guiLeft - slotOffsetX;
        final int adjustedMouseY = mouseY - guiTop - slotOffsetY;

        int mouseSlotX = adjustedMouseX / (iconWidth + iconPadding);
        int mouseSlotY = adjustedMouseY / (iconHeight + iconPadding);
        if (adjustedMouseX < 0 || adjustedMouseY < 0) {
            mouseSlotX = -1;
            mouseSlotY = -1;
        } else if (adjustedMouseX % (iconWidth + iconPadding) >= iconWidth || adjustedMouseY % (iconHeight + iconPadding) >= iconHeight) {
            mouseSlotX = -1;
            mouseSlotY = -1;
        }

        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();

        int currentItem = startItem;
        for (int y = 0; y < itemsPerRow; ++y) {
            for (int x = 0; x < itemsPerColumn; ++x) {
                if (currentItem < totalDecals) {
                    final RenderableSlot renderableSlot = decalList.get(currentItem);
                    final int renderX = slotOffsetX + x * (iconWidth + iconPadding);
                    final int renderY = slotOffsetY + y * (iconHeight + iconPadding);
                    renderableSlot.renderableSlotType.render(renderX, renderY);
                }
                currentItem++;
            }
        }

        final int hoveredItem = (page*itemsPerPage) + mouseSlotY * itemsPerRow + mouseSlotX;

        if (mouseSlotX >= 0 && mouseSlotY >= 0 && mouseSlotX < itemsPerRow && mouseSlotY < itemsPerColumn && hoveredItem < totalDecals) {

            final int renderX = slotOffsetX + mouseSlotX * (iconWidth + iconPadding);
            final int renderY = slotOffsetY + mouseSlotY * (iconHeight + iconPadding);

            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            drawGradientRect(renderX, renderY, renderX + 16, renderY + 16, 0x80FFFFFF, 0x80FFFFFF);
            GlStateManager.colorMask(true, true, true, false);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }

        RenderHelper.disableStandardItemLighting();
        drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    private void drawGuiContainerBackgroundLayer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(DECAL_SELECTOR_TEXTURE);
        final int i = (width - xSize) / 2;
        final int j = (height - ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (selectedDecal != null) {
            final String s = "Artist: " + selectedDecal.authorName;
            this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
            this.fontRenderer.drawString(selectedDecal.decalName, 4, 74, 4210752);
            RenderableSlotTypeBase renderableSlotType = selectedDecal.renderableSlotType;
            if (renderableSlotType.isTextureSizeKnown()) {
                String textureSize = renderableSlotType.getTextureWidth() + "x" + renderableSlotType.getTextureHeight();
                int textureSizeWidth = fontRenderer.getStringWidth(textureSize);
                this.fontRenderer.drawString(textureSize, 59-textureSizeWidth, 61, 4210752);
            }
        }

    }



    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0) {
            final List<RenderableSlot> decalList = this.builtinDecals;
            final int totalDecals = decalList.size();

            final int slotOffsetX = 66;
            final int slotOffsetY = 36;
            final int adjustedMouseX = mouseX - guiLeft - slotOffsetX;
            final int adjustedMouseY = mouseY - guiTop - slotOffsetY;

            int mouseSlotX = adjustedMouseX / (iconWidth + iconPadding);
            int mouseSlotY = adjustedMouseY / (iconHeight + iconPadding);
            if (adjustedMouseX < 0 || adjustedMouseY < 0) {
                mouseSlotX = -1;
                mouseSlotY = -1;
            } else if (adjustedMouseX % (iconWidth + iconPadding) >= iconWidth || adjustedMouseY % (iconHeight + iconPadding) >= iconHeight) {
                mouseSlotX = -1;
                mouseSlotY = -1;
            }

            final int selectedItem = (page*itemsPerPage) + mouseSlotY * itemsPerRow + mouseSlotX;

            if (mouseSlotX >= 0 && mouseSlotY >= 0 && mouseSlotX < itemsPerRow && mouseSlotY < itemsPerColumn && selectedItem < totalDecals)
            {
                selectedDecal = decalList.get(selectedItem);
            }
        }
    }
}

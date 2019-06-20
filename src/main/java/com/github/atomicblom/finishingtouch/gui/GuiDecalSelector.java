package com.github.atomicblom.finishingtouch.gui;

import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.model.Artist;
import com.github.atomicblom.finishingtouch.network.SetWandDecalMessage;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.utility.Reference;
import com.github.atomicblom.finishingtouch.utility.Reference.NBT;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiDecalSelector extends Screen {

    private static final ResourceLocation DECAL_SELECTOR_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/decal_selector.png");
    private final ItemStack itemBeingEdited;

    /** The X size of the inventory window in pixels. */
    private static final int xSize = 252;
    /** The Y size of the inventory window in pixels. */
    private static final int ySize = 134;

    private int guiLeft;
    private int guiTop;
    private List<RenderableSlot> builtinDecals = null;

    private static final int page = 0;

    private static final int itemsPerRow = 9;
    private static final int itemsPerColumn = 5;
    private static final int itemsPerPage = itemsPerRow * itemsPerColumn;
    private static final int iconWidth = 16;
    private static final int iconHeight = 16;
    private static final int iconPadding = 2;

    private static final int slotOffsetX = 84;
    private static final int slotOffsetY = 30;

    private RenderableSlot selectedDecal = null;
    private List<RenderableSlot> visibleDecalList = null;

    public GuiDecalSelector(ItemStack itemBeingEdited) {
        super(new TranslationTextComponent("decalselector"));
        this.itemBeingEdited = itemBeingEdited;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void init()
    {
        super.init();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;

        CompoundNBT tagCompound = itemBeingEdited.getOrCreateTag();
        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
            itemBeingEdited.setTag(tagCompound);
        }
        final String selectedDecalLocation = tagCompound.getString(NBT.DecalLocation);

        final IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        final ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "textures/decals/decals.json");
        try (final IResource resource = resourceManager.getResource(location);
             final Reader reader = new InputStreamReader(resource.getInputStream()))
        {
            final Gson gson = new Gson();

            builtinDecals = Arrays.stream(gson.fromJson(reader, Artist[].class)).map(a -> Arrays.stream(a.getDecals()).map(d -> {
                RenderableSlot renderableSlot = new RenderableSlot();
                renderableSlot.authorName = a.getName();
                renderableSlot.authorUrl = a.getSite();
                renderableSlot.authorSiteName = a.getSiteName();
                renderableSlot.decalName = d.getName();
                String decalLocation = d.getLocation();
                if (!decalLocation.endsWith(".png")) {
                    decalLocation += ".png";
                }

                if (decalLocation.startsWith("http://") || decalLocation.startsWith("https://")) {
                    //Community renderer
                } else if (false) {
                    //Stitched texture
                } else {
                    renderableSlot.renderableSlotType = new LooseTextureRenderableSlotType(new ResourceLocation(decalLocation));
                }

                if (renderableSlot.renderableSlotType.getTextureLocation().equals(selectedDecalLocation)) {
                    selectedDecal = renderableSlot;
                }

                return renderableSlot;
            })).flatMap(items -> items).collect(Collectors.toList());
            visibleDecalList = builtinDecals;
        } catch (final IOException e) {
            LogHelper.error(e.toString());
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawGuiContainerBackgroundLayer();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        super.render(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(guiLeft, guiTop, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();

        //FIXME: Reenable this!
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();

        drawDecalPage(visibleDecalList);
        drawPreviewDecal(selectedDecal);

        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.colorMask(true, true, true, false);

        drawHoverSelection(mouseX, mouseY);

        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();

        RenderHelper.disableStandardItemLighting();
        drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        RenderHelper.enableStandardItemLighting();
    }

    private void drawPreviewDecal(RenderableSlot selectedDecal) {
        if (selectedDecal != null) {
            final RenderableSlotTypeBase renderableSlot = selectedDecal.renderableSlotType;
            renderableSlot.render(10, 18, 64, 64);
        }
    }

    private void drawHoverSelection(int mouseX, int mouseY) {
        final int mouseSlotX = getSlotFromMouseX(mouseX);
        final int mouseSlotY = getSlotFromMouseY(mouseY);
        final int hoveredItem = (int)((page*itemsPerPage) + mouseSlotY * itemsPerRow + mouseSlotX);

        if (mouseSlotX >= 0 && mouseSlotY >= 0 && mouseSlotX < itemsPerRow && mouseSlotY < itemsPerColumn && hoveredItem < visibleDecalList.size()) {
            final int renderX = (int)(slotOffsetX + mouseSlotX * (iconWidth + iconPadding));
            final int renderY = (int)(slotOffsetY + mouseSlotY * (iconHeight + iconPadding));

            fillGradient(renderX, renderY, renderX + 16, renderY + 16, 0x80FFFFFF, 0x80FFFFFF);
        }
    }

    private int drawDecalPage(List<RenderableSlot> decalList) {
        int currentItem = getDecalPageStart(decalList);
        final int totalDecals = decalList.size();
        for (int y = 0; y < itemsPerRow; ++y) {
            for (int x = 0; x < itemsPerColumn; ++x) {
                if (currentItem < totalDecals) {
                    final RenderableSlot renderableSlot = decalList.get(currentItem);
                    final int renderX = slotOffsetX + x * (iconWidth + iconPadding);
                    final int renderY = slotOffsetY + y * (iconHeight + iconPadding);
                    renderableSlot.renderableSlotType.render(renderX, renderY, 16, 16);
                }
                currentItem++;
            }
        }
        return totalDecals;
    }

    private int getDecalPageStart(List<RenderableSlot> decalList) {
        final int totalDecals = decalList.size();
        int startItem = page * itemsPerPage;
        if (startItem > totalDecals) {
            startItem = totalDecals - itemsPerPage;
        }
        if (startItem < 0) {
            startItem = 0;
        }
        return startItem;
    }

    private void drawGuiContainerBackgroundLayer() {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(DECAL_SELECTOR_TEXTURE);
        final int i = (width - xSize) / 2;
        final int j = (height - ySize) / 2;
        blit(i, j, 0, 0, xSize, ySize);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bindTexture(DECAL_SELECTOR_TEXTURE);
        blit(82, 17, 0, 134, 138, 12);
        blit(140, 11, 138, 134, 6, 13);
        blit(137, 119, 144, 134, 6, 13);
        blit(185, 119, 150, 134, 6, 13);

        if (selectedDecal != null) {
            float scale;

            final String authorName = selectedDecal.authorName;
            final String decalName = selectedDecal.decalName;
            final String siteName = "§9" + selectedDecal.authorSiteName;

            GlStateManager.pushMatrix();
            scale = font.getStringWidth(decalName) > 80 ? 0.5f : 1f;
            GlStateManager.scalef(scale, scale, scale);
            font.drawString(decalName, (int)(8 * (1/scale)), (int)(92 * (1/scale)), 4210752);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            scale = font.getStringWidth(authorName) > 80 ? 0.5f : 1f;
            GlStateManager.scalef(scale, scale, scale);
            font.drawString(authorName, (int)(8 * (1/scale)), (int)(102 * (1/scale)), 4210752);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            scale = font.getStringWidth(siteName) > 80 ? 0.5f : 1f;
            GlStateManager.scalef(scale, scale, scale);
            font.drawString(siteName, (int)(8 * (1/scale)), (int)(112 * (1/scale)), 0xFFFFFFFF);
            GlStateManager.popMatrix();

            final RenderableSlotTypeBase renderableSlotType = selectedDecal.renderableSlotType;
            if (renderableSlotType.isTextureSizeKnown()) {
                final String textureSize = renderableSlotType.getTextureWidth() + "x" + renderableSlotType.getTextureHeight();
                final int textureSizeWidth = font.getStringWidth(textureSize);
                font.drawString(textureSize, 78-textureSizeWidth, 80, 0xFFFFFFFF);
            }
        }

        font.drawString(I18n.format("gui."+Reference.MOD_ID + ".decal_selector.included"), 86, 20, 0xFFFFFFFF);
        font.drawString(I18n.format("gui."+Reference.MOD_ID + ".decal_selector.community"), 158, 20, 0xFF808080);
        font.drawString(I18n.format("gui."+Reference.MOD_ID + ".decal_selector.decallibrary"), 7, 6, 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        final double adjustedMouseX = mouseX - guiLeft;
        final double adjustedMouseY = mouseY - guiTop;
        if (mouseButton == 0) {
            if (trySelectDecal(mouseX, mouseY)) return true;
            if (trySelectArtistLink(adjustedMouseX, adjustedMouseY)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean trySelectArtistLink(double mouseX, double mouseY)
    {
        if (selectedDecal == null || Strings.isNullOrEmpty(selectedDecal.authorUrl)) return false;
        if (mouseX < 7 || mouseX > 78) return false;
        if (mouseY < 109 || mouseY > 117) return false;

        if (minecraft.gameSettings.chatLinksPrompt)
        {
            minecraft.displayGuiScreen(new ConfirmOpenLinkScreen((trust) -> {
                if (trust)
                {
                    openAuthorUrl();
                }

                minecraft.displayGuiScreen(this);
            }, selectedDecal.authorSiteName, false));
        }
        else
        {
            openAuthorUrl();
        }
        return true;
    }

    private void openAuthorUrl()
    {
        try
        {
            final URI uri = new URI(selectedDecal.authorUrl);
            final Class<?> oclass = Class.forName("java.awt.Desktop");
            final Object object = oclass.getMethod("getDesktop").invoke(null);
            oclass.getMethod("browse", URI.class).invoke(object, uri);
        }
        catch (final Throwable throwable1)
        {
            final Throwable throwable = throwable1.getCause();
            LogHelper.error("Couldn't open link: {}", (Object)(throwable == null ? "<UNKNOWN>" : throwable.getMessage()));
        }
    }

    private boolean trySelectDecal(double mouseX, double mouseY)
    {
        final int totalDecals = visibleDecalList.size();

        final int mouseSlotX = getSlotFromMouseX(mouseX);
        final int mouseSlotY = getSlotFromMouseY(mouseY);

        final int selectedItem = ((page*itemsPerPage) + mouseSlotY * itemsPerRow + mouseSlotX);

        if (mouseSlotX >= 0 && mouseSlotY >= 0 && mouseSlotX < itemsPerRow && mouseSlotY < itemsPerColumn && selectedItem < totalDecals)
        {
            selectedDecal = visibleDecalList.get(selectedItem);

            final CompoundNBT tagCompound = itemBeingEdited.getOrCreateTag();
            tagCompound.putString(NBT.AuthorName, selectedDecal.authorName);
            tagCompound.putString(NBT.DecalName, selectedDecal.decalName);
            tagCompound.putInt(NBT.DecalType, selectedDecal.renderableSlotType.getType().ordinal());
            tagCompound.putString(NBT.DecalLocation, selectedDecal.renderableSlotType.getTextureLocation());

            TheFinishingTouch.CHANNEL.sendToServer(new SetWandDecalMessage(tagCompound));

            return true;
        }
        return false;
    }

    private int getSlotFromMouseX(double mouseX) {
        final double adjustedMouseX = mouseX - guiLeft - slotOffsetX;
        final int iconSize = iconWidth + iconPadding;

        if (adjustedMouseX < 0 || adjustedMouseX % iconSize >= iconWidth) {
            return -1;
        }

        return (int)(adjustedMouseX / iconSize);
    }

    private int getSlotFromMouseY(double mouseY) {
        final double adjustedMouseY = mouseY - guiTop - slotOffsetY;
        final int iconSize = iconHeight + iconPadding;


        if (adjustedMouseY < 0 || adjustedMouseY % iconSize >= iconHeight) {
            return -1;
        }

        return (int)(adjustedMouseY / iconSize);
    }
}

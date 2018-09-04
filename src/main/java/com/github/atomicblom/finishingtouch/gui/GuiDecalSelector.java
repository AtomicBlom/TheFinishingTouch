package com.github.atomicblom.finishingtouch.gui;

import com.github.atomicblom.finishingtouch.TheFinishingTouch;
import com.github.atomicblom.finishingtouch.model.Artist;
import com.github.atomicblom.finishingtouch.network.SetWandDecalMessage;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.utility.Reference;
import com.github.atomicblom.finishingtouch.utility.Reference.NBT;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiDecalSelector extends GuiScreen {

    private static final ResourceLocation DECAL_SELECTOR_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/decal_selector.png");
    private final ItemStack itemBeingEdited;

    /** The X size of the inventory window in pixels. */
    private static final int xSize = 252;
    /** The Y size of the inventory window in pixels. */
    private static final int ySize = 134;

    private int guiLeft;
    private int guiTop;
    private List<RenderableSlot> builtinDecals = null;
    private final List<RenderableSlot> communityDecals = Lists.newArrayList();
    private SelectedTab selectedTab = SelectedTab.BUILT_IN;

    private int page = 0;

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

    public GuiDecalSelector(ItemStack itemBeingEdited)
    {
        this.itemBeingEdited = itemBeingEdited;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;

        NBTTagCompound tagCompound = itemBeingEdited.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
            itemBeingEdited.setTagCompound(tagCompound);
        }
        final String selectedDecalLocation = tagCompound.getString(NBT.DecalLocation);

        final IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGuiContainerBackgroundLayer();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();

        drawDecalPage(visibleDecalList);
        drawPreviewDecal(selectedDecal);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.colorMask(true, true, true, false);

        drawHoverSelection(mouseX, mouseY);

        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();

        RenderHelper.disableStandardItemLighting();
        drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
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
        final int hoveredItem = (page*itemsPerPage) + mouseSlotY * itemsPerRow + mouseSlotX;

        if (mouseSlotX >= 0 && mouseSlotY >= 0 && mouseSlotX < itemsPerRow && mouseSlotY < itemsPerColumn && hoveredItem < visibleDecalList.size()) {
            final int renderX = slotOffsetX + mouseSlotX * (iconWidth + iconPadding);
            final int renderY = slotOffsetY + mouseSlotY * (iconHeight + iconPadding);

            drawGradientRect(renderX, renderY, renderX + 16, renderY + 16, 0x80FFFFFF, 0x80FFFFFF);
        }
    }

    private int drawDecalPage(List<RenderableSlot> decalList) {
        int currentItem = getDecalPageStart(decalList);
        final int totalDecals = decalList.size();
        for (int y = 0; y < itemsPerColumn; ++y) {
            for (int x = 0; x < itemsPerRow; ++x) {
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
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(DECAL_SELECTOR_TEXTURE);
        final int i = (width - xSize) / 2;
        final int j = (height - ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(DECAL_SELECTOR_TEXTURE);
        int tabOffset = selectedTab == SelectedTab.COMMUNITY ? 12 : 0;
        int clippyOffset = selectedTab == SelectedTab.COMMUNITY ? 68 : 0;
        drawTexturedModalRect(82, 17, 0, 134 + tabOffset, 138, 12);
        drawTexturedModalRect(140+ clippyOffset, 11, 138, 134, 6, 13);
        drawTexturedModalRect(137, 119, 144, 134, 6, 13);
        drawTexturedModalRect(185, 119, 150, 134, 6, 13);

        if (selectedDecal != null) {
            float scale;

            final String authorName = selectedDecal.authorName;
            final String decalName = selectedDecal.decalName;
            final String siteName = "§9" + selectedDecal.authorSiteName;

            GlStateManager.pushMatrix();
            scale = fontRenderer.getStringWidth(decalName) > 80 ? 0.5f : 1f;
            GlStateManager.scale(scale, scale, scale);
            fontRenderer.drawString(decalName, (int)(8 * (1/scale)), (int)(92 * (1/scale)), 4210752);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            scale = fontRenderer.getStringWidth(authorName) > 80 ? 0.5f : 1f;
            GlStateManager.scale(scale, scale, scale);
            fontRenderer.drawString(authorName, (int)(8 * (1/scale)), (int)(102 * (1/scale)), 4210752);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            scale = fontRenderer.getStringWidth(siteName) > 80 ? 0.5f : 1f;
            GlStateManager.scale(scale, scale, scale);
            fontRenderer.drawString(siteName, (int)(8 * (1/scale)), (int)(112 * (1/scale)), 0xFFFFFFFF);
            GlStateManager.popMatrix();

            final RenderableSlotTypeBase renderableSlotType = selectedDecal.renderableSlotType;
            if (renderableSlotType.isTextureSizeKnown()) {
                final String textureSize = renderableSlotType.getTextureWidth() + "x" + renderableSlotType.getTextureHeight();
                final int textureSizeWidth = fontRenderer.getStringWidth(textureSize);
                fontRenderer.drawString(textureSize, 78-textureSizeWidth, 80, 0xFFFFFFFF);
            }
        }

        fontRenderer.drawString(I18n.format("gui."+Reference.MOD_ID + ":decal_selector.included"), 86, 20,  selectedTab == SelectedTab.BUILT_IN ? 0xFFFFFFFF : 0xFF808080);
        fontRenderer.drawString(I18n.format("gui."+Reference.MOD_ID + ":decal_selector.community"), 158, 20, selectedTab == SelectedTab.COMMUNITY ? 0xFFFFFFFF : 0xFF808080);
        fontRenderer.drawString(I18n.format("gui."+Reference.MOD_ID + ":decal_selector.decallibrary"), 7, 6, 4210752);

        if (selectedTab == SelectedTab.COMMUNITY) {
            String comingSoonText = I18n.format("gui."+Reference.MOD_ID + ":decal_selector.comingsoon");
            int comingSoonWidth = 162 / 2 - fontRenderer.getStringWidth(comingSoonText) / 2 + 83;
            fontRenderer.drawString(comingSoonText, comingSoonWidth, 70, 0xFFFFFFFF);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        final int adjustedMouseX = mouseX - guiLeft;
        final int adjustedMouseY = mouseY - guiTop;
        if (mouseButton == 0) {
            if (trySelectDecal(mouseX, mouseY)) return;
            if (trySelectArtistLink(adjustedMouseX, adjustedMouseY)) return;
            if (tryChangePageBack(adjustedMouseX, adjustedMouseY)) return;
            if (tryChangePageForward(adjustedMouseX, adjustedMouseY)) return;
            if (trySelectComunityTab(adjustedMouseX, adjustedMouseY)) return;
            if (trySelectBuiltInTab(adjustedMouseX, adjustedMouseY)) return;
        }
    }

    private boolean trySelectBuiltInTab(int mouseX, int mouseY)
    {
        if (selectedTab == SelectedTab.BUILT_IN) return false;
        if (mouseX < 82 || mouseX > 82 + 65) return false;
        if (mouseY < 17 || mouseY > 17 + 11) return false;

        selectedTab = SelectedTab.BUILT_IN;
        page = 0;
        visibleDecalList = builtinDecals;

        return true;
    }

    private boolean trySelectComunityTab(int mouseX, int mouseY)
    {
        if (selectedTab == SelectedTab.COMMUNITY) return false;
        if (mouseX < 82 + 72 || mouseX > 82 + 72 + 66) return false;
        if (mouseY < 17 || mouseY > 17 + 11) return false;

        selectedTab = SelectedTab.COMMUNITY;
        page = 0;
        visibleDecalList = communityDecals;

        return true;
    }

    private boolean tryChangePageBack(int mouseX, int mouseY) {
        if (mouseX < 137 || mouseX > 137 + 6) return false;
        if (mouseY < 119 || mouseY > 119 + 13) return false;
        if (page > 0) {
            page--;
        }

        return true;
    }

    private boolean tryChangePageForward(int mouseX, int mouseY) {
        if (mouseX < 185 || mouseX > 185 + 6) return false;
        if (mouseY < 119 || mouseY > 119 + 13) return false;
        if (page < visibleDecalList.size() / itemsPerPage) {
            page++;
        }

        return true;
    }

    private boolean trySelectArtistLink(int mouseX, int mouseY)
    {
        if (selectedDecal == null || Strings.isNullOrEmpty(selectedDecal.authorUrl)) return false;
        if (mouseX < 7 || mouseX > 78) return false;
        if (mouseY < 109 || mouseY > 117) return false;

        if (mc.gameSettings.chatLinksPrompt)
        {
            mc.displayGuiScreen(new GuiConfirmOpenLink(this, selectedDecal.authorSiteName, -1, false));
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

    @Override
    public void confirmClicked(boolean result, int id)
    {
        if (id == -1)
        {
            if (result)
            {
                openAuthorUrl();
            }

            mc.displayGuiScreen(this);
        } else {
            super.confirmClicked(result, id);
        }
    }

    private boolean trySelectDecal(int mouseX, int mouseY)
    {
        final int totalDecals = visibleDecalList.size();

        final int mouseSlotX = getSlotFromMouseX(mouseX);
        final int mouseSlotY = getSlotFromMouseY(mouseY);

        final int selectedItem = (page*itemsPerPage) + mouseSlotY * itemsPerRow + mouseSlotX;

        if (mouseSlotX >= 0 && mouseSlotY >= 0 && mouseSlotX < itemsPerRow && mouseSlotY < itemsPerColumn && selectedItem < totalDecals)
        {
            selectedDecal = visibleDecalList.get(selectedItem);

            final NBTTagCompound tagCompound = itemBeingEdited.getTagCompound();
            tagCompound.setString(NBT.AuthorName, selectedDecal.authorName);
            tagCompound.setString(NBT.DecalName, selectedDecal.decalName);
            tagCompound.setInteger(NBT.DecalType, selectedDecal.renderableSlotType.getType().ordinal());
            tagCompound.setString(NBT.DecalLocation, selectedDecal.renderableSlotType.getTextureLocation());

            TheFinishingTouch.CHANNEL.sendToServer(new SetWandDecalMessage(tagCompound));

            return true;
        }
        return false;
    }

    private int getSlotFromMouseX(int mouseX) {
        final int adjustedMouseX = mouseX - guiLeft - slotOffsetX;
        final int iconSize = iconWidth + iconPadding;

        if (adjustedMouseX < 0 || adjustedMouseX % iconSize >= iconWidth) {
            return -1;
        }

        return adjustedMouseX / iconSize;
    }

    private int getSlotFromMouseY(int mouseY) {
        final int adjustedMouseY = mouseY - guiTop - slotOffsetY;
        final int iconSize = iconHeight + iconPadding;


        if (adjustedMouseY < 0 || adjustedMouseY % iconSize >= iconHeight) {
            return -1;
        }

        return adjustedMouseY / iconSize;
    }
}

enum SelectedTab {
    BUILT_IN,
    COMMUNITY
}

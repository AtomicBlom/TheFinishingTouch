package com.github.atomicblom.finishingtouch.handlers;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public final class DecalPreviewHandler
{
	@SubscribeEvent
	public static void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
		if (DecalPositioningHandler.isRemoving() || DecalPositioningHandler.isPlacing())
		{
			event.setCanceled(true);
		}
	}

}

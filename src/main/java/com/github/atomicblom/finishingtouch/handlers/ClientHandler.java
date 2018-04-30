package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.decals.RenderableDecalStore;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientHandler
{
	@SubscribeEvent
	public static void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
		RenderableDecalStore.clearAllDecals();
		DecalPositioningHandler.reset();
	}
}

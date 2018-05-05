package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.NonRegistryLibrary;
import com.github.atomicblom.finishingtouch.decals.ClientDecalStore;
import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public final class ClientHandler
{
	@SubscribeEvent
	public static void onGameStarting(NewRegistry event) {
		//crap event to do this, but there's no "just after game loaded" event.
		ClientRegistry.registerKeyBinding(NonRegistryLibrary.removeDecalBinding);

		NonRegistryLibrary.decalsAdded.registerStat();
		NonRegistryLibrary.decalsRemoved.registerStat();
	}

	@SubscribeEvent
	public static void onWorldUnload(Unload event) {
		ClientDecalStore.clearAllDecals();
		DecalPositioningHandler.reset();
	}
}

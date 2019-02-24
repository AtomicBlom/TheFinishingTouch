package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.ItemLibrary;
import com.github.atomicblom.finishingtouch.utility.Reference.Items;
import javafx.geometry.Side;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public final class ItemRenderingHandler
{
	@SubscribeEvent
	public static void onRenderingReady(ModelRegistryEvent evt)
	{
//		ModelLoader.setCustomModelResourceLocation(
//				ItemLibrary.decal_wand,
//				0,
//				new ModelResourceLocation(Items.DecalWand, "inventory")
//		);
	}
}

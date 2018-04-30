package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.DecalWandItem;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class ItemRegistrationHandler
{
	@SubscribeEvent
	public static void onRegisterItems(Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registerItem(registry, new DecalWandItem(), Reference.Items.DecalWand, true);
	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item, ResourceLocation registryName, boolean showInCreativeTab)
	{
		item.setRegistryName(registryName)
				.setUnlocalizedName(registryName.toString());

		if (showInCreativeTab) {
			item.setCreativeTab(Reference.CreativeTab);
		}

		registry.register(item);
	}
}

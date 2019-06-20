package com.github.atomicblom.finishingtouch.handlers;

import com.github.atomicblom.finishingtouch.DecalWandItem;
import com.github.atomicblom.finishingtouch.ItemLibrary;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistrationHandler
{
	@SubscribeEvent
	public static void onRegisterItems(Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		final ItemGroup itemGroup = new ItemGroup(Reference.MOD_ID + ".main") {
			private ItemStack _stack = null;

			@Override
			public ItemStack createIcon()
			{
				return _stack != null
						? _stack
						: (_stack = new ItemStack(ItemLibrary.decal_wand));
			}
		};

		registerItem(registry, new DecalWandItem(new Properties().group(itemGroup)), Reference.Items.DecalWand);
	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item, ResourceLocation registryName)
	{
		item.setRegistryName(registryName);
		registry.register(item);
	}
}

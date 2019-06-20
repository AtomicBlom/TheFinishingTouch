package com.github.atomicblom.finishingtouch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;

public class NonRegistryLibrary
{
	//The Finishing Touch's keybinds.
	public static final KeyBinding removeDecalBinding = new KeyBinding(
			"remove.decal",
			new IKeyConflictContext() {
				@Override
				public boolean isActive()
				{
					//FIXME: Determine if it's possible to remove a decal now.
					return true;
				}

				@Override
				public boolean conflicts(IKeyConflictContext other)
				{
					return other == this || other == KeyConflictContext.IN_GAME;
				}
			},
			InputMappings.getInputByName("key.keyboard.left.alt"),
			"key.categories.gameplay");

	//Statistics
	//FIXME: Reenable this
	//public static final StatBase decalsAdded = new StatBase(Reference.Stats.DecalsAdded, new TextComponentTranslation(Reference.Language.DecalsAdded));
	//public static final StatBase decalsRemoved = new StatBase(Reference.Stats.DecalsRemoved, new TextComponentTranslation(Reference.Language.DecalsRemoved));

	//Minecraft keybinds
	public static final KeyBinding keyBindUseItem = Minecraft.getInstance().gameSettings.keyBindUseItem;
	public static final KeyBinding keyBindSneak = Minecraft.getInstance().gameSettings.keyBindSneak;
}

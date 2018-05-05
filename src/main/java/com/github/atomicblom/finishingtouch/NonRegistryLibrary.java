package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.input.Keyboard;

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
			Keyboard.KEY_LMENU,
			"key.categories.gameplay");

	//Statistics
	public static final StatBase decalsAdded = new StatBase(Reference.Stats.DecalsAdded, new TextComponentTranslation(Reference.Language.DecalsAdded));
	public static final StatBase decalsRemoved = new StatBase(Reference.Stats.DecalsRemoved, new TextComponentTranslation(Reference.Language.DecalsRemoved));

	//Minecraft keybinds
	public static final KeyBinding keyBindUseItem = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
	public static final KeyBinding keyBindSneak = Minecraft.getMinecraft().gameSettings.keyBindSneak;
}

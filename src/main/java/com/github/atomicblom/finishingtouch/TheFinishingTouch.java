package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.handlers.GuiHandler;
import com.github.atomicblom.finishingtouch.network.*;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.utility.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class TheFinishingTouch
{
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    @Instance
    public static TheFinishingTouch INSTANCE;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Configure Logging
        LogHelper.setLog(event.getModLog());

        //Configure Networking
        CHANNEL.registerMessage(DecalMessageHandler.class, DecalMessage.class, 0, Side.SERVER);
        CHANNEL.registerMessage(SendDecalEventToClientMessageHandler.class, SendDecalEventToClientMessage.class, 1, Side.CLIENT);
        CHANNEL.registerMessage(SetWandDecalMessageHandler.class, SetWandDecalMessage.class, 2, Side.SERVER);

        //Configure GUI handling
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }
}

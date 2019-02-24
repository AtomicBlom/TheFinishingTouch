package com.github.atomicblom.finishingtouch;

import com.github.atomicblom.finishingtouch.handlers.GuiHandler;
import com.github.atomicblom.finishingtouch.network.*;
import com.github.atomicblom.finishingtouch.utility.LogHelper;
import com.github.atomicblom.finishingtouch.utility.Reference;
import com.sun.javafx.scene.traversal.ContainerTabOrder;
import javafx.geometry.Side;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;

@Mod(Reference.MOD_ID)
public class TheFinishingTouch
{
    public static TheFinishingTouch INSTANCE;

    public TheFinishingTouch()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        INSTANCE = this;
    }

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reference.MOD_ID, "network"),
            () -> "1.0",
            "1.0"::equals,
            "1.0"::equals
            );

    private void setup(final FMLCommonSetupEvent event) {
        LogHelper.setLog(LogManager.getLogger(Reference.MOD_ID));
        CHANNEL.registerMessage(0, DecalMessage.class,
                DecalMessageHandler::toBytes,
                DecalMessageHandler::fromBytes,
                DecalMessageHandler::handle
        );

        CHANNEL.registerMessage(1, SendDecalEventToClientMessage.class,
                SendDecalEventToClientMessageHandler::toBytes,
                SendDecalEventToClientMessageHandler::fromBytes,
                SendDecalEventToClientMessageHandler::handle
        );

        CHANNEL.registerMessage(2, SetWandDecalMessage.class,
                SetWandDecalMessageHandler::toBytes,
                SetWandDecalMessageHandler::fromBytes,
                SetWandDecalMessageHandler::handle
        );
    }
}

package talonos.cavestokingdoms.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandler;
import talonos.cavestokingdoms.lib.DEFS;
import talonos.cavestokingdoms.network.handlers.AddAllDiscoveriesHandler;
import talonos.cavestokingdoms.network.handlers.AddDiscoveryHandler;
import talonos.cavestokingdoms.network.handlers.WipeDiscoveryProgressHandler;
import talonos.cavestokingdoms.network.packets.AddAllDiscoveriesPacket;
import talonos.cavestokingdoms.network.packets.AddDiscoveryPacket;
import talonos.cavestokingdoms.network.packets.WipeDiscoveryProgressPacket;

@ChannelHandler.Sharable
public class CavesToKingdomsNetwork {

    private static final CavesToKingdomsNetwork INSTANCE = new CavesToKingdomsNetwork();
    private SimpleNetworkWrapper networkWrapper;

    public static void init() {
        INSTANCE.networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(DEFS.MODID);
        INSTANCE.networkWrapper
            .registerMessage(WipeDiscoveryProgressHandler.class, WipeDiscoveryProgressPacket.class, 0, Side.CLIENT);
        INSTANCE.networkWrapper.registerMessage(AddDiscoveryHandler.class, AddDiscoveryPacket.class, 1, Side.CLIENT);
        INSTANCE.networkWrapper
            .registerMessage(AddAllDiscoveriesHandler.class, AddAllDiscoveriesPacket.class, 2, Side.CLIENT);
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
        INSTANCE.networkWrapper.sendTo(message, player);
    }
}

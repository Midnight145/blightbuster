package talonos.cavestokingdoms.network.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import talonos.cavestokingdoms.CavesToKingdoms;
import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryRegistry;
import talonos.cavestokingdoms.network.packets.WipeDiscoveryProgressPacket;

public class WipeDiscoveryProgressHandler implements IMessageHandler<WipeDiscoveryProgressPacket, IMessage> {

    public WipeDiscoveryProgressHandler() {}

    @Override
    public IMessage onMessage(WipeDiscoveryProgressPacket message, MessageContext ctx) {
        OreDiscoveryRegistry.getInstance()
            .clearDiscoveries(
                CavesToKingdoms.proxy.getPlayerFromContext(ctx)
                    .getEntityData());

        return null;
    }
}

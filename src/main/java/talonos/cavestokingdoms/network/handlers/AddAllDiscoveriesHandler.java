package talonos.cavestokingdoms.network.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import talonos.cavestokingdoms.CavesToKingdoms;
import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryRegistry;
import talonos.cavestokingdoms.network.packets.AddAllDiscoveriesPacket;

public class AddAllDiscoveriesHandler implements IMessageHandler<AddAllDiscoveriesPacket, IMessage> {

    public AddAllDiscoveriesHandler() {}

    @Override
    public IMessage onMessage(AddAllDiscoveriesPacket message, MessageContext ctx) {
        OreDiscoveryRegistry.getInstance()
            .addAllDiscoveries(
                CavesToKingdoms.proxy.getPlayerFromContext(ctx)
                    .getEntityData());

        return null;
    }
}

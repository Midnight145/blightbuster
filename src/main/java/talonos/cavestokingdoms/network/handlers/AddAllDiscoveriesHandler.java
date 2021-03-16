package talonos.cavestokingdoms.network.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import talonos.cavestokingdoms.CavesToKindgoms;
import talonos.cavestokingdoms.client.pages.orediscovery.OreDiscoveryRegistry;
import talonos.cavestokingdoms.network.packets.AddAllDiscoveriesPacket;

public class AddAllDiscoveriesHandler implements IMessageHandler<AddAllDiscoveriesPacket, IMessage> {

    public AddAllDiscoveriesHandler(){}

    @Override
    public IMessage onMessage(AddAllDiscoveriesPacket message, MessageContext ctx) {
        OreDiscoveryRegistry.getInstance().addAllDiscoveries(CavesToKindgoms.proxy.getPlayerFromContext(ctx).getEntityData());

        return null;
    }
}

package talonos.cavestokingdoms.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class AddDiscoveryPacket implements IMessage {

    private String discoveryName;

    public AddDiscoveryPacket() {}

    public AddDiscoveryPacket(String name) {
        this.discoveryName = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.discoveryName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.discoveryName);
    }

    public String getDiscoveryName() {
        return this.discoveryName;
    }
}

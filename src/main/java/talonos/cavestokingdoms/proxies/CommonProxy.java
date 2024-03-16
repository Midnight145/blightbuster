package talonos.cavestokingdoms.proxies;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import talonos.cavestokingdoms.blocks.entities.AltarEntity;

public class CommonProxy {

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(AltarEntity.class, "AltarEntity");
    }

    public void registerRenderers() {}

    public EntityPlayer getPlayerFromContext(MessageContext context) {
        return context.getServerHandler().playerEntity;
    }
}

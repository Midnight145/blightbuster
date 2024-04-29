package talonos.blightbuster;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import talonos.blightbuster.client.DawnMachineControllerRenderer;
import talonos.blightbuster.client.DawnMachineSpoutRenderer;
import talonos.blightbuster.entities.EntitySilverPotion;
import talonos.blightbuster.items.BBItems;
import talonos.blightbuster.tileentity.DawnMachineSpoutTileEntity;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderers() {
        super.registerRenderers();

        if (BlightbusterConfig.enableSilverPotion) {
            RenderingRegistry
                .registerEntityRenderingHandler(EntitySilverPotion.class, new RenderSnowball(BBItems.silverPotion));
            RenderManager.instance.entityRenderMap
                .put(EntitySilverPotion.class, new RenderSnowball(BBItems.silverPotion));
        }
        if (BlightbusterConfig.enableDawnMachine) {
            ClientRegistry
                .bindTileEntitySpecialRenderer(DawnMachineTileEntity.class, new DawnMachineControllerRenderer());
            ClientRegistry
                .bindTileEntitySpecialRenderer(DawnMachineSpoutTileEntity.class, new DawnMachineSpoutRenderer());
        }
    }

    @Override
    public double getBestCleanseSpawnHeight() {
        return Minecraft.getMinecraft().thePlayer.posY;
    }
}

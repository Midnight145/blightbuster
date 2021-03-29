package talonos.blightbuster.network.handlers;

import java.util.Random;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.network.packets.SpawnCleanseParticlesPacket;

public class SpawnCleanseParticleHandler implements IMessageHandler<SpawnCleanseParticlesPacket, IMessage> {
    @Override
    public IMessage onMessage(SpawnCleanseParticlesPacket message, MessageContext ctx) {
        double height = BlightBuster.proxy.getBestCleanseSpawnHeight();
        Random r = new Random();

        int maxBlocks = message.isLargeArea()?1:0;
        for (int x = 0; x <= maxBlocks; x++) {
            for (int z = 0; z <= maxBlocks; z++) {
                for (int y = 0; y < 50; y++)
                {
                    double d1 = r.nextGaussian() * 0.02D;
                    double d2 = r.nextGaussian() * 0.02D;
                    Minecraft.getMinecraft().theWorld.spawnParticle(message.doUseFlameParticles() ? "flame" : "smoke", x+message.getSpawnX() + r.nextDouble(), height - 5 + (r.nextDouble() * 15), z+message.getSpawnZ() + r.nextDouble(), 0, d1, d2);
                }
            }
        }

        return null;
    }
}

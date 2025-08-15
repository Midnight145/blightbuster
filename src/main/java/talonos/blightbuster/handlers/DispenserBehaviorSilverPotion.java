package talonos.blightbuster.handlers;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

import talonos.blightbuster.entities.EntitySilverPotion;

public class DispenserBehaviorSilverPotion extends BehaviorProjectileDispense {

    @Override
    protected IProjectile getProjectileEntity(World world, IPosition position) {
        return new EntitySilverPotion(world, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
    }
}

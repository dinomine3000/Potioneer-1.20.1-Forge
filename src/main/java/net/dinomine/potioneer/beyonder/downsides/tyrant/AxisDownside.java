package net.dinomine.potioneer.beyonder.downsides.tyrant;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class AxisDownside extends Downside {
    public AxisDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        alignLook(target);
        return true;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        int interval = 15 - (9-sequenceLevel)*3;
        if (target.tickCount % (interval*20) != 0) return;
        alignLook(target);

    }

    public static void alignLook(LivingEntity target){
        Vec3 lookAngle = target.getLookAngle();

        // The 6 cardinal/axis directions
        Vec3[] directions = new Vec3[] {
                new Vec3(1, 0, 0),   // East
                new Vec3(-1, 0, 0),  // West
                new Vec3(0, 1, 0),   // Up
                new Vec3(0, -1, 0),  // Down
                new Vec3(0, 0, 1),   // South
                new Vec3(0, 0, -1)   // North
        };

        Vec3 bestDir = directions[0];
        double maxDot = -Double.MAX_VALUE;

        // Find the direction vector closest to current look angle (highest dot product)
        for (Vec3 dir : directions) {
            double dot = lookAngle.dot(dir);
            if (dot > maxDot) {
                maxDot = dot;
                bestDir = dir;
            }
        }

        // Convert the closest direction vector into Yaw and Pitch angles
        double dx = bestDir.x;
        double dy = bestDir.y;
        double dz = bestDir.z;

        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.asin(dy));

        // Update entity rotations
        target.setYRot(yaw);
        target.setXRot(pitch);
        target.setYHeadRot(yaw);
        target.yRotO = yaw;
        target.xRotO = pitch;

        // If target is a ServerPlayer, sync the rotation back to the client
        if (target instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(
                    serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                    yaw, pitch
            );
        }
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_axis_lock";
    }
}

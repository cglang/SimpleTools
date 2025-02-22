package org.simpleTools.NetherPortal.Teleport;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportModel {
    public TeleportModel(@NotNull TeleportMode mode, @Nullable World world, @Nullable Location loc, float yaw) {
        this.mode = mode;

        if (world != null) {
            this.location = world.getSpawnLocation();
            if (loc != null)
                this.location.set(loc.getX(), loc.getY(), loc.getZ());
            this.location.setYaw(yaw);
        }
    }

    private final TeleportMode mode;

    public @NotNull TeleportMode getMode() {
        return mode;
    }

    private @Nullable Location location = null;

    public @Nullable Location getLocation() {
        return location;
    }
}

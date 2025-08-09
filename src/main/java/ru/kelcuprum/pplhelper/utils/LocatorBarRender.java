package ru.kelcuprum.pplhelper.utils;

//#if MC >= 12106
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;

public class LocatorBarRender {
    private static final ResourceLocation ARROW_UP = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_up");
    private static final ResourceLocation ARROW_DOWN = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_down");

    public static void renderLodestoneWaypoints(Minecraft client, GuiGraphics context, int centerY) {
        FollowManager.Coordinates coordinates = FollowManager.getCurrentCoordinates();
        if (client.player == null || client.cameraEntity == null || coordinates == null) return;
        if(FollowManager.playerInCurrentLevel() && FollowManager.playerInCurrentWorld()) {
            renderLodestoneWaypoint(client, context, centerY, coordinates.pos());
        }
    }

    private static void renderLodestoneWaypoint(Minecraft client, GuiGraphics context, int centerY, Vec3 lodestone) {
        if (client.player == null || client.cameraEntity == null) return;

        double relativeYaw = getRelativeYaw(lodestone, client.gameRenderer.getMainCamera());
        if (relativeYaw <= -61.0 || relativeYaw > 60.0)  return;

        Waypoint.Icon config = new Waypoint.Icon();
        config.style = ResourceKey.create(WaypointStyleAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath("pplhelper", "project"));

        WaypointStyle waypointStyleAsset = client.getWaypointStyles().get(config.style);
        ResourceLocation ResourceLocation = waypointStyleAsset.sprite(
                (float) Math.sqrt(lodestone.distanceToSqr(client.cameraEntity.position()))
        );
        int color = config.color.orElseGet(() -> ARGB.setBrightness(
                ARGB.color(255, -1), 0.9F
        ));

        int x = Mth.ceil((context.guiWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, ResourceLocation, x, centerY - 2, 9, 9, color);

        TrackedWaypoint.PitchDirection pitch = getPitch(lodestone, client.gameRenderer);
        if (pitch != TrackedWaypoint.PitchDirection.NONE) {
            int yOffset;
            ResourceLocation texture;
            if (pitch == TrackedWaypoint.PitchDirection.DOWN) {
                yOffset = 6;
                texture = ARROW_DOWN;
            } else {
                yOffset = -6;
                texture = ARROW_UP;
            }

            context.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x + 1, centerY + yOffset, 7, 5);
        }
    }

    private static double getRelativeYaw(Vec3 pos, TrackedWaypoint.Camera yawProvider) {
        Vec3 vec3d = yawProvider.position().subtract(pos).rotateClockwise90();
        float f = (float) Mth.atan2(vec3d.z(), vec3d.x()) * (180.0F / (float)Math.PI);
        return Mth.degreesDifference(yawProvider.yaw(), f);
    }

    private static TrackedWaypoint.PitchDirection getPitch(Vec3 pos, TrackedWaypoint.Projector cameraProvider) {
        Vec3 vec3d = cameraProvider.projectPointToScreen(pos);
        boolean bl = vec3d.z > 1.0;
        double d = bl ? -vec3d.y : vec3d.y;
        if (d < -1.0) {
            return TrackedWaypoint.PitchDirection.DOWN;
        } else if (d > 1.0) {
            return TrackedWaypoint.PitchDirection.UP;
        } else {
            if (bl) {
                if (vec3d.y > 0.0) {
                    return TrackedWaypoint.PitchDirection.UP;
                }

                if (vec3d.y < 0.0) {
                    return TrackedWaypoint.PitchDirection.DOWN;
                }
            }

            return TrackedWaypoint.PitchDirection.NONE;
        }
    }
}
//#endif
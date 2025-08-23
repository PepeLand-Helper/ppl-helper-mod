package ru.pplh.mod.utils;

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
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.PepeLandHelper;

import static ru.kelcuprum.alinlib.gui.Colors.CONVICT;
import static ru.kelcuprum.alinlib.gui.Colors.SEADRIVE;

public class LocatorBarRender {
    private static final ResourceLocation ARROW_UP = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_up");
    private static final ResourceLocation ARROW_DOWN = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_down");

    public static void renderLodestoneWaypoints(Minecraft client, GuiGraphics context, int centerY) {
        FollowManager.Coordinates coordinates = FollowManager.getCurrentCoordinates();
        if (client.player == null || client.cameraEntity == null) return;
        if(coordinates != null && FollowManager.playerInCurrentLevel() && FollowManager.playerInCurrentWorld()) {
            renderLodestoneWaypoint(client, context, centerY, coordinates.pos());
        }
        if(TabHelper.getWorld() == TabHelper.Worlds.TRADE && PepeLandHelper.config.getBoolean("LOCATOR_BAR.TRADE", true)){
            for(TradeManager.Category category : TradeManager.categories){
                if(!playerInArea(AlinLib.MINECRAFT.player.position(), category.pos1(), category.pos2())) renderIconWaypoint(client, context, TradeManager.activeCategory == category ? ResourceLocation.parse("pplhelper:star") : category.icon(), TradeManager.activeCategory == category ? CONVICT : category.color(), category.name().substring(0, 3), centerY, category.center());
                else if(TradeManager.activeCategory == category) TradeManager.activeCategory = null;
            }
        }
    }

    public static boolean playerInArea(Vec3 playerPos, Vec3 pos1, Vec3 pos2){
        return ((pos1.x <= playerPos.x && playerPos.x <= pos2.x) &&
                (pos1.y <= playerPos.y && playerPos.y <= pos2.y) &&
                (pos1.z <= playerPos.z && playerPos.z <= pos2.z));
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
    private static void renderIconWaypoint(Minecraft client, GuiGraphics context, ResourceLocation resourceLocation, int point_color, String name, int centerY, Vec3 lodestone) {
        if (client.player == null || client.cameraEntity == null) return;

        double relativeYaw = getRelativeYaw(lodestone, client.gameRenderer.getMainCamera());
        if (relativeYaw <= -61.0 || relativeYaw > 60.0)  return;

        Waypoint.Icon config = new Waypoint.Icon();
        config.style = ResourceKey.create(WaypointStyleAssets.ROOT_ID, resourceLocation);

        WaypointStyle waypointStyleAsset = client.getWaypointStyles().get(config.style);
        ResourceLocation ResourceLocation = waypointStyleAsset.sprite(
                (float) Math.sqrt(lodestone.distanceToSqr(client.cameraEntity.position()))
        );
        int color = config.color.orElse(point_color);

        int x = Mth.ceil((context.guiWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, ResourceLocation, x, centerY - 2, 9, 9, color);
        if(PepeLandHelper.isTestSubject() && PepeLandHelper.config.getBoolean("IM_A_TEST_SUBJECT.LOCATOR.TRADE", false)) context.drawString(AlinLib.MINECRAFT.font, name, x, centerY-5, -1);

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
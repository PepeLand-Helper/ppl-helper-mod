//package ru.pplh.mod.utils;
//
//import com.mojang.datafixers.util.Either;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.client.waypoints.ClientWaypointManager;
//import net.minecraft.world.waypoints.TrackedWaypoint;
//
//import java.util.*;
//
//public class TracksWaypoints {
//    public static final Map<Either<UUID, String>, TrackedWaypoint> WAYPOINTS = new HashMap<>();
//    private static long lastUpdateTime = 0;
//
//    public static void resetWaypoints() {
//        WAYPOINTS.clear();
//        lastUpdateTime = 0;
//    }
//
//    public static void updateWaypoints(LocalPlayer player) {
//        if (player == null || (lastUpdateTime + 20 > player.tickCount && lastUpdateTime < player.tickCount)) return;
//        lastUpdateTime = player.tickCount;
//
//        Map<Either<UUID, String>, TrackedWaypoint> oldWaypoints = new HashMap<>(WAYPOINTS);
//        WAYPOINTS.clear();
//        getWaypointsFromStack().forEach(waypoint -> WAYPOINTS.put(waypoint.id(), waypoint));
//
//        ClientWaypointManager waypointHandler = player.connection.getWaypointManager();
//
//        for (TrackedWaypoint newWaypoint : WAYPOINTS.values()) {
//            if (oldWaypoints.containsKey(newWaypoint.id()) && waypointHandler.waypoints.containsKey(newWaypoint.id())) {
//                waypointHandler.updateWaypoint(newWaypoint);
//            } else {
//                waypointHandler.trackWaypoint(newWaypoint);
//            }
//        }
//
//        for (TrackedWaypoint oldWaypoint : oldWaypoints.values()) {
//            if (!WAYPOINTS.containsKey(oldWaypoint.id())) {
//                waypointHandler.untrackWaypoint(oldWaypoint);
//            }
//        }
//    }
//    private static List<TrackedWaypoint> getWaypointsFromStack() {
//        List<TrackedWaypoint> waypoints = new ArrayList<>();
//        for(TrackedWaypoint waypoint : ){
//            boolean add = !ClientClovrerBuild.config.getBoolean("disable_static_waypoints", false) || waypoint.removable;
//            if(add) {
//                assert Minecraft.getInstance().level != null;
//                if(Minecraft.getInstance().level.dimension().identifier().equals(waypoint.level)){
//                    waypoints.add(waypoint.getTrackedWaypoint());
//                }
//            }
//        }
//        return waypoints;
//    }
//}

package ru.pplh.mod.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.kelcuprum.alinlib.AlinLib;
import ru.pplh.mod.gui.screens.CameraScreen;

import java.util.ArrayList;

import static ru.pplh.mod.PepeLandHelper.LOG;

public class CameraManager {
    public static boolean isCameraMode = false;
    public static Camera currentCamera = null;
    public static float xRot = 0;
    public static float yRot = 0;
    public static float fov = 0;

    public static void setCamera(Camera camera){
        isCameraMode = !(camera == null);
        currentCamera = camera;
        xRot = camera == null ? 0f : camera.rotation;
        yRot = 0;
        fov = AlinLib.MINECRAFT.options.fov().get();
    }

    public record Camera(String name, BlockPos position, float rotation, BlockEntity blockEntity, CameraType cameraType){}
    public enum CameraType{
        DEFAULT,
        RGB,
        CREEPER,
        CLEAR
    }

    public static CameraType getCameraType(String type){
        return switch (type.toLowerCase()){
            case "rgb" -> CameraType.RGB;
            case "clear" -> CameraType.CLEAR;
            case "creeper" -> CameraType.CREEPER;
            default -> CameraType.DEFAULT;
        };
    }

    public static void openMonitor(String name, BlockPos blockPos, Level level){
        int radius = 100;
        ArrayList<Camera> cameras = new ArrayList<>();
        for(int y = blockPos.getY()-radius;y<blockPos.getY()+radius;y++){
            for(int x = blockPos.getX()-radius;x<blockPos.getX()+radius;x++){
                for(int z = blockPos.getZ()-radius;z<blockPos.getZ()+radius;z++){
                    BlockPos blockPos1 = new BlockPos(x, y, z);
                    BlockState blockState = level.getBlockState(blockPos1);
                    if(blockState.is(Blocks.PLAYER_HEAD) || blockState.is(Blocks.PLAYER_WALL_HEAD)){
                        BlockEntity blockEntity = level.getBlockEntity(blockPos1);
                        BlockDataAccessor dataAccessor = new BlockDataAccessor(blockEntity, blockEntity.getBlockPos());
                        Tag tag = dataAccessor.getData().get("custom_name");
                        if (tag != null) {
                            String customName = tag.asString().get();
                            if(customName.startsWith("camera")){
                                String[] args = customName.split(":");
                                if(args.length >= 2){
                                    if(args[1].equals(name)) {
                                        float i = blockState.is(Blocks.PLAYER_HEAD) ? getRotate(blockState.getValue(SkullBlock.ROTATION).intValue()) : getRotate(blockState.getValue(WallSkullBlock.FACING));
                                        cameras.add(new Camera(name, blockPos1, i, blockEntity, getCameraType(args.length < 3 ? "default" : args[2])));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(cameras.isEmpty()) AlinLib.MINECRAFT.gui.getChat().addMessage(Component.translatable("pplhelper.camera.empty"));
        else AlinLib.MINECRAFT.execute(() -> AlinLib.MINECRAFT.setScreen(new CameraScreen(AlinLib.MINECRAFT.screen, cameras)));
    }

    public static float getRotate(int rotate){
        return switch (rotate){
            case 15 -> 157.5f;
            case 14 -> 135f;
            case 13 -> 112.5f;
            case 12 -> 90.0f;
            case 11 -> 67.5f;
            case 10 -> 45f;
            case 9 -> 22.5f;
            case 8 -> 0.0f;
            case 7 -> -22.5f;
            case 6 -> -45f;
            case 5 -> -67.5f;
            case 4 -> -90.0f;
            case 3 -> -112.5f;
            case 2 -> -135f;
            case 1 -> -157.5f;
            default -> -180.0f;
        };
    }

    public static float getRotate(Direction rotate){
        return switch (rotate){
            case EAST -> -90;
            case SOUTH -> 0;
            case WEST -> 90;
            default -> -180;
        };
    }
}

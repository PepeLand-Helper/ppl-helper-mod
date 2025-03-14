package ru.kelcuprum.pplhelper.utils;


import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.SchematicHolder;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import fi.dy.masa.litematica.util.FileType;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.components.Project;

import java.io.File;

public class LitematicaManager {
    public static IMessageConsumer lol = new IMessageConsumer() {
        @Override
        public void addMessage(Message.MessageType messageType, String s, Object... objects) {
            PepelandHelper.LOG.log(s);
        }

        @Override
        public void addMessage(Message.MessageType messageType, int i, String s, Object... objects) {
            PepelandHelper.LOG.log(s);
        }
    };

    public static void loadSchematic(File file, Project project){
        if(!file.exists() || !file.isFile() || !file.canRead()) throw new RuntimeException("Схематика не читается, SAJ");
        LitematicaSchematic schematic = null;
        FileType type = FileType.fromFile(file.toPath());
        if(type == FileType.LITEMATICA_SCHEMATIC)
            schematic = LitematicaSchematic.createFromFile(file.getParentFile(), file.getName());
        else if (type == FileType.SCHEMATICA_SCHEMATIC)
            schematic = WorldUtils.convertSchematicaSchematicToLitematicaSchematic(file.getParentFile(), file.getName(), false, new IStringConsumer() {
                @Override
                public void setString(String s) {

                }
            });
        else if (type == FileType.VANILLA_STRUCTURE)
            schematic = WorldUtils.convertStructureToLitematicaSchematic(file.getParentFile(), file.getName());
        else if (type == FileType.SPONGE_SCHEMATIC)
            schematic = WorldUtils.convertSpongeSchematicToLitematicaSchematic(file.getParentFile(), file.getName());
        if(schematic == null) throw new RuntimeException("Схематики нет :(");
        SchematicHolder.getInstance().addSchematic(schematic, true);
        if(DataManager.getCreatePlacementOnLoad()){
            BlockPos pos = new BlockPos(project.schematicX, project.schematicY, project.schematicZ);
            SchematicPlacementManager manager = DataManager.getSchematicPlacementManager();
            SchematicPlacement placement = SchematicPlacement.createFor(schematic, pos, String.format("Схематика %s", project.title), true, true);
            placement = placement.setRotation(getRotation(project.schematicRotate), lol)
                    .setMirror(getMirror(project.schematicMirror), lol);
            manager.addSchematicPlacement(placement, true);
            manager.setSelectedSchematicPlacement(placement);
        }
    }

    public static Rotation getRotation(String rotate){
        return switch (rotate.toLowerCase()){
            case "cw_90" -> Rotation.CLOCKWISE_90;
            case "cw_180" -> Rotation.CLOCKWISE_180;
            case "ccw_90" -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }
    public static Mirror getMirror(String mirror){
        return switch (mirror.toLowerCase()){
            case "left_right" -> Mirror.LEFT_RIGHT;
            case "front_back" -> Mirror.FRONT_BACK;
            default -> Mirror.NONE;
        };
    }
}

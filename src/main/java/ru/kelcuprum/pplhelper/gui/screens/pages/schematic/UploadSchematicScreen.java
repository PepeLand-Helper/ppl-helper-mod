package ru.kelcuprum.pplhelper.gui.screens.pages.schematic;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.api.PepeLandHelperAPI;
import ru.kelcuprum.pplhelper.api.components.Project;
import ru.kelcuprum.pplhelper.gui.screens.message.ErrorScreen;

import java.io.File;
import java.nio.file.Files;

import static java.lang.Integer.parseInt;
import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.WHITE_PEPE;

public class UploadSchematicScreen extends Screen {
    public final Screen screen;
    public final File file;
    public final Project project;
    public UploadSchematicScreen(Screen screen, Project project, File file) {
        super(Component.literal(file.getName()));
        this.file = file;
        this.screen = screen;
        this.project = project;
    }
    public static String[] schematicRotate = new String[]{
            "none",
            "cw_90",
            "cw_180",
            "ccw_90"
    };
    public static String[] schematicMirror = new String[]{
            "none",
            "left_right",
            "front_back"
    };
    @Override
    protected void init() {
        addRenderableOnly(new TextBuilder(Component.translatable("pplhelper.project.schematic.upload", project.title)).setWidth(width).setPosition(0, 20).build());
        int size = 300;
        int y = height/2 - 45;
        int x = width/2-size/2;
        addRenderableWidget(new TextBuilder(Component.translatable("pplhelper.project.schematic.selected_file", file.getName())).setType(TextBuilder.TYPE.MESSAGE).setSize(size, 40).setPosition(x, y).build());
        y+=45;

        addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.schematic.rotate")).setList(schematicRotate)
                .setOnPress((s) -> project.schematicRotate = schematicRotate[s.getPosition()])
                .setPosition(x, y).setWidth(size/2-2).build());

        addRenderableWidget(new SelectorBuilder(Component.translatable("pplhelper.project.schematic.rotate")).setList(schematicMirror)
                .setOnPress((s) -> project.schematicMirror = schematicMirror[s.getPosition()])
                .setPosition(x+size/2+2, y).setWidth(size/2-2).build());
        y+=25;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.project.schematic.x")).setResponder((s) -> {
            try {
                if(s.isBlank() || s.startsWith("-")) return;
                project.schematicX = parseInt(s);
            } catch (Exception ex) {
                ex.printStackTrace();
                new ToastBuilder().setIcon(WHITE_PEPE).setTitle(Component.literal("PepeLand Helper")).setMessage(Component.literal("Недопустимый символ!"))
                        .setType(ToastBuilder.Type.ERROR).buildAndShow();
            }
        }).setWidth(98).setPosition(x, y).build());
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.project.schematic.y")).setResponder((s) -> {
            try {
                if(s.isBlank() || s.startsWith("-")) return;
                project.schematicY = parseInt(s);
            } catch (Exception ex) {
                ex.printStackTrace();
                new ToastBuilder().setIcon(WHITE_PEPE).setTitle(Component.literal("PepeLand Helper")).setMessage(Component.literal("Недопустимый символ!"))
                        .setType(ToastBuilder.Type.ERROR).buildAndShow();
            }
        }).setWidth(98).setPosition(x+101, y).build());
        addRenderableWidget(new EditBoxBuilder(Component.translatable("pplhelper.project.schematic.z")).setResponder((s) -> {
            try {
                if(s.isBlank() || s.startsWith("-")) return;
                project.schematicZ = parseInt(s);
            } catch (Exception ex) {
                ex.printStackTrace();
                new ToastBuilder().setIcon(WHITE_PEPE).setTitle(Component.literal("PepeLand Helper")).setMessage(Component.literal("Недопустимый символ!"))
                        .setType(ToastBuilder.Type.ERROR).buildAndShow();
            }
        }).setWidth(98).setPosition(x+202, y).build());
        y+=25;
        addRenderableWidget(new ButtonBuilder(CommonComponents.GUI_BACK).setOnPress((s) -> onClose()).setSize(size/2-2,20).setPosition(x, y).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("pplhelper.project.schematic.upload_to_api")).setOnPress((s) -> upload()).setSize(size/2-2,20).setPosition(x+size/2+2, y).build());
    }

    public void upload(){
        try {
            PepeLandHelperAPI.uploadProjectSchematicFile(Files.readAllBytes(file.toPath()), project.id);
            PepeLandHelperAPI.updateProject(project);

        } catch (Exception ex){
            ex.printStackTrace();
            AlinLib.MINECRAFT.setScreen(new ErrorScreen(ex, screen));
        }
    }

    public static void openFileSelect(Screen parent, Project project){
        MemoryStack stack = MemoryStack.stackPush();
        PointerBuffer filters = stack.mallocPointer(2);
        filters.put(stack.UTF8("*.litematic"));
        filters.put(stack.UTF8("*.schematic"));

        filters.flip();
        File defaultPath = AlinLib.MINECRAFT.gameDirectory.toPath().resolve("schematics").toFile().getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator)){
            defaultString += File.separator;
        }

        String result = TinyFileDialogs.tinyfd_openFileDialog(Component.translatable("waterplayer.editor.selector").getString(), defaultString, filters, Component.translatable("waterplayer.editor.selector.filter_description").getString(), false);
        if(result == null) return;
        File file = new File(result);
        if(file.exists()) AlinLib.MINECRAFT.setScreen(new UploadSchematicScreen(parent, project, file));
    }

    @Override
    public void onClose() {
        AlinLib.MINECRAFT.setScreen(screen);
    }
}

package ru.pplh.mod.gui.screens.message;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 12106
import net.minecraft.client.renderer.RenderPipelines;
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.pplh.mod.PepeLandHelper;

public class DownloadScreen extends Screen {
    public Screen parent;
    public JsonObject packData;
    public boolean onlyEmote;
    public boolean modrinth;
    public DownloadScreen(Screen screen, JsonObject object, boolean onlyEmote, boolean modrinth) {
        super(Component.translatable("pplhelper.pack.download_screen"));
        this.parent = screen;
        this.packData = object;
        this.onlyEmote = onlyEmote;
        this.modrinth = modrinth;
    }
    boolean inited = false;
    boolean downloaded = false;
    Exception exception;
    Thread thread;
    long start = 0;
    @Override
    protected void init() {
        if (!inited) {
            start = System.currentTimeMillis();
            inited = true;
            thread = PepeLandHelper.downloadPack(packData, onlyEmote, (s) -> {
                if(s) downloaded = true;
                else exception = new Exception(Component.translatable("pplhelper.pack.file_broken").getString());
            }, modrinth);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractBackground(guiGraphics, i, j, f);
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x7F0A2725, 0x7F134E4A);
        guiGraphics.blit(
                //#if MC >= 12106
                RenderPipelines.GUI_TEXTURED,
                //#elseif MC >= 12102
                //$$ RenderType::guiTextured,
                //#endif
                PepeLandHelper.Icons.WHITE_PEPE, guiGraphics.guiWidth() / 2 - 30, guiGraphics.guiHeight() / 2 - 30, 0, 0, 60, 60, 60, 60);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractRenderState(guiGraphics, i, j, f);
        int y = height / 2 + 30;
        guiGraphics.centeredText(font, title, width/2, y, -1);
        long t = (System.currentTimeMillis()-start) % 4000;
        String dodo = t < 1000 ? "" : t < 2000 ? "." : t < 3000 ? ".." : "...";
        guiGraphics.centeredText(font, Component.empty().append(Component.translatable("pplhelper.pack.download_screen.wait")).append(dodo), width/2, y+15, -1);
    }

    @Override
    public void tick() {
        if(exception != null) {
            AlinLib.MINECRAFT.gui.setScreen(new ErrorScreen(exception, parent));
            return;
        }
        if(downloaded) {
            String fileName = String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString());
            AlinLib.MINECRAFT.getResourcePackRepository().reload();
            for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks()){
                if(pack.getDescription().getString().toLowerCase().contains("pepeland pack"))
                    AlinLib.MINECRAFT.getResourcePackRepository().removePack(pack.getId());
            }
            for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()){
                if(pack.getId().contains(fileName))
                    AlinLib.MINECRAFT.getResourcePackRepository().addPack(pack.getId());
            }
            AlinLib.MINECRAFT.options.updateResourcePacks(AlinLib.MINECRAFT.getResourcePackRepository());
            AlinLib.MINECRAFT.gui.setScreen(parent);

            new ToastBuilder().setTitle(Component.translatable("pplhelper"))
                    .setIcon(PepeLandHelper.Icons.WHITE_PEPE)
                    .setMessage(Component.translatable("pplhelper.pack.downloaded", packData.get("version").getAsString())).buildAndShow();
        }
    }

    @Override
    public void onClose() {
        if(thread != null && !thread.isInterrupted()) thread.interrupt();
        assert this.minecraft != null;
        this.minecraft.gui.setScreen(parent);
    }
}

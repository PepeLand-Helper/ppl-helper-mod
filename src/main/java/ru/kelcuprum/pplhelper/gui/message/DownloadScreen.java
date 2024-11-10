package ru.kelcuprum.pplhelper.gui.message;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.pplhelper.PepelandHelper;
import ru.kelcuprum.pplhelper.api.PepeLandAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DownloadScreen extends Screen {
    public Screen parent;
    public JsonObject packData;
    public boolean onlyEmote;
    public DownloadScreen(Screen screen, JsonObject object, boolean onlyEmote) {
        super(Component.translatable("pplhelper.pack.download_screen"));
        this.parent = screen;
        this.packData = object;
        this.onlyEmote = onlyEmote;
    }
    boolean inited = false;
    boolean downloaded = false;
    Exception exception;
    long start = 0;
    @Override
    protected void init() {
        if (!inited) {
            start = System.currentTimeMillis();
            inited = true;
            new Thread(() -> {
                try {
                    String originalChecksum = packData.get("checksum").getAsString();
                    String path = AlinLib.MINECRAFT.getResourcePackDirectory().resolve(String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString())).toString();
                    File file = new File(path);
                    if(!file.exists()) PepeLandAPI.downloadFile$queue(packData.get("url").getAsString(), AlinLib.MINECRAFT.getResourcePackDirectory().toString(), String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString()), originalChecksum, 5);
                    if(file.exists() && originalChecksum.contains(toSHA(path))){
                        downloaded = true;
                    } else {
                        if(file.exists()) file.delete();
                        throw new RuntimeException(Component.translatable("pplhelper.pack.file_broken").getString());
                    }
                } catch (Exception e) {
                    exception = e;
                }
            }).start();
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x7F0A2725, 0x7F134E4A);
        guiGraphics.blit(PepelandHelper.Icons.WHITE_PEPE, guiGraphics.guiWidth() / 2 - 30, guiGraphics.guiHeight() / 2 - 30, 0, 0, 60, 60, 60, 60);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        int y = height / 2 + 30;
        guiGraphics.drawCenteredString(font, title, width/2, y, -1);
        long t = (System.currentTimeMillis()-start) % 4000;
        String dodo = t < 1000 ? "" : t < 2000 ? "." : t < 3000 ? ".." : "...";
        guiGraphics.drawCenteredString(font, Component.empty().append(Component.translatable("pplhelper.pack.download_screen.wait")).append(dodo), width/2, y+15, -1);
    }

    @Override
    public void tick() {
        if(exception != null) {
            AlinLib.MINECRAFT.setScreen(new ErrorScreen(exception, parent));
            return;
        }
        if(downloaded) {
            String fileName = String.format("pepeland-%1$s-v%2$s.zip", onlyEmote ? "emotes" : "main", packData.get("version").getAsString());
            AlinLib.MINECRAFT.getResourcePackRepository().reload();
            for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getSelectedPacks()){
                if(pack.getDescription().getString().contains("PepeLand Pack"))
                    AlinLib.MINECRAFT.getResourcePackRepository().removePack(pack.getId());
            }
            for(Pack pack : AlinLib.MINECRAFT.getResourcePackRepository().getAvailablePacks()){
                if(pack.getId().contains(fileName))
                    AlinLib.MINECRAFT.getResourcePackRepository().addPack(pack.getId());
            }
            AlinLib.MINECRAFT.options.updateResourcePacks(AlinLib.MINECRAFT.getResourcePackRepository());
            AlinLib.MINECRAFT.setScreen(parent);

            new ToastBuilder().setTitle(Component.translatable("pplhelper"))
                    .setIcon(PepelandHelper.Icons.WHITE_PEPE)
                    .setMessage(Component.translatable("pplhelper.pack.downloaded", packData.get("version").getAsString())).buildAndShow();
        }
    }

    @Override
    public void onClose() {}

    public static String toSHA(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(filePath);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        fis.close();

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

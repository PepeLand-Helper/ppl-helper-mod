
package ru.kelcuprum.pplhelper.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Async;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.pplhelper.PepelandHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.PACK_INFO;

public class TextureHelper {
    public static HashMap<String, ResourceLocation> resourceLocationMap = new HashMap<>();
    public static HashMap<String, Boolean> urls = new HashMap<>();
    public static HashMap<String, DynamicTexture> urlsTextures = new HashMap<>();
    public static JsonArray map = new JsonArray();
    // Internet
    public static ResourceLocation getTexture(String url, String id) {
        return getTexture(url, id, 128, 128);
    }
    public static ResourceLocation getTexture(String url, String id, int width, int height) {
        id = formatUrls(id.toLowerCase());
        if (resourceLocationMap.containsKey(id)) return resourceLocationMap.get(id);
        else {
            if (!urls.getOrDefault(id, false)) {
                urls.put(id, true);
                String finalId = id;
                new Thread(() -> registerTexture(url, finalId, width, height, AlinLib.MINECRAFT.getTextureManager(), GuiUtils.getResourceLocation("pplhelper", finalId))).start();
            }
            return PACK_INFO;
        }
    }

    @Async.Execute
    public static void registerTexture(String url, String id, int width, int height, TextureManager textureManager, ResourceLocation textureId) {
        PepelandHelper.log(String.format("REGISTER: %s %s", url, id), Level.DEBUG);
        DynamicTexture texture;
        if (urlsTextures.containsKey(url)) {
            JsonObject data = new JsonObject();
            data.addProperty("url", url);
            data.addProperty("id", id);
            if (!map.contains(data)) map.add(data);
            texture = urlsTextures.get(url);
        } else {
            NativeImage image;
            File textureFile = getTextureFile(id);
            boolean isFileExists = textureFile.exists();
            try {
                BufferedImage bufferedImage = isFileExists ? ImageIO.read(getTextureFile(id)) : ImageIO.read(new URL(url));
                int widthScale = bufferedImage.getWidth() / width;
                int heightScale = bufferedImage.getHeight() / height;
                int widthScaled = widthScale > 0 ? bufferedImage.getWidth() / widthScale : 1;
                int heightScaled = heightScale > 0 ? bufferedImage.getHeight() / heightScale : 1;
                if (widthScaled > width) {
                    int x = (bufferedImage.getWidth() - width*widthScale) / 2;
                    bufferedImage = bufferedImage.getSubimage(x, 0, width*widthScale, bufferedImage.getHeight());
                }
                if (heightScaled > height) {
                    int x = (bufferedImage.getHeight() - height*heightScale) / 2;
                    bufferedImage = bufferedImage.getSubimage(0, x, bufferedImage.getWidth(), height*heightScale);
                }
                BufferedImage scaleImage = toBufferedImage(bufferedImage.getScaledInstance(width, height, 2));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(scaleImage, "png", byteArrayOutputStream);
                byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
                image = NativeImage.read(bytesOfImage);
                if (!isFileExists) {
                    Files.createDirectories(textureFile.toPath().getParent());
                    Files.write(textureFile.toPath(), bytesOfImage);
                }
            } catch (Exception e) {
                PepelandHelper.log("Error loading image from URL: " + url + " - " + e.getMessage());
                resourceLocationMap.put(id, PACK_INFO);
                return;
            }
            texture = new DynamicTexture(image);
        }
        if (textureManager != null) {
            textureManager.register(textureId, texture);
            resourceLocationMap.put(id, textureId);
            JsonObject data = new JsonObject();
            data.addProperty("url", url);
            data.addProperty("id", id);
            if (!map.contains(data)) map.add(data);
        }
    }



    public static File getTextureFile(String url) {
        return new File("./pplhelper/textures/" + url + ".png");
    }

    public static void saveMap() {
        try {
            Path path = new File("./pplhelper/textures/map.json").toPath();
            Files.createDirectories(path.getParent());
            Files.writeString(path, map.toString());
        } catch (IOException e) {
            PepelandHelper.log(e.getLocalizedMessage(), Level.ERROR);
        }
    }

    public static void loadTextures(TextureManager textureManager) {
        loadMap();
        try {
            final JsonArray finalMap = map;
            for (JsonElement json : finalMap) {
                JsonObject data = json.getAsJsonObject();
                ResourceLocation l = GuiUtils.getResourceLocation("pplhelper", data.get("id").getAsString());
//                registerTexture(data.get("url").getAsString(), data.get("id").getAsString(), textureManager, l);
            }
        } catch (Exception e) {
            PepelandHelper.log("MAP ERROR!", Level.ERROR);
            e.printStackTrace();
        }
    }

    public static void loadMap() {
        File mapFile = new File("./pplhelper/textures/map.json");
        if (mapFile.exists() && mapFile.isFile()) {
            try {
                map = GsonHelper.parseArray(Files.readString(mapFile.toPath()));
            } catch (Exception e) {
                map = new JsonArray();
                PepelandHelper.log(e.getMessage() == null ? e.getClass().getName() : e.getMessage(), Level.ERROR);
            }
        } else map = new JsonArray();
    }

    public static String formatUrls(String url) {
        return url.toLowerCase().replaceAll(" ", "-").replaceAll("[^A-Za-z0-9_-]", "_");
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }
}

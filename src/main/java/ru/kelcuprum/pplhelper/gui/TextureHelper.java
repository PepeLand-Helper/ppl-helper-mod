
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
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.PACK_INFO;

public class TextureHelper {
    public static HashMap<String, ResourceLocation> resourceLocationMap = new HashMap<>();
    public static HashMap<String, Boolean> urls = new HashMap<>();
    public static HashMap<String, DynamicTexture> urlsTextures = new HashMap<>();
    public static HashMap<String, NativeImage> urlsImages = new HashMap<>();
    public static JsonArray map = new JsonArray();
    // Internet
    public static ResourceLocation getTexture(String url, String id) {
        id = formatUrls(id.toLowerCase());
        if (resourceLocationMap.containsKey(id)) return resourceLocationMap.get(id);
        else {
            if (!urls.getOrDefault(id, false)) {
                urls.put(id, true);
                String finalId = id;
                new Thread(() -> registerTexture(url, finalId, AlinLib.MINECRAFT.getTextureManager(), GuiUtils.getResourceLocation("pplhelper", finalId))).start();
            }
            return PACK_INFO;
        }
    }

    @Async.Execute
    public static void registerTexture(String url, String id, TextureManager textureManager, ResourceLocation textureId) {
        PepelandHelper.LOG.debug(String.format("REGISTER: %s %s", url, id));
        DynamicTexture texture;
        if (urlsTextures.containsKey(url)) {
            addToMap(id, url);
            texture = urlsTextures.get(url);
        } else {
            NativeImage image;
            File textureFile = getTextureFile(id);
            boolean isFileExists = textureFile.exists();
            try {
                BufferedImage bufferedImage = isFileExists ? ImageIO.read(getTextureFile(id)) : ImageIO.read(new URL(url));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                image = NativeImage.read(is);
                if (!isFileExists) {
                    Files.createDirectories(textureFile.toPath().getParent());
                    Files.write(textureFile.toPath(), byteArrayOutputStream.toByteArray());
                }
                texture = new DynamicTexture(image);
                urlsTextures.put(url, texture);
                urlsImages.put(url, image);
            } catch (Exception e) {
                PepelandHelper.LOG.log("Error loading image from URL: " + url + " - " + e.getMessage());
                resourceLocationMap.put(id, PACK_INFO);
                return;
            }
        }
        if (textureManager != null) {
            textureManager.register(textureId, texture);
            resourceLocationMap.put(id, textureId);
            addToMap(id, url);
        }
    }

    public static ResourceLocation getBanner(String url, String id) {
        id = formatUrls(id.toLowerCase());
        if (resourceLocationMap.containsKey(id)) return resourceLocationMap.get(id);
        else {
            if (!urls.getOrDefault(id, false)) {
                urls.put(id, true);
                String finalId = id;
                new Thread(() -> registerBanner(url, finalId, AlinLib.MINECRAFT.getTextureManager(), GuiUtils.getResourceLocation("pplhelper", finalId))).start();
            }
            return PACK_INFO;
        }
    }

    @Async.Execute
    public static void registerBanner(String url, String id, TextureManager textureManager, ResourceLocation textureId) {
        PepelandHelper.LOG.log(String.format("REGISTER: %s %s", url, id), Level.DEBUG);
        DynamicTexture texture;
        if (urlsTextures.containsKey(url)) {
            addToMap(id, url);
            texture = urlsTextures.get(url);
        } else {
            NativeImage image;
            File textureFile = getTextureFile(id);
            boolean isFileExists = textureFile.exists();
            try {
                BufferedImage bufferedImage = isFileExists ? ImageIO.read(getTextureFile(id)) : ImageIO.read(new URL(url));
                double widthScale = (double) bufferedImage.getWidth() / 750;
                int threeHundredBucks = (int) (300 * widthScale);
                double scale = (double) bufferedImage.getHeight() / threeHundredBucks;
                int height = (int) (bufferedImage.getHeight() / scale);
                if (bufferedImage.getHeight() > height && !isFileExists) {
                    int y = (bufferedImage.getHeight() - height) / 2;
                    bufferedImage = bufferedImage.getSubimage(0, y, bufferedImage.getWidth(), bufferedImage.getHeight()-(y*2));
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                image = NativeImage.read(is);
                if (!isFileExists) {
                    Files.createDirectories(textureFile.toPath().getParent());
                    Files.write(textureFile.toPath(), byteArrayOutputStream.toByteArray());
                }
                texture = new DynamicTexture(image);
                urlsTextures.put(url, texture);
                urlsImages.put(url, image);
            } catch (Exception e) {
                PepelandHelper.LOG.log("Error loading image from URL: " + url + " - " + e.getMessage());
                resourceLocationMap.put(id, PACK_INFO);
                return;
            }
        }
        if (textureManager != null) {
            textureManager.register(textureId, texture);
            resourceLocationMap.put(id, textureId);
            addToMap(id, url);
        }
    }

    public static void addToMap(String id, String url){
        JsonObject data = new JsonObject();
        data.addProperty("url", url);
        data.addProperty("id", id);
        if (!map.contains(data)) map.add(data);
    }

    public static File getTextureFile(String url) {
        return new File("./config/pplhelper/textures/" + url + ".png");
    }

    public static void saveMap() {
        try {
            Path path = new File("./config/pplhelper/textures/map.json").toPath();
            Files.createDirectories(path.getParent());
            Files.writeString(path, map.toString());
        } catch (IOException e) {
            PepelandHelper.LOG.log(e.getLocalizedMessage(), Level.ERROR);
        }
    }

    public static void loadTextures(TextureManager textureManager) {
        loadMap();
        try {
            final JsonArray finalMap = map;
            for (JsonElement json : finalMap) {
                JsonObject data = json.getAsJsonObject();
                ResourceLocation l = GuiUtils.getResourceLocation("pplhelper", data.get("id").getAsString());
                registerTexture(data.get("url").getAsString(), data.get("id").getAsString(), textureManager, l);
            }
        } catch (Exception e) {
            PepelandHelper.LOG.log("MAP ERROR!", Level.ERROR);
            e.printStackTrace();
        }
    }

    public static void loadMap() {
        File mapFile = new File("./config/pplhelper/textures/map.json");
        JsonArray finalMap;
        if (mapFile.exists() && mapFile.isFile()) {
            try {
                finalMap = GsonHelper.parseArray(Files.readString(mapFile.toPath()));
            } catch (Exception e) {
                finalMap = new JsonArray();
                PepelandHelper.LOG.log(e.getMessage() == null ? e.getClass().getName() : e.getMessage(), Level.ERROR);
            }
        } else finalMap = new JsonArray();
        JsonArray array = new JsonArray();
        for(JsonElement element : finalMap){
            JsonObject jsonObject = element.getAsJsonObject();
            File file = getTextureFile(jsonObject.get("id").getAsString());
            try {
                if (file.exists() && System.currentTimeMillis() - Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toMillis() >= 259200000)
                    file.deleteOnExit();
                else array.add(element);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        map = array;
    }

    public static String formatUrls(String url) {
        return url.toLowerCase().replaceAll(" ", "-").replaceAll("[^A-Za-z0-9_-]", "_");
    }
}

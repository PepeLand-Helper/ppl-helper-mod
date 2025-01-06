
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
import static ru.kelcuprum.pplhelper.PepelandHelper.Icons.WHITE_PEPE;

public class TextureHelper {
    public static HashMap<String, ResourceLocation> resourceLocationMap = new HashMap<>();
    public static HashMap<String, Boolean> urls = new HashMap<>();
    public static HashMap<String, DynamicTexture> urlsTextures = new HashMap<>();
    public static HashMap<String, NativeImage> urlsImages = new HashMap<>();
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
            return WHITE_PEPE;
        }
    }

    @Async.Execute
    public static void registerTexture(String url, String id, TextureManager textureManager, ResourceLocation textureId) {
        PepelandHelper.LOG.debug(String.format("REGISTER: %s %s", url, id));
        DynamicTexture texture;
        if (urlsTextures.containsKey(url)) {
            texture = urlsTextures.get(url);
        } else {
            NativeImage image;
            try {
                BufferedImage bufferedImage = ImageIO.read(new URL(url));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, url.contains(".webp") ? "webp" : "png", byteArrayOutputStream);
                InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                image = NativeImage.read(is);
                texture = new DynamicTexture(image);
                urlsTextures.put(url, texture);
                urlsImages.put(url, image);
            } catch (Exception e) {
                PepelandHelper.LOG.error("Error loading image from URL: " + url + " - " + e.getMessage());
                resourceLocationMap.put(id, PACK_INFO);
                return;
            }
        }
        if (textureManager != null) {
            textureManager.register(textureId, texture);
            resourceLocationMap.put(id, textureId);
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
            return WHITE_PEPE;
        }
    }

    @Async.Execute
    public static void registerBanner(String url, String id, TextureManager textureManager, ResourceLocation textureId) {
        PepelandHelper.LOG.debug(String.format("REGISTER: %s %s", url, id));
        DynamicTexture texture;
        if (urlsTextures.containsKey(url))
            texture = urlsTextures.get(url);
        else {
            NativeImage image;
            try {
                BufferedImage bufferedImage = ImageIO.read(new URL(url));
                double widthScale = (double) bufferedImage.getWidth() / 750;
                int threeHundredBucks = (int) (300 * widthScale);
                double scale = (double) bufferedImage.getHeight() / threeHundredBucks;
                int height = (int) (bufferedImage.getHeight() / scale);
                if (bufferedImage.getHeight() > height) {
                    int y = (bufferedImage.getHeight() - height) / 2;
                    bufferedImage = bufferedImage.getSubimage(0, y, bufferedImage.getWidth(), bufferedImage.getHeight()-(y*2));
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                image = NativeImage.read(is);
                texture = new DynamicTexture(image);
                urlsTextures.put(url, texture);
                urlsImages.put(url, image);
            } catch (Exception e) {
                PepelandHelper.LOG.error("Error loading image from URL: " + url + " - " + e.getMessage());
                resourceLocationMap.put(id, PACK_INFO);
                return;
            }
        }
        if (textureManager != null) {
            textureManager.register(textureId, texture);
            resourceLocationMap.put(id, textureId);
        }
    }

    public static String formatUrls(String url) {
        return url.toLowerCase().replaceAll(" ", "-").replaceAll("[^A-Za-z0-9_-]", "_");
    }
}

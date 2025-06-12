
package ru.kelcuprum.pplhelper.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Async;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.pplhelper.PepeLandHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.PACK_INFO;
import static ru.kelcuprum.pplhelper.PepeLandHelper.Icons.WHITE_PEPE;

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

    public static void registerTexture(String url, String id, TextureManager textureManager, ResourceLocation textureId) {
        PepeLandHelper.LOG.debug(String.format("REGISTER: %s %s", url, id));
        AtomicReference<DynamicTexture> texture = new AtomicReference<>();
        if (urlsTextures.containsKey(url)) {
            texture.set(urlsTextures.get(url));
        } else {
            NativeImage image;
            try {
                BufferedImage bufferedImage = ImageIO.read(new URL(url));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, url.contains(".webp") ? "webp" : "png", byteArrayOutputStream);
                InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                image = NativeImage.read(is);
                Minecraft.getInstance().execute(() -> {
                    //#if MC >= 12105
                    texture.set(new DynamicTexture(() -> id, image));
                    //#else
                    //$$ texture.set(new DynamicTexture(image));
                    //#endif
                });
                urlsTextures.put(url, texture.get());
                urlsImages.put(url, image);
            } catch (Exception e) {
                PepeLandHelper.LOG.error("Error loading image from URL: " + url + " - " + e.getMessage());
                resourceLocationMap.put(id, PACK_INFO);
                return;
            }
        }
        if (textureManager != null) {
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().register(textureId, texture.get()));
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

    public static void registerBanner(String url, String id, TextureManager textureManager, ResourceLocation textureId) {
        PepeLandHelper.LOG.debug(String.format("REGISTER: %s %s", url, id));
        AtomicReference<DynamicTexture> texture = new AtomicReference<>();
        if (urlsTextures.containsKey(url))
            texture.set(urlsTextures.get(url));
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
                Minecraft.getInstance().execute(() -> {
                    //#if MC >= 12105
                    texture.set(new DynamicTexture(() -> id, image));
                    //#else
                    //$$ texture.set(new DynamicTexture(image));
                    //#endif
                });
                urlsTextures.put(url, texture.get());
                urlsImages.put(url, image);
            } catch (Exception e) {
                PepeLandHelper.LOG.error("Error loading image from URL: " + url + " - " + e.getMessage());
                resourceLocationMap.put(id, PACK_INFO);
                return;
            }
        }
        if (textureManager != null) {
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().getTextureManager().register(textureId, texture.get()));
            resourceLocationMap.put(id, textureId);
        }
    }

    public static String formatUrls(String url) {
        return url.toLowerCase().replaceAll(" ", "-").replaceAll("[^A-Za-z0-9_-]", "_");
    }
}

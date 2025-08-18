package ru.pplh.mod.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.GsonHelper;
import ru.pplh.mod.PepeLandHelper;

public class WebUtils {
    public static HttpClient httpClient;

    public static String getString(HttpRequest request) throws IOException, InterruptedException {
        if (httpClient == null) {
            httpClient = HttpClient.newBuilder().version(Version.HTTP_2).connectTimeout(Duration.ofSeconds(10L)).build();
        }

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        return (String)response.body();
    }

    public static String getString(HttpRequest.Builder url) throws IOException, InterruptedException {
        return getString(setupHeaders(url).build());
    }

    public static String getString(String url) throws IOException, InterruptedException {
        return getString(setupHeaders(HttpRequest.newBuilder().uri(URI.create(url))).build());
    }
    // URL
    public static JsonObject getJsonObject(String url) throws IOException, InterruptedException {
        return getJsonObject(setupHeaders(HttpRequest.newBuilder().uri(URI.create(url))).header("Content-Type", "application/json").build());
    }

    public static JsonArray getJsonArray(String url) throws IOException, InterruptedException {
        return getJsonArray(setupHeaders(HttpRequest.newBuilder().uri(URI.create(url))).header("Content-Type", "application/json").build());
    }
    // REQUEST
    public static JsonObject getJsonObject(HttpRequest url) throws IOException, InterruptedException {
        return GsonHelper.parse(getString(url));
    }

    public static JsonArray getJsonArray(HttpRequest url) throws IOException, InterruptedException {
        return GsonHelper.parseArray(getString(url));
    }
    // BUILDER
    public static JsonObject getJsonObject(HttpRequest.Builder url) throws IOException, InterruptedException {
        return getJsonObject(setupHeaders(url).header("Content-Type", "application/json").build());
    }

    public static JsonArray getJsonArray(HttpRequest.Builder url) throws IOException, InterruptedException {
        return getJsonArray(setupHeaders(url).header("Content-Type", "application/json").build());
    }

    public static HttpRequest.Builder setupHeaders(HttpRequest.Builder builder){
        String token = PepeLandHelper.config.getString("oauth.access_token", "");
        if(!token.isBlank()) builder.header("pplh_access_token", token);
        builder.header("pplh_version", FabricLoader.getInstance().getModContainer("pplhelper").get().getMetadata().getVersion().getFriendlyString());
        return builder;
    }

    static {
        httpClient = HttpClient.newBuilder().version(Version.HTTP_2).connectTimeout(Duration.ofSeconds(2L)).build();
    }
}
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
import net.minecraft.util.GsonHelper;

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
        return getString(url.build());
    }

    public static String getString(String url) throws IOException, InterruptedException {
        return getString(HttpRequest.newBuilder().uri(URI.create(url)).build());
    }

    public static JsonObject getJsonObject(String url) throws IOException, InterruptedException {
        return getJsonObject(HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").build());
    }

    public static JsonArray getJsonArray(String url) throws IOException, InterruptedException {
        return getJsonArray(HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").build());
    }

    public static JsonObject getJsonObject(HttpRequest url) throws IOException, InterruptedException {
        return GsonHelper.parse(getString(url));
    }

    public static JsonArray getJsonArray(HttpRequest url) throws IOException, InterruptedException {
        return GsonHelper.parseArray(getString(url));
    }

    public static JsonObject getJsonObject(HttpRequest.Builder url) throws IOException, InterruptedException {
        return getJsonObject(url.header("Content-Type", "application/json").build());
    }

    public static JsonArray getJsonArray(HttpRequest.Builder url) throws IOException, InterruptedException {
        return getJsonArray(url.header("Content-Type", "application/json").build());
    }

    static {
        httpClient = HttpClient.newBuilder().version(Version.HTTP_2).connectTimeout(Duration.ofSeconds(2L)).build();
    }
}
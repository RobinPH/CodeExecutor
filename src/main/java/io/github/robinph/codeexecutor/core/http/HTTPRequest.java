package io.github.robinph.codeexecutor.core.http;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HTTPRequest {
    public static JsonObject get(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");

            JsonObject response = new JsonObject();

            response.addProperty("code", con.getResponseCode());
            response.addProperty("response", new JsonObject().toString());
            if (con.getResponseCode() == 400) {
                return response;
            }

            response.addProperty("response", String.join("\n", HTTPRequest.getInputStream(con)));

            return response;

        } catch (IOException e) {
            JsonObject response = new JsonObject();

            response.addProperty("code", -1);
            response.addProperty("response", new JsonObject().toString());

            return response;
        }
    }

    public static JsonObject post(String url, JsonObject request) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = request.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            JsonObject response = new JsonObject();

            response.addProperty("code", con.getResponseCode());
            response.addProperty("response", new JsonObject().toString());
            if (con.getResponseCode() == 400) {
                return response;
            }

            response.addProperty("response", String.join("\n", HTTPRequest.getInputStream(con)));

            return response;

        } catch (IOException e) {
            JsonObject response = new JsonObject();
            response.addProperty("code", -1);
            response.addProperty("response", new JsonObject().toString());

            return response;
        }
    }

    public static List<String> getInputStream(HttpURLConnection con) throws IOException {
        List<String> lines = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }
}

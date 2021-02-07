package io.github.robinph.codeexecutor.pastebin;

import com.google.gson.JsonObject;
import io.github.robinph.codeexecutor.core.http.HTTPRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PastebinAPI {
    public static String fetchPaste(String url) {
        if (!PastebinAPI.validateURL(url)) {
            return null;
        }

        Pattern pattern = Pattern.compile("^((?:https://)?pastebin.com/)?([a-zA-Z0-9_]{8})(?:/)?");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String pasteID = matcher.group(2);
            JsonObject response = HTTPRequest.get("https://pastebin.com/raw/" + pasteID);
            int code = response.get("code").getAsInt();

            if (code == 200) {
                return response.get("response").getAsString();
            }
        }

        return null;
    }

    public static boolean validateURL(String url) {
        Pattern pattern = Pattern.compile("^((?:https://)?pastebin.com/)?([a-zA-Z0-9_]{8})(?:/)?");
        Matcher matcher = pattern.matcher(url);

        return matcher.matches();
    }
}

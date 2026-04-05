package skillpoints;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WynnUtils {
	// TODO: local store and cache json
//	public final static Map<String, WynnItem> items = loadItems("https://api.wynncraft.com/v3/item/database?fullResult");
	
    private static Map<String, WynnItem> loadItems(String url) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response;
        try {
            response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());	
        } catch (Exception e) {
        	return null;
        }

        JsonObject root = JsonParser.parseReader(
                new InputStreamReader(response.body(), StandardCharsets.UTF_8)
        ).getAsJsonObject();

        JsonArray itemsArray = root.getAsJsonArray("items");
        Map<String, WynnItem> items = new HashMap<String, WynnItem>();
        for (int i = 0; i < itemsArray.size(); ++i) {
        	JsonElement element = itemsArray.get(i);
            JsonObject obj = element.getAsJsonObject();

            String name = obj.get("name").getAsString();

            int[] req = new int[WynnItem.NUM_SKILLPOINTS];
            int[] bonus = new int[WynnItem.NUM_SKILLPOINTS];

            req[0] = getInt(obj, "strReq");
            req[1] = getInt(obj, "dexReq");
            req[2] = getInt(obj, "intReq");
            req[3] = getInt(obj, "defReq");
            req[4] = getInt(obj, "agiReq");

            // Bonuses
            bonus[0] = getInt(obj, "str");
            bonus[1] = getInt(obj, "dex");
            bonus[2] = getInt(obj, "int");
            bonus[3] = getInt(obj, "def");
            bonus[4] = getInt(obj, "agi");

            items.put(name, new WynnItem(req, bonus));
        }
        return items;
    }
    private static int getInt(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsInt() : 0;
    }
}

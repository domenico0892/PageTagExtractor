package it.uniroma3.services;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.bson.Document;
import org.json.JSONObject;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/**
 * Created by Domenico on 22/08/16.
 */
public class OpenCalaisClient {
    private static final String SERVICE_URL = "https://api.thomsonreuters.com/permid/calais";
    private static final String TOKEN = "pObvraIguRooYpnwB2QlcrwxrtDmy7yJ";

    public String callReturnString(String text) {
        HttpResponse<String> response;
        try {
            response = Unirest.post(SERVICE_URL)
                    .header("content-type", "text/raw")
                    .header("outputformat", "application/json")
                    .header("x-ag-access-token", TOKEN)
                    .header("omitoutputtingoriginaltext", "true")
                    .body(text)
                    .asString();
            if (response.getStatus() == 429) {
                System.err.println("Superato il limite giornaliero!");
                exit(429);
            }
            return response.getBody();
        } catch (UnirestException e) {
            System.err.println("Errore nella risposta di OpenCalais");
            return null;
        }
    }

    public JSONObject callReturnJsonObject (String text) {
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.post(SERVICE_URL)
                    .header("content-type", "text/raw")
                    .header("outputformat", "application/json")
                    .header("x-ag-access-token", TOKEN)
                    .header("omitoutputtingoriginaltext", "true")
                    .body(text)
                    .asJson();
            if (response.getStatus() == 429) {
                System.err.println("Superato il limite giornaliero!");
                exit(429);
            }
            return response.getBody().getObject();

        } catch (UnirestException e) {
            System.err.println("Errore nella risposta di OpenCalais");
            return null;
        }
    }

    public JSONObject callReturnJsonObjectSelectRegex (String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m;
        JSONObject response = this.callReturnJsonObject(text);
        if (response != null) {
            Set<String> keys = response.keySet();
            for (String key : keys) {
                m = p.matcher(key);
                if (m.matches())
                    return (JSONObject) response.get(key);
            }
        }
        return null;
    }

    public String callReturnTopic (String text) {
        return this.callReturnJsonObjectSelectRegex(text, ".*/cat.*").get("name").toString().replace("_", " ");
    }

    public Document callReturnDocument(String text) {
        String response = this.callReturnString(text);
        System.out.println(response);
        Document doc = Document.parse(response);
        return doc;
    }
}

package it.uniroma3.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.bson.Document;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by ai-lab on 27/07/16.
 */
public class TagMeClient {

    private static final String SERVICE_URL = "https://tagme.d4science.org/tagme/tag";
    private static final String TOKEN = "1a7be2b0-0fcc-4316-9a29-7212eb280c5f";

    public String callReturnString (String incAb, String incCat, String text) {
        try {
            HttpResponse<String> response = Unirest.get(SERVICE_URL)
                    .queryString("include_abstract", incAb)
                    .queryString("include_categories", incCat)
                    .queryString("gcube-token", TOKEN)
                    .queryString("text", text)
                    .asString();
            return response.getBody();
        } catch (UnirestException e) {
            System.err.println("Errore nella risposta di TagMe");
            return null;
        }

    }

    /*public String callReturnString(String incAb, String incCat, String text) {
        this.hello = client.target(SERVICE_URL).queryParam.queryParam
                .queryParam.queryParam;
        String result = hello.request().accept(MediaType.APPLICATION_JSON).get().readEntity(String.class);
        return result;
    }*/

    public Document callReturnDocument(String incAb, String incCat, String text) {
        String response = this.callReturnString(incAb, incCat, text);
        System.out.println(response);
        Document doc = Document.parse(response);
        return doc;
    }
}
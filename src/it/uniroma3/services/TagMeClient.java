package it.uniroma3.services;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Created by ai-lab on 27/07/16.
 */
public class TagMeClient {

    private static final String SERVICE_URL = "https://tagme.d4science.org/tagme/tag";
    private static final String TOKEN = "1a7be2b0-0fcc-4316-9a29-7212eb280c5f";
    private Client client;
    private WebTarget hello;

    public TagMeClient() {
        this.client = ClientBuilder.newClient();
    }

    public String callReturnString(String incAb, String incCat, String text) {
        this.hello = client.target(SERVICE_URL).queryParam("include_abstract", incAb).queryParam("include_categories", incCat)
                .queryParam("gcube-token", TOKEN).queryParam("text", text);
        String result = hello.request().accept(MediaType.APPLICATION_JSON).get().readEntity(String.class);
        return result;
    }

    public Document callReturnDocument(String incAb, String incCat, String text) {
        String response = this.callReturnString(incAb, incCat, text);
        System.out.println(response);
        Document doc = Document.parse(response);
        return doc;
    }

    public void close() {
        client.close();
    }

}
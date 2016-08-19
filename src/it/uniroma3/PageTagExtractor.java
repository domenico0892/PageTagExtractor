package it.uniroma3;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import it.uniroma3.db.MongoConnection;
import it.uniroma3.services.BoilerpipeService;
import it.uniroma3.services.HtmlDownloader;
import it.uniroma3.services.TagMeClient;
import org.bson.Document;
import java.util.ArrayList;

/**
 * Created by ai-lab on 27/07/16.
 */
public class PageTagExtractor {

    public static void main(String[] args) {

        MongoCollection<Document> collTweet = MongoConnection.getInstance().getClient().getDatabase("export").getCollection("export");
        MongoCollection<Document> collPagine = MongoConnection.getInstance().getClient().getDatabase("pagine").getCollection("pagine");
        HtmlDownloader htmlDownloader = new HtmlDownloader();
        TagMeClient tagMeClient = new TagMeClient();
        BoilerpipeService boilerpipeService = new BoilerpipeService();
        Document doc, doc2;

        while (true) {

            //cerco i tweet non ancora analizzati che hanno almeno un url condiviso
            FindIterable<Document> tweets = collTweet.find(Document.parse("{$and: [{\"is_analyzed\": {$exists: false}}, {\"urls\": {$not: {$size: 0}}}]}"));

            //per ciascun tweet trovato...
            for (Document tweet : tweets) {

                //estraggo i tag dal testo del tweet e lo aggiorno
                String cleanedTweet="";
                cleanedTweet = tweet.getString("cleanedTweet");
                doc = tagMeClient.callReturnDocument("true", "true", cleanedTweet);
                collTweet.replaceOne(new Document().append("_id", tweet.getObjectId("_id")), tweet.append("tagme", doc).append("is_analyzed", "true"));

                //gestione della pagina
                ArrayList<String> urls = (ArrayList<String>) tweet.get("urls");
                for (String url : urls) {
                    System.out.println("Url: " + url);
                    FindIterable<Document> page = collPagine.find(Document.parse("{\"url\":\"" + url + "\"}"));

                    //se la pagina non è presente la scarico
                    if (!page.iterator().hasNext()) {
                        HtmlPage hp = htmlDownloader.getPageFromUrl(url);
                        String html = hp.getWebResponse().getContentAsString();
                        String urlVero = hp.getWebResponse().getWebRequest().getUrl().toString();
                        String testo = boilerpipeService.getCleanedText(html);
                        collPagine.insertOne(new Document().append("url", url).append("urlVero", urlVero).append("html", html).append("testo",testo));
                    }
                    page = collPagine.find(Document.parse("{\"url\":\"" + url + "\"}"));
                    doc = page.iterator().next();

                    //controllo se il testo del tweet si trova nel testo della pagina
                    boolean contains = false;
                    if (doc.getString("testo").toLowerCase().contains(cleanedTweet.toLowerCase().substring(5)))
                        contains = true;

                    doc2 = tagMeClient.callReturnDocument("true", "true", (String) doc.get("testo"));
                    collPagine.replaceOne(new Document().append("_id", doc.getObjectId("_id")), doc.append("tagme", doc2));
                    if (!(tweet.containsKey("containsPageText") && tweet.getBoolean("containsPageText").equals(true)))
                        collTweet.replaceOne(new Document().append("_id", tweet.getObjectId("_id")), tweet.append("containsPageText", contains));
                }
                break;
            }
            break;
        }
    }
}
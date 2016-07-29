import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonArray;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by ai-lab on 27/07/16.
 */
public class PageTagExtractor {

    public static void main(String[] args) {

        MongoCollection<Document> collTweet = MongoConnection.getInstance().getClient().getDatabase("twitter").getCollection("tweets");
        MongoCollection<Document> collPagine = MongoConnection.getInstance().getClient().getDatabase("pagine").getCollection("pagine");
        HtmlDownloader htmlDownloader = new HtmlDownloader();
        TagMeClient tagMeClient = new TagMeClient();
        Document doc, doc2;


        while (true) {

            //cerco i tweet non ancora analizzati
            FindIterable<Document> tweets = collTweet.find(Document.parse("{$and: [{\"is_analyzed\": {$exists: false}}, {\"urls\": {$not: {$size: 0}}}]}"));

            //per ciascun tweet...
            for (Document tweet : tweets) {

                //estraggo i tag dal testo del tweet e lo aggiorno
                doc = tagMeClient.callReturnDocument(tweet.getString("lang"), "true", "true", tweet.getString("cleanedTweet"));
                //collTweet.replaceOne(new Document().append("_id", tweet.getObjectId("_id")), tweet.append("tagme", doc).append("is_analyzed", "true"));

                //gestione della pagina
                ArrayList<String> urls = (ArrayList<String>) tweet.get("urls");
                for (String url : urls) {
                    System.out.println("Url: " + url);
                    FindIterable<Document> page = collPagine.find(Document.parse("{\"url\":\"" + url + "\"}"));
                    if (!page.iterator().hasNext()) { //vuol dire che la pagina non è presente
                        HtmlPage hp = htmlDownloader.getPageFromUrl(url);
                        String html = hp.getWebResponse().getContentAsString();
                        String urlVero = hp.getWebResponse().getWebRequest().getUrl().toString();
                        String testo = hp.asText();
                        collPagine.insertOne(new Document().append("url", url).append("urlVero", urlVero).append("html", html).append("testo",testo));
                    }
                    page = collPagine.find(Document.parse("{\"url\":\"" + url + "\"}"));
                    doc = page.iterator().next();
                    doc2 = tagMeClient.callReturnDocument(tweet.getString("lang"), "true", "true", (String) doc.get("testo"));
                    collPagine.replaceOne(new Document().append("_id", doc.getObjectId("_id")), doc.append("tagme", doc));
                }
                break;
            }
            break;
        }
    }
}
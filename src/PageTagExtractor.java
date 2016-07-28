import com.gargoylesoftware.htmlunit.WebResponse;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonArray;
import org.bson.Document;

import java.util.Iterator;

/**
 * Created by ai-lab on 27/07/16.
 */
public class PageTagExtractor {

    public static void main(String[] args) {

        MongoCollection collTweet;
        MongoCollection collPagine;
        HtmlDownloader htmlDownloader = new HtmlDownloader();
        TagMeClient tagMeClient = new TagMeClient();
        Document doc;

        while (true) {

            //cerco i tweet non ancora analizzati
            collTweet = MongoConnection.getInstance().getClient().getDatabase("twitter").getCollection("tweets");
            FindIterable<Document> tweets = collTweet.find(Document.parse("{$and: [{\"is_analyzed\": {$exists: false}}, {\"urls\": {$not: {$size: 0}}}]}"));

            //per ciascun tweet...
            for (Document tweet:tweets) {

                //estraggo i tag dal testo del tweet e lo aggiorno
                doc = tagMeClient.callReturnDocument(tweet.getString("lang"), "true", "true", tweet.getString("cleanedTweet"));
                collTweet.replaceOne(new Document().append("_id", tweet.getObjectId("_id")), tweet.append("tagme", doc));

                //gestione della pagina
                BsonArray urls = (BsonArray) tweet.get("urls");
                Iterator itUrls = urls.iterator();
                while (itUrls.hasNext()) {
                    String url = (String) itUrls.next();
                    collPagine = MongoConnection.getInstance().getClient().getDatabase("pagine").getCollection("pagine");
                    FindIterable<Document> page = collPagine.find(Document.parse("{\"url\":"+url+"}"));
                    if (!page.iterator().hasNext()) { //vuol dire che la pagina non è presente
                        WebResponse w = htmlDownloader.getPageFromUrl(url).getWebResponse();
                        String html = w.getContentAsString();
                        String urlVero = w.getWebRequest().getUrl().toString();
                        collPagine.insertOne(new Document().append("url", url).append("urlVero", urlVero).append("html", html));
                    }







                }
            }
        }
    }
}
package it.uniroma3;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import it.uniroma3.db.MongoConnection;
import it.uniroma3.model.PaginaHtml;
import it.uniroma3.services.BoilerpipeService;
import it.uniroma3.services.HtmlUnitDownloader;
import it.uniroma3.services.OpenCalaisClient;
import it.uniroma3.services.TagMeClient;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by Domenico on 23/08/16.
 */
public class PageTagExtratorThread implements Runnable {

    private MongoCollection<Document> collTweet;
    private MongoCollection<Document> collPagine;
    private HtmlUnitDownloader htmlUnitDownloader;
    private TagMeClient tagMeClient;
    private BoilerpipeService boilerpipeService;
    private OpenCalaisClient openCalaisClient;
    private final String queryAnalyzedAndUrls = "{$and: [{\"is_analyzed\": {$exists: false}}, {\"urls\": {$not: {$size: 0}}}]}";
    private final String queryAnalyzed = "{\"is_analyzed\": {$exists: false}}";

    public PageTagExtratorThread() {
        this.collTweet = MongoConnection.getInstance().getClient().getDatabase("export").getCollection("export");
        this.collPagine = MongoConnection.getInstance().getClient().getDatabase("pagine").getCollection("pagine");
        this.htmlUnitDownloader = new HtmlUnitDownloader();
        this.tagMeClient = new TagMeClient();
        this.boilerpipeService = new BoilerpipeService();
        this.openCalaisClient = new OpenCalaisClient();
    }

    @Override
    public void run() {

        Document doc;
        String cleanedTweet = "";
        PaginaHtml paginaHtml;

        //cerco i tweet non ancora analizzati
        FindIterable<Document> tweets = collTweet.find(Document.parse(queryAnalyzed));
        for (Document tweet : tweets) {
            if (tweet.containsKey("urls")) {

                //se il tweet condivide almeno un url
                ArrayList<String> urls = (ArrayList<String>) tweet.get("urls");
                for (String url : urls) {
                    System.out.println("Url: " + url);
                    FindIterable<Document> page = collPagine.find(Document.parse("{\"url\":\"" + url + "\"}"));

                    //se la pagina non è presente la scarico
                    if (!page.iterator().hasNext()) {
                        paginaHtml = htmlUnitDownloader.getPaginaFromUrl(url);
                        paginaHtml.setTesto(boilerpipeService.getCleanedText(paginaHtml.getHtml()));
                        paginaHtml.setTagMe(tagMeClient.callReturnDocument("true", "true", paginaHtml.getTesto()));
                        paginaHtml.setTopic(openCalaisClient.callReturnTopic(paginaHtml.getTesto()));
                        collPagine.insertOne(paginaHtml.getBsonDocument());
                    }

                    //estraggo i tag dal testo del tweet e lo aggiorno
                    cleanedTweet = tweet.getString("cleanedTweet");
                    doc = tagMeClient.callReturnDocument("true", "true", cleanedTweet);
                    collTweet.replaceOne(new Document().append("_id", tweet.getObjectId("_id")), tweet
                            .append("tagme", doc)
                            .append("topic_opencalais", openCalaisClient.callReturnTopic(cleanedTweet))
                            .append("is_analyzed", "true"));

                    System.out.println("\n\n*** PAGINA ANALIZZATA, PASSO ALLA PROSSIMA!\n\n");
                }
                System.out.println("\n\n*** TWEET ANALIZZATO, PASSO AL PROSSIMO!\n\n");
            }
        }
        System.out.println("\n\n*** FINE!\n\n");
    }
}

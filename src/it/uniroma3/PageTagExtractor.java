package it.uniroma3;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
 * Created by ai-lab on 27/07/16.
 */
public class PageTagExtractor {

    public static void main(String[] args) {
        PageTagExtratorThread p = new PageTagExtratorThread();
        p.run();
    }
}


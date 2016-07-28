import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

/**
 * Created by ai-lab on 27/07/16.
 */
public class HtmlDownloader {

    private WebClient webClient;

    public HtmlDownloader () {
        this.webClient = new WebClient();
        this.webClient.getOptions().setUseInsecureSSL(true);
        this.webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
        this.webClient.waitForBackgroundJavaScript(30000);
        this.webClient.waitForBackgroundJavaScriptStartingBefore(30000);
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(true);
        this.webClient.getOptions().setRedirectEnabled(true);
    }

    public HtmlPage getPageFromUrl (String url) {
        HtmlPage page = null;
        try {
            page = webClient.getPage("https://t.co/DAf9OoPgg8");
        } catch (IOException e) {
            System.err.println("Errore nel download della pagina");
        }
       return page;
    }
}

    /*
                System.out.println(page.asText());
                System.out.println(response.getWebRequest().getUrl());
                String content = response.getContentAsString();
//System.out.println(content);*/
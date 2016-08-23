package it.uniroma3.services;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import it.uniroma3.model.PaginaHtml;

import java.io.IOException;

/**
 * Created by ai-lab on 27/07/16.
 */
public class HtmlUnitDownloader {

    private WebClient webClient;

    public HtmlUnitDownloader() {
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
        this.webClient.getOptions().setTimeout(30000);
    }

    public PaginaHtml getPaginaFromUrl (String url) {
        HtmlPage page = null;
        try {
            page = webClient.getPage(url);
            PaginaHtml paginaHtml = new PaginaHtml();
            paginaHtml.setUrl(url);
            paginaHtml.setUrlVero(page.getWebResponse().getWebRequest().getUrl().toString());
            paginaHtml.setHtml(page.getWebResponse().getContentAsString());
            return paginaHtml;
        } catch (IOException e) {
            System.err.println("Errore nel download della pagina");
            return null;
        }
    }
}

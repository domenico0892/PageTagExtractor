package it.uniroma3.services;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * Created by domenico on 06/08/16.
 */
public class BoilerpipeService {

    private ArticleExtractor articleExtractor;

    public BoilerpipeService () {
        this.articleExtractor = new ArticleExtractor();
    }

    public String getCleanedText(String text) {
        try {
            return this.articleExtractor.getText(text);
        } catch (BoilerpipeProcessingException e) {
            System.err.println("Errore nel processamento dell'HTML");
        }
        return null;
    }
}

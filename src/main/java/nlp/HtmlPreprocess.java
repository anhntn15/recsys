package nlp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlPreprocess {

    /**
     * Get cleaned text from html text
     *
     * @param text html text
     * @return cleaned text
     */
    public static String removeHtmlTags(String text) {
        Document doc = Jsoup.parse(text);
        return doc.text();
    }
}

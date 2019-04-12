package nlp;

import java.util.Map;

public interface ContentSimilarity {
    Double getSimilar(Map<String, Double> vec1, Map<String, Double> vec2);
}

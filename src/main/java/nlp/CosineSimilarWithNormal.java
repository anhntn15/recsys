package nlp;

import java.util.Map;

public class CosineSimilarWithNormal implements ContentSimilarity {

    public Double getSimilar(Map<String, Double> vec1, Map<String, Double> vec2) {
        if (vec1 == null || vec2 == null)
            return 0.0;

        double d = 0.0;
        for (Map.Entry<String, Double> entry : vec1.entrySet()) {
            if (vec2.containsKey(entry.getKey())) {
                d += entry.getValue() * vec2.get(entry.getKey());
            }
        }
        return d;
    }
}

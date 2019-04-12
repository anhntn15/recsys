package nlp;

import java.util.Map;

public class CosineSimilar implements ContentSimilarity {

    public Double getSimilar(Map<String, Double> vec1, Map<String, Double> vec2) {
        if (vec1 == null || vec2 == null)
            return 0.0;

        double s1 = 0;
        double s2 = 0;
        double d = 0;
        for (Map.Entry<String, Double> entry : vec1.entrySet()) {
            if (vec2.containsKey(entry.getKey())) {
                d += entry.getValue() * vec2.get(entry.getKey());
            }
            s1 += entry.getValue() * entry.getValue();
        }
        for (double v : vec2.values()) {
            s2 += v * v;
        }
        return d / Math.sqrt(s1 * s2);
    }
}

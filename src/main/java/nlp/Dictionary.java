package nlp;

import java.io.*;
import java.util.*;

/**
 * Represent dictionary, provide method process with Dictionary as:  get represent vector
 */
public class Dictionary {
    private Map<String, String> token2id;
    private Map<String, String> id2token;
    private Map<String, Integer> dfs;
    private Integer numDocs;

    public Dictionary() {
        token2id = new HashMap<>();
        dfs = new HashMap<>();
        numDocs = 0;
        id2token = null;
    }

    /**
     * Update Dictionary with multi document. Each document is a List of tokens
     *
     * @param docs list documents
     */
    public void addDocs(List<List<String>> docs) {
        for (List<String> doc : docs) {
            addDoc(doc);
        }
    }

    /**
     * Update Dictionary with one document.
     *
     * @param doc list tokens of document
     */
    public void addDoc(List<String> doc) {
        for (String token : new HashSet<>(doc)) {
            String id = token2id.get(token);
            if (id == null) {
                id = token2id.size() + "";
                token2id.put(token, id);
                if (id2token != null) {
                    id2token.put(id, token);
                }
                dfs.put(id, 1);
            } else {
                dfs.put(id, dfs.get(id) + 1);
            }
        }
        numDocs++;
    }

    /**
     * Filter dictionary
     *
     * @param lower
     * @param upper
     */
    public void filter(int lower, float upper) {
        for (String token : new ArrayList<>(token2id.keySet())) {
            String id = token2id.get(token);
            if (dfs.get(id) < lower || dfs.get(id) > numDocs * upper) {
                dfs.remove(id);
                token2id.remove(token);
                if (id2token != null) {
                    id2token.remove(id);
                }
            }
        }
    }

    /**
     * Get tf-idf vector of a document
     *
     * @param doc document need to get tf-idf vector
     * @return a Map represent tf-idf vector of document
     */
    public Map<String, Double> docToTfidf(List<String> doc) {
        Map<String, Integer> bow = docToBow(doc);
        Map<String, Double> tf_idf = new HashMap<>();
        for (Map.Entry<String, Integer> bowEntry : bow.entrySet()) {
            String id = bowEntry.getKey();
            tf_idf.put(id, (1 + Math.log(bowEntry.getValue())) * Math.log((float) numDocs / dfs.get(id)));
        }
        return tf_idf;
    }

    /**
     * Get tf-idf vector with L2 normalized of a document
     *
     * @param doc document need to get tf-idf vector
     * @return a Map represent tf-idf vector of document
     */
    public Map<String, Double> docToTfidfNormalized(List<String> doc) {
        Map<String, Integer> bow = docToBow(doc);
        Map<String, Double> tf_idf = new HashMap<>();
        double sumSquare = 0f;
        for (Map.Entry<String, Integer> bowEntry : bow.entrySet()) {
            String id = bowEntry.getKey();
            double tfIdfValue = (1 + Math.log(bowEntry.getValue())) * Math.log(1 + (float) numDocs / dfs.get(id));
            tf_idf.put(id, tfIdfValue);
            sumSquare += tfIdfValue * tfIdfValue;
        }
        double rootSumSquare = Math.sqrt(sumSquare);
        for (Map.Entry<String, Double> tfIdfEntry : tf_idf.entrySet()) {
            tf_idf.put(tfIdfEntry.getKey(), tfIdfEntry.getValue() / rootSumSquare);
        }
        return tf_idf;
    }

    /**
     * Get bag of word of a document
     *
     * @param doc in put document
     * @return A Map represent bow vector
     */
    public Map<String, Integer> docToBow(List<String> doc) {
        Map<String, Integer> bow = new HashMap<String, Integer>();
        for (String token : doc) {
            String id = token2id.get(token);
            if (id != null) {
                Integer count = bow.getOrDefault(id, 0);
                bow.put(id, count + 1);
            }
        }
        return bow;
    }

    /**
     * get token by token id
     *
     * @param id input token id
     * @return token string if token be contained in Dictionary, otherwise return null;
     */
    public String getToken(String id) {
        if (id2token == null) {
            id2token = new HashMap<>();
            for (Map.Entry<String, String> token2idEntry : token2id.entrySet()) {
                id2token.put(token2idEntry.getValue(), token2idEntry.getKey());
            }
        }
        return id2token.get(id);
    }

    /**
     * Save dictionary to file
     *
     * @param pathFile
     * @return
     */
    public boolean saveToFile(String pathFile) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFile))) {
            bufferedWriter.write("" + numDocs);
            bufferedWriter.newLine();
            for (String token : token2id.keySet()) {
                String id = token2id.get(token);
                int df = dfs.get(id);
                bufferedWriter.write(id + "\t" + token + "\t" + df + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Load dictionary from file
     */
    public static Dictionary loadFromFile(String pathFile) {
        BufferedReader bufferedReader = null;
        Dictionary dictionary;
        try {
            bufferedReader = new BufferedReader(new FileReader(pathFile));
            dictionary = new Dictionary();
            dictionary.numDocs = Integer.parseInt(bufferedReader.readLine());
            dictionary.token2id = new HashMap<>();
            dictionary.dfs = new HashMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] s = line.split("\t");
                if (s.length < 3) {
                    continue;
                }
                String id = s[0];
                String token = s[1];
                int df = Integer.parseInt(s[2]);
                dictionary.token2id.put(token, id);
                dictionary.dfs.put(id, df);
            }
        } catch (IOException e) {
            e.printStackTrace();
            dictionary = null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dictionary;
    }
}

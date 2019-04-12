package nlp;

import ai.vitk.tok.Tokenizer;

public class TokenizerFactory {
    private static Tokenizer tokenizer = new Tokenizer();

    public static Tokenizer getTokenizer() {
        return tokenizer;
    }
}

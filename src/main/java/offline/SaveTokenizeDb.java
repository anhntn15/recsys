package offline;

import ai.vitk.tok.Tokenizer;
import ai.vitk.type.Token;
import io.OriginItem;
import io.sql.ItemDBHandler;
import nlp.HtmlPreprocess;
import nlp.TokenizerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Tách từ cho title và content, lưu luôn vào bảng item, title và content phân cách nhau bằng dấu \n
 */
public class SaveTokenizeDb {
    private static final int BATCH_SIZE = 1000;         // Số lượng item được xử lý trong mỗi lần xử lý
    private static final int TIME_RELOAD = 1000;        // Thời gian dãn cách mỗi lần xử lý dữ liệu

    private static String token(String text) {
        Tokenizer tokenizer = TokenizerFactory.getTokenizer();
        List<Token> words = tokenizer.tokenize(text);
        Pattern p = Pattern.compile("[\\w\\d',. ]*[\\w][\\w\\d'., ]*", Pattern.UNICODE_CHARACTER_CLASS);
        List<String> doc = new ArrayList<>();
        for (Token token : words) {
            String tk = token.getWord().toLowerCase();
            if (!p.matcher(tk).matches()) {
                continue;
            }
            doc.add(tk.replace(" ", "_"));
        }
        return String.join(" ", doc);
    }

    private static void update(int limit) {
        ItemDBHandler itemDBHandler = new ItemDBHandler();
        List<OriginItem> items = itemDBHandler.getItemNonTokenize(limit);
        Map<Long, String> tokenizes = new HashMap<>();
        for (OriginItem originItem : items) {
            String token = token(originItem.getTitle()) + "\n" + token(HtmlPreprocess.removeHtmlTags(originItem.getContent()));
            tokenizes.put(originItem.getId(), token);
        }
        itemDBHandler.updateTokenizes(tokenizes);
        System.out.println("Finish new batch. size: " + items.size());
    }

    public static void listening() {
        while (true) {
            update(BATCH_SIZE);
            System.out.println("Sleep " + TIME_RELOAD + "s");

            try {
                Thread.sleep(TIME_RELOAD);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

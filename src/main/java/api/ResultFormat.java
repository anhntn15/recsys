package api;

import java.util.List;

import datastruct.Item;
import datastruct.Pair;

/**
 * Output format for preview API
 */
public class ResultFormat {
    public static String getItemsHTMLFormat(List<Item> items){
        StringBuilder str = new StringBuilder();
        str.append("<table border=\"1\">");

        str.append("<tr>");
        str.append("<th>ID</th>");
        str.append("<th>TYPE</th>");
        str.append("<th>CATEGORY</th>");
        str.append("<th>LOCATION</th>");
        str.append("<th>PRICE</th>");
        str.append("<th>AREA</th>");
        str.append("<th>ROOM</th>");
        str.append("<th>DESCRIPTION</th>");
        str.append("</tr>");
        for (Item h : items) {
            str.append("<tr>");
            str.append("<td>").append(h.getId()).append("</td>");
            str.append("<td>").append(h.getSellType()).append("</td>");
            str.append("<td>").append(h.getCategory()).append("</td>");
            str.append("<td>").append(h.getLocation()).append("</td>");
            str.append("<td>").append(h.getPrice()).append("</td>");
            str.append("<td>").append(h.getAcreage()).append("</td>");
            str.append("<td>").append(h.getRoomNumber()).append("</td>");
            str.append("<td>").append(h.getContent().getValue()).append("</td>");
            str.append("</tr>");
        }
        str.append("</table>");
        return str.toString();
    }

    public static String getItemsWithScoreHTMLFormat(List<Pair<Item, Double>> items){
        StringBuilder str = new StringBuilder();
        str.append("<table border=\"1\">");

        str.append("<tr>");
        str.append("<th>ID</th>");
        str.append("<th>SCORE</th>");
        str.append("<th>TYPE</th>");
        str.append("<th>CATEGORY</th>");
        str.append("<th>LOCATION</th>");
        str.append("<th>PRICE</th>");
        str.append("<th>AREA</th>");
        str.append("<th>ROOM</th>");
        str.append("<th>DESCRIPTION</th>");
        str.append("</tr>");
        for (Pair<Item, Double> p : items) {
            Item h = p.getLeft();
            str.append("<tr>");
            str.append("<td>").append(h.getId()).append("</td>");
            str.append("<th>").append(p.getRight()).append("</th>");
            str.append("<td>").append(h.getSellType()).append("</td>");
            str.append("<td>").append(h.getCategory()).append("</td>");
            str.append("<td>").append(h.getLocation()).append("</td>");
            str.append("<td>").append(h.getPrice()).append("</td>");
            str.append("<td>").append(h.getAcreage()).append("</td>");
            str.append("<td>").append(h.getRoomNumber()).append("</td>");
            str.append("<td>").append(h.getContent().getValue()).append("</td>");
            str.append("</tr>");
        }
        str.append("</table>");
        return str.toString();
    }
}

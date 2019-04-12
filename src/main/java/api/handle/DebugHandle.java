package api.handle;

import datamanager.ItemManager;
import datamanager.UserLogManager;
import datastruct.HistoryCell;
import datastruct.Item;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.LocationName;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

/**
 * Show data APIss
 */
@Path("/debug/")
public class DebugHandle {


    @Path("info")
    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public static Response info() {
        StringBuilder stringBuilder = new StringBuilder();
        ItemManager itemManager = ItemManager.getInstance();
        stringBuilder.append("<h1>Item Manager</h1>");
        stringBuilder.append("<p>Num item contain: ");
        stringBuilder.append(itemManager.getItemIds().size());
        stringBuilder.append("</p>");
        stringBuilder.append("<p>Num item available: ");
        stringBuilder.append(itemManager.getAvailableItems().size());
        stringBuilder.append("</p>");

        UserLogManager logManager = UserLogManager.getInstance();
        stringBuilder.append("<h1>User history Manager</h1>");
        stringBuilder.append("<p>Num user contain: ");
        stringBuilder.append(logManager.getListUserId().size());
        stringBuilder.append("</p>");

        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");
        response.entity(stringBuilder.toString());
        return response.build();
    }

    @Path("item")
    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public static Response itemPreview(@QueryParam("itemId") Long itemId) {
        ItemManager itemManager = ItemManager.getInstance();
        System.out.println("Item id: " + itemId);
        Item item = itemManager.getItem(itemId);

        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");
        if (item == null) {
            response.entity("Item don't exist.");
            return response.build();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<html>");
            stringBuilder.append("<meta charset=\"UTF-8\">");
            stringBuilder.append("price: ");
            stringBuilder.append(item.getPrice().getValue().getLeft());
            stringBuilder.append(" - ");
            stringBuilder.append(item.getPrice().getValue().getRight());
            stringBuilder.append("<br>");

            stringBuilder.append("square: ");
            stringBuilder.append(item.getAcreage().getValue().getLeft());
            stringBuilder.append(" - ");
            stringBuilder.append(item.getAcreage().getValue().getRight());
            stringBuilder.append("<br>");

            stringBuilder.append("location: ");
            stringBuilder.append(LocationName.getDistrict(item.getLocation().getAddress().getDistrictId()));
            stringBuilder.append(" - ");
            stringBuilder.append(LocationName.getCity(item.getLocation().getAddress().getCityId()));
            stringBuilder.append("<br>");

            stringBuilder.append(item.getContent().getValue());
//            stringBuilder.append("<html>");
            stringBuilder.append("</html>");
            System.out.println(stringBuilder.toString());

            response.entity(stringBuilder.toString());
            return response.build();
        }
    }


    @Path("all_items")
    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public static String allItems(@QueryParam("limit") @DefaultValue("10") Integer limit) {
        ItemManager itemManager = ItemManager.getInstance();

        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");

        Set<Long> itemIds = itemManager.getItemIds();
        System.out.println("Num item: " + itemIds.size());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>");
        stringBuilder.append("<meta charset=\"UTF-8\">");
        JSONArray jsonArray = new JSONArray();
        int it = 0;
        for (Long itemId : itemIds) {
            try {
                Item item = itemManager.getItem(itemId);
                if (item != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("price", item.getPrice().value.getLeft() + "-" + item.getPrice().getValue().getRight());
                    jsonObject.put("square", item.getAcreage().value.getLeft() + "-" + item.getAcreage().getValue().getRight());
                    jsonObject.put("location", LocationName.getDistrict(item.getLocation().getAddress().getDistrictId()) +
                            "-" + LocationName.getCity(item.getLocation().getAddress().getCityId()));
//                    jsonObject.put("content", item.getContent().getValue());
//                    System.out.println(jsonObject);
//                jsonObject.put("price", item.getPrice().value.getLeft() + "-" + item.getPrice().getValue().getRight());

                    stringBuilder.append("<h4>");
                    stringBuilder.append(item.getId());
                    stringBuilder.append("</h4>");
//                    stringBuilder.append("<br>");

                    stringBuilder.append("price: ");
                    stringBuilder.append(item.getPrice().getValue().getLeft());
                    stringBuilder.append(" - ");
                    stringBuilder.append(item.getPrice().getValue().getRight());
                    stringBuilder.append("<br>");

                    stringBuilder.append("square: ");
                    stringBuilder.append(item.getAcreage().getValue().getLeft());
                    stringBuilder.append(" - ");
                    stringBuilder.append(item.getAcreage().getValue().getRight());
                    stringBuilder.append("<br>");

                    stringBuilder.append("location: ");
                    stringBuilder.append(LocationName.getDistrict(item.getLocation().getAddress().getDistrictId()));
                    stringBuilder.append(" - ");
                    stringBuilder.append(LocationName.getCity(item.getLocation().getAddress().getCityId()));
                    stringBuilder.append("<br>");

                    stringBuilder.append(item.getContent().getValue());

                    stringBuilder.append("<hr>");
                    jsonArray.put(jsonObject);
                    it++;
                    if (it > limit) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        stringBuilder.append("</html>");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("items", jsonArray);
            response.entity(jsonObject);
            System.out.println(response.toString());
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Have error");
            return "Have error";
        }
    }

    @Path("user_history")
    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public static Response userHistoryPreview(@QueryParam("userId") String userId) {
        UserLogManager userLogManager = UserLogManager.getInstance();
        List<HistoryCell> userHistory = userLogManager.getLogHistory(userId);
        System.out.println("User id: " + userId);

        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");
        if (userHistory == null) {
            response.entity("User don't exist.");
            return response.build();
        } else {
            JSONArray jsonArray = new JSONArray();
            for (HistoryCell historyCell : userHistory) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("price", historyCell.getItem().getPrice().value.getLeft() + "-" + historyCell.getItem().getPrice().getValue().getRight());
                jsonObject.put("square", historyCell.getItem().getAcreage().value.getLeft() + "-" + historyCell.getItem().getAcreage().getValue().getRight());
                jsonObject.put("location", LocationName.getDistrict(historyCell.getItem().getLocation().getAddress().getDistrictId()) +
                        "-" + LocationName.getCity(historyCell.getItem().getLocation().getAddress().getCityId()));
                jsonObject.put("create_time", historyCell.getCreateTime());
                jsonObject.put("click_to_call", historyCell.isClickToCall());
                jsonArray.put(jsonObject);
            }

            response.entity(jsonArray.toString());
            return response.build();
        }
    }
}

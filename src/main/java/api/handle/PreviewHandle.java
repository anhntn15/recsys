package api.handle;

import api.ResultFormat;
import datamanager.UserLogManager;
import datastruct.Item;
import recommender.BasicRecommender;
import recommender.ContentBasedRecommender;
import recommender.DebugData;
import recommender.RecommendAbstract;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * APIs for preview recommend result
 */
@Path("/preview")
public class PreviewHandle {
    /**
     * API for preview recommend with a user id and a item id
     *
     * @param itemId
     * @param userId
     * @return
     */
    @Path("/{recommendType}/get_recommend")
    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Response getRecommend(@PathParam("recommendType") String recommendType, @DefaultValue("-1") @QueryParam("itemId") Integer itemId,
                                 @DefaultValue("") @QueryParam("userId") String userId) {
        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");
        String str = getPreviewResult(userId, selectRecommend(recommendType));
        if (str != null) {
            response.entity(str);
        }
        return response.build();
    }

    /**
     * API for preview recommend with user random
     */
    @Path("/{recommendType}/get_random_recommend")
    @GET
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Response getRandomRecommendV1(@PathParam("recommendType") String recommendType) {
        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");
        String str = getPreviewResult(UserLogManager.getInstance().getRandomUserId(), selectRecommend(recommendType));
        if (str != null) {
            response.entity(str);
        }
        return response.build();
    }

    /**
     * Select recommend algorithm by type
     *
     * @param recommendType recommend alg type: v1: BasicRecommend, v2: ContentBasedRecommend
     * @return
     */
    private RecommendAbstract selectRecommend(String recommendType) {
        recommendType = recommendType.toLowerCase();
        switch (recommendType) {
            case "v1":
                return BasicRecommender.getInstance();
            case "v2":
                return ContentBasedRecommender.getInstance();
            default:
                return BasicRecommender.getInstance();
        }
    }

    private String getPreviewResult(String userId, RecommendAbstract recommender) {
        try {
            long time = System.currentTimeMillis();
            String randomUId = UserLogManager.getInstance().getRandomUserId();
            DebugData result = recommender.getRecommendDebugResult(userId, null);
            if (result == null)
                return "null";
            long calculateTime = (System.currentTimeMillis() - time);
            System.out.print("\ttotal time: " + calculateTime + "\tuser_id: " + randomUId);
            System.out.println();
            List<Item> history = result.getHistoryItems();
            String extractedString = result.getItemScoring() != null ? result.getItemScoring().getExtractedFeatureAsHTML() : "";
            return "<html>" +
                    "<meta charset=\"UTF-8\">" +
                    "UID: " + randomUId + "<br></br>TIME: " + calculateTime + "ms" +
                    "<h2>history</h2>" +
                    ResultFormat.getItemsHTMLFormat(history) +
                    "<h2>Extracted Feature</h2>" + extractedString + "<h2>Recommend Items</h2>" +
                    ResultFormat.getItemsWithScoreHTMLFormat(result.getResult()) +
                    "</html>";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

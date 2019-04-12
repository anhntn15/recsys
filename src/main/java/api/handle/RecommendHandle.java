package api.handle;


import datamanager.UserLogManager;
import datastruct.Item;
import datastruct.Pair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import recommender.BasicRecommender;
import recommender.ContentBasedRecommender;
import recommender.RecommendAbstract;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Main recommend APIs
 */
@Path("/recommend")
public class RecommendHandle {
    private static final Logger LOGGER = Logger.getLogger(RecommendHandle.class);
    private static int count = 0;

    @Path("/alg_info")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getAlgInfo(@QueryParam("itemId") Integer itemId, @QueryParam("userId") String userId) {
        String s = "<p>algId=1: Basic algorithms</p> <p>algId=2: ContentBased algorithms</p>";
        LOGGER.info("Recommend for user: " + userId + " with current item: " + itemId);
        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");
        response.entity(s);
        return response.build();
    }


    /**
     * get recommend with identify user id
     *
     * @param itemId
     * @param userId
     * @return
     */
    @Path("/{recommendType}/get_recommend")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getRecommend(@PathParam("recommendType") String recommendType,
                                 @DefaultValue("0") @QueryParam("itemId") Integer itemId,
                                 @QueryParam("userId") String userId,
                                 @DefaultValue("0") @QueryParam("catId") Integer catId,
                                 @DefaultValue("0") @QueryParam("sellType") Integer sellType,
                                 @DefaultValue("0") @QueryParam("cityId") Integer cityId) {
        LOGGER.info("Recommend for user: " + userId + " with current item: " + itemId);
        Response.ResponseBuilder response = Response.status(200);
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        response = response.header("Access-Control-Allow-Headers", "accept, content-type, x-parse-application-id, x-parse-rest-api-key, x-parse-session-token");
        RecommendAbstract recommend = selectRecommend(recommendType);
        try {
            List<Pair<Item, Double>> recommendResult;
            if (itemId > 0) {
                System.out.println("UserId " + userId + "\tItem " + itemId);
                recommendResult = recommend.getRecommend(userId, itemId);
            }
            else {
                System.out.println("UserId " + userId + "\tCity " + cityId + "\tCate " + catId + "\tSellType " + sellType);
                recommendResult = recommend.getRecommend(userId, cityId, catId, sellType);
            }

            if(recommendResult == null) {
                System.out.println("Rec null");
            }
            else{
                System.out.println("Rec ok");
                JSONArray jsonArray = formatResultJSON(recommendResult);
                response.entity(jsonArray.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.build();
    }

    /**
     * Recommend with random user
     *
     * @return
     */
    @Path("/{recommendType}/get_random_recommend")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getRandomRecommend(@PathParam("recommendType") String recommendType,
                                       @DefaultValue("0") @QueryParam("catId") Integer catId,
                                       @DefaultValue("0") @QueryParam("sellType") Integer sellType,
                                       @DefaultValue("0") @QueryParam("cityId") Integer cityId) {
        LOGGER.info("Recommend random");
        Response.ResponseBuilder response = Response.status(200);
        RecommendAbstract recommend = selectRecommend(recommendType);
        try {
            String randomUId = UserLogManager.getInstance().getRandomUserId();
            List<Pair<Item, Double>> recommendResult;

            if (catId == 0 && sellType == 0 && cityId == 0)
                recommendResult = recommend.getRecommend(randomUId, 0);
            else
                recommendResult = recommend.getRecommend(randomUId, cityId, catId, sellType);

            if(recommendResult == null){
                System.out.println("Rec null");
            }else{
                System.out.println("Rec ok");
                JSONArray jsonArray = formatResultJSON(recommendResult);
                response.entity(jsonArray.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Format recommend result to JSONArray
     *
     * @param recommendResult recommend result
     * @return
     */
    private JSONArray formatResultJSON(List<Pair<Item, Double>> recommendResult) {
        JSONArray jsonArray = new JSONArray();
        for (Pair<Item, Double> pair : recommendResult) {
            JSONObject object = new JSONObject();
            object.put("id", pair.getLeft().getId());
            object.put("score", pair.getRight());
            jsonArray.put(object);
        }
        return jsonArray;
    }
}

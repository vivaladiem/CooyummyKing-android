package com.coo.y2.cooyummyking.entity;

import com.coo.y2.cooyummyking.network.URL;
import com.coo.y2.cooyummyking.util.RecipeSerializer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Y2 on 2015-04-25.
 */
public class Recipe {
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_TITLE = "title";
    public static final String RECIPE_INST = "instruction";
    public static final String RECIPE_MAINIMG = "main_image_num";
    public static final String RECIPE_COOKINGTIME = "cooking_time";
    public static final String RECIPE_THEME = "theme";
    public static final String RECIPE_INGREDIENTS = "ingredients";
    public static final String RECIPE_SOURCES = "sources";
    public static final String RECIPE_LIKE = "like_count";
    public static final String RECIPE_SCRAP = "scrap_count";
    public static final String CREATED_AT = "created_at";

    public static final String RECIPE_IMAGE_PATH = "image_path";


    // [Tuning] 안드로이드에선 게터 세터가 성능면에서 좋지 않기때문에 필터링이 필요없을땐 직접접근으로 한다.
    public int id;
    public int userId;
    public String userName;
    public String title;
    public ArrayList<String> instructions;
    public int mainImageNum;
    public int cookingTime;
    public String theme;
    public String ingredients;
    public String sources;
    public int likeCount;
    public int scrapCount;
    public String createdAt;

    // ------------------------- Related to tool -------------------------- //
    private static Recipe sRecipe;
    public static ArrayList<String> imagePaths = new ArrayList<>();
    public static boolean isChanged = false; // 레시피 저장 여부를 알기 위한 전역변수

    public static Recipe getScheme() {
        if (sRecipe == null) {
            sRecipe = new Recipe();

            // 초기화 필요한 변수들 초기화
            sRecipe.title = "";
            sRecipe.cookingTime = 0;
            sRecipe.theme = "";
            sRecipe.ingredients = "";
            sRecipe.sources = "";
            sRecipe.instructions = new ArrayList<>();
        }

        return sRecipe;
    }

    public static boolean isSchemeLoaded() {
        return sRecipe != null;
    }

    /**
     * Load saved recipe schema. needed for make one. if changed and not saved data exist, that will be lost. so be careful.
     * @param serializer : RecipeSerializer to handle serializing recipe data. Exist for memory efficient as this method is only needed in specific occasion.
     * @return : If temp data exist, returns loaded SingleTone recipe instance. If not, returns new empty recipe instance
     */
    public static Recipe loadTempScheme(RecipeSerializer serializer) {

        // get saved file
        JSONObject json = null;
        try {
            json = serializer.loadTempData();
        } catch(Exception e) {
            // 파일이 없는 등 로드에 실패했을 때
        }

        if (json == null) {
//            return getScheme(); // 이렇게 하려면 throws를 안해야하는데 그러면 ToolFragment에서 새로 불러온건지 로딩된건지 알 수 없으므로 return null을 한다.
            return null;
        }

        sRecipe = new Recipe();
        sRecipe.title = json.optString(RECIPE_TITLE);
        sRecipe.instructions = new ArrayList<>(Arrays.asList(json.optString(RECIPE_INST).split("\\|\\|", -1)));
        sRecipe.mainImageNum = json.optInt(RECIPE_MAINIMG);
        sRecipe.cookingTime = json.optInt(RECIPE_COOKINGTIME);
        sRecipe.theme = json.optString(RECIPE_THEME);
        sRecipe.ingredients = json.optString(RECIPE_INGREDIENTS);
        sRecipe.sources = json.optString(RECIPE_SOURCES);

        // * split 에 -1을 인자로 넣어줘야만 끝의 빈 요소를 취급함. (빈 요소가 연속이면 연속되는 모두를)
        // ex) split(",") (1,2,,) -> [1, 2]  // split(",", -1) (1,2,,) -> [1, 2, , ]
        // 기본값은 0
        Recipe.imagePaths = new ArrayList<>(Arrays.asList(json.optString(RECIPE_IMAGE_PATH).split(", ", -1)));
        return sRecipe;
    }

    // temp file delete도 해야.

//    public JSONObject toJSON() throws JSONException{
//        JSONObject json = new JSONObject();
//        json.put(RECIPE_TITLE, title);
//        json.put(RECIPE_INST, TextUtils.join("||", instructions));
//        json.put(RECIPE_COOKINGTIME, cookingTime);
//        json.put(RECIPE_THEME, theme);
//        json.put(RECIPE_INGREDIENTS, ingredients);
//        json.put(RECIPE_SOURCES, sources);
//        return json;
//    }
//
//    public void saveTempData(Context context) throws IOException, JSONException{
//        OutputStream out = context.openFileOutput(mFileName, Context.MODE_PRIVATE);
//        Writer writer = new OutputStreamWriter(out);
//        writer.write(sRecipe.toJSON().toString());
//
//        if (writer != null) writer.close();
//    }
    // ----------------------------------------------------------------------- //


    /**
     * load recipe from network or scraped data
     * @param json : downloaded JSONObject data
     * @return : recipe instance
     */
    public static Recipe loadRecipe(JSONObject json) {
        if (json == null) {
            return null;
        }
        Recipe recipe = new Recipe();
        recipe.id = json.optInt(RECIPE_ID);
        recipe.userId = json.optInt(User.USER_ID);
        recipe.userName = json.optString(User.USER_NAME);
        recipe.title = json.optString(RECIPE_TITLE);
        recipe.instructions = new ArrayList<>(Arrays.asList(json.optString(RECIPE_INST).split("\\|\\|"))); // Split by || // asList가 참조값을 가지게 하는거라 어딘가 누수 생길지도
        recipe.mainImageNum = json.optInt(RECIPE_MAINIMG);
        recipe.cookingTime = json.optInt(RECIPE_COOKINGTIME);
        recipe.theme = json.optString(RECIPE_THEME);
        recipe.ingredients = json.optString(RECIPE_INGREDIENTS);
        recipe.sources = json.optString(RECIPE_SOURCES);
        recipe.likeCount = json.optInt(RECIPE_LIKE);
        recipe.scrapCount = json.optInt(RECIPE_SCRAP);
        recipe.createdAt = json.optString(CREATED_AT);

        return recipe;
    }

    // static으로 하는게 좋으려나
    public String getImageUrl(int imageNum) {
        return String.format(URL.GET_IMAGE_URL, this.id, imageNum);
    }

    // 임시로 작성. User에 있어야.
    public String getWriterProfileImageUrl(int userId) {
        return URL.getBaseUrl() + "users/profile/" + userId;
    }

}

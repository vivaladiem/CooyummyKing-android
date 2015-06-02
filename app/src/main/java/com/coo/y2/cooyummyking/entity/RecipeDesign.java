package com.coo.y2.cooyummyking.entity;

import android.util.Log;

import com.coo.y2.cooyummyking.util.RecipeSerializer;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Y2 on 2015-06-01.
 * Recipe design class used in Tool function
 */
public class RecipeDesign {
    public String title;
    public ArrayList<String> imagePaths;
    public ArrayList<String> instructions;
    public int mainImageIndex;
    public int cookingTime;
    public String theme;
    public String ingredients;
    public String sources;
    
    public boolean isChanged = false; // 레시피 저장 여부를 알기 위한 전역변수 // 이것 빼먹으면 문제가 생기니 적어도 할당은 세터로 하는것이 좋을지도..
    public boolean isMainImgManuallySet = false;
    private static RecipeDesign sRecipe;

    public static RecipeDesign getDesign() {
        if (sRecipe == null) {
            sRecipe = new RecipeDesign();

            // 초기화 필요한 변수들 초기화
            sRecipe.title = "";
            sRecipe.cookingTime = 0;
            sRecipe.theme = "";
            sRecipe.ingredients = "";
            sRecipe.sources = "";
            sRecipe.imagePaths = new ArrayList<>();
            sRecipe.instructions = new ArrayList<>();
        }

        return sRecipe;
    }

    public static boolean isInited() {
        return sRecipe != null;
    }

    /**
     * Load saved recipe design. needed for make one. if changed and not saved data exist, that will be lost. so be careful.
     * @param serializer RecipeSerializer to handle serializing recipe data. Exist for memory efficient as this method is only needed in specific occasion.
     * @return If temp data exist, returns loaded SingleTone recipe design instance. If not, returns new empty instance
     */
    public static RecipeDesign loadTempDesign(RecipeSerializer serializer) {
        // get saved file
        JSONObject json = null;
        try {
            json = serializer.loadTempData();
        } catch(Exception e) {
            // 파일이 없는 등 로드에 실패했을 때
            return null;
        }

        if (json == null) {
            return null; // 데이터가 없을 때
        }

        sRecipe = new RecipeDesign();
        sRecipe.title = json.optString(Recipe.RECIPE_TITLE);
        sRecipe.instructions = new ArrayList<>(Arrays.asList(json.optString(Recipe.RECIPE_INST).split("\\|\\|", -1)));
        sRecipe.mainImageIndex = json.optInt(Recipe.RECIPE_MAINIMG);
        sRecipe.cookingTime = json.optInt(Recipe.RECIPE_COOKINGTIME);
        sRecipe.theme = json.optString(Recipe.RECIPE_THEME);
        sRecipe.ingredients = json.optString(Recipe.RECIPE_INGREDIENTS);
        sRecipe.sources = json.optString(Recipe.RECIPE_SOURCES);


        // * split 에 -1을 인자로 넣어줘야만 끝의 빈 요소를 취급함. (빈 요소가 연속이면 연속되는 모두를)
        // ex) split(",") (1,2,,) -> [1, 2]  // split(",", -1) (1,2,,) -> [1, 2, , ]
        // 기본값은 0
        sRecipe.imagePaths = new ArrayList<>(Arrays.asList(json.optString(Recipe.RECIPE_IMAGE_PATH).split(", ", -1)));
        return sRecipe;
    }


    // Temp Data delete기능도 구현해야.


    public int getStepSize() {
        // 뭔가 더 확실한 조치를 취해야 할 것 같기는 한데..
        // 근데 오류가 발생하는 경우가 있긴 할지 잘 모르겠다.
        // image Url에 맞춰 instructions 추가시키는 도중에 에러난다면 발생하려나
        return Math.min(instructions.size(), imagePaths.size());
    }

    /**
     * Get image path. if edited photo exist, return that.
     */
    public String getImagePath(int index) {
        String path = imagePaths.get(index);
        if (path.contains("||")) {
            return path.split("\\|\\|")[1];
        } else {
            return path;
        }
    }

    public String getImagePath(int index, boolean original) {
        return original ? imagePaths.get(index).split("\\|\\|")[0] : getImagePath(index);
    }

    /**
     * Add edited photo path. Append edited photo path with || separator.
     * @param index step index
     * @param path edited photo path
     */
    public void addEditedImage(int index, String path) {
        // ||을 구분자로 뒷부분에 수정된 이미지 경로를 덧붙입니다
        if (imagePaths.get(index).contains("||")) {
            String[] paths = imagePaths.get(index).split("\\|\\|", -1);
            String origin = paths[0];
            String preEdImage = paths[1];

            // 기존 파일을 삭제합니다
            boolean isDeleted = new File(preEdImage).delete();
            if (!isDeleted) {
                // 기존 편집 이미지파일 삭제 실패
                Log.i("CYMK", "Previous Deco Image File Delete Failed : " + preEdImage);
            }

            imagePaths.set(index, origin + "||" + path);
        } else {
            imagePaths.set(index, imagePaths.get(index) + "||" + path);
        }
    }

    public void resetImage(int index) {
        String[] paths = imagePaths.get(index).split("\\|\\|");

        String preEdImage = paths[1];
        boolean isDeleted = new File(preEdImage).delete();
        if (!isDeleted) {
            // 기존 편집 이미지파일 삭제 실패
            // TODO 시스템이 나중에 다시 삭제하는 등 조치 취할 수 있도록 처리해야.
            // TODO 좀비파일이 남아있게 하면 안됨.
            Log.i("CYMK", "Previous deco image file delete failed : " + preEdImage);
        }

        imagePaths.set(index, paths[0]);
    }
}

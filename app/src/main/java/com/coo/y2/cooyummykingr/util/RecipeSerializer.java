package com.coo.y2.cooyummykingr.util;

import android.content.Context;
import android.text.TextUtils;

import com.coo.y2.cooyummykingr.entity.Recipe;
import com.coo.y2.cooyummykingr.entity.RecipeDesign;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Y2 on 2015-05-24.
 * Recipe design temp data serializer class
 */
public class RecipeSerializer {
    private final String mFileName = "temp_recipe.json";
    private Context mContext;
    
    public RecipeSerializer(Context context) {
        mContext = context;
    }

    public void saveTempData(RecipeDesign recipe) throws IOException, JSONException {
        OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
        Writer writer = new OutputStreamWriter(out);
        writer.write(recipeToJSON(recipe).toString());

        if (writer != null) writer.close();
    }

    public JSONObject recipeToJSON(RecipeDesign recipe) throws JSONException{
        JSONObject json = new JSONObject();
        json.put(Recipe.RECIPE_TITLE, recipe.title);
        json.put(Recipe.RECIPE_INST, TextUtils.join("||", recipe.instructions));
        json.put(Recipe.RECIPE_COOKINGTIME, recipe.cookingTime);
        json.put(Recipe.RECIPE_THEME, recipe.theme);
        json.put(Recipe.RECIPE_INGREDIENTS, recipe.ingredients);
        json.put(Recipe.RECIPE_SOURCES, recipe.sources);
        json.put(Recipe.RECIPE_IMAGE_PATH, TextUtils.join(", ", recipe.getAllImagePath()));
        json.put(Recipe.RECIPE_MAINIMG, recipe.mainImageIndex);
        return json;
    }

    public JSONObject loadTempData() throws IOException, JSONException {
        BufferedReader reader = null;
        JSONObject json = null;
        try {
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            json = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
        } catch (FileNotFoundException e) {
            // 저장된 파일이 없을 때.
        } finally {
            if (reader != null) reader.close();
        }
        
        return json;
    }
}

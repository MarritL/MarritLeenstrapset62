package marrit.marritleenstra_pset62;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Marrit on 20-3-2018.
 * Class containing all Recipe data
 */

public class Recipe implements Serializable {

    // variables
    private String mId;
    private String mRecipeName;
    private Double mRating;
    private String mSourceName;
    private String mSourceUrl;
    private int mServings;
    private String mTime;
    private ArrayList<String> mImages;
    private String mLargeImageUrl;
    private ArrayList<String> mIngredients;
    private String mYummlySearch;
    private String mYummlyUrl;
    private String mYummlyLogoUrl;

    // default constructor for FireBase
    public Recipe() {}

    // constructor
    Recipe(String id, String name, String sourceName, Double rating, ArrayList<String> images,
           String yummlyLogo, String yummlySearch, String yummlyUrl){
        this.mId = id;
        this.mRecipeName = name;
        this.mSourceName = sourceName;
        this.mRating = rating;
        this.mImages = images;
        this.mYummlyLogoUrl = yummlyLogo;
        this.mYummlySearch = yummlySearch;
        this.mYummlyUrl = yummlyUrl;
        this.mSourceUrl = "";
        this.mServings = 0;
        this.mTime = "";
        this.mLargeImageUrl = "";
        this.mIngredients = new ArrayList<>();
    }

    // getters and setters
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getRecipeName() {
        return mRecipeName;
    }

    public void setRecipeName(String recipeName) {
        mRecipeName = recipeName;
    }

    public Double getRating() {
        return mRating;
    }

    public void setRating(Double rating) {
        mRating = rating;
    }

    public String getSourceName() {
        return mSourceName;
    }

    public void setSourceName(String sourceName) {
        mSourceName = sourceName;
    }

    public ArrayList<String> getImages() {
        return mImages;
    }

    public void setImages(ArrayList<String> images) {
        mImages = images;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        mSourceUrl = sourceUrl;
    }

    public int getServings() {
        return mServings;
    }

    public void setServings(int servings) {
        mServings = servings;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getLargeImageUrl() {
        return mLargeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        mLargeImageUrl = largeImageUrl;
    }

    public ArrayList<String> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        mIngredients = ingredients;
    }

    public String getYummlySearch() {
        return mYummlySearch;
    }

    public void setYummlySearch(String yummlySearch) {
        mYummlySearch = yummlySearch;
    }

    public String getYummlyUrl() {
        return mYummlyUrl;
    }

    public void setYummlyUrl(String yummlyUrl) {
        mYummlyUrl = yummlyUrl;
    }

    public String getYummlyLogoUrl() {
        return mYummlyLogoUrl;
    }

    public void setYummlyLogoUrl(String yummlyLogoUrl) {
        mYummlyLogoUrl = yummlyLogoUrl;
    }

    // find the recipe with the specified id in the specified arrayList
    public static Recipe getRecipe(String id, ArrayList<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            if (recipe.getId().equals(id)) {
                return recipe;
            }
        }
        return null;
    }
}

package hu.blog.megosztanam.rest;

import com.google.gson.*;
import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Created by Miklós on 2016. 12. 10..
 */

public class LFGServicesImpl implements ILFGServicesHelper{

    private static final String BASE_URL = "https://thawing-journey-82619.herokuapp.com/";
    private ILFGServicesHelper servicesHelper;

    public LFGServicesImpl() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson =
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                }).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        servicesHelper = retrofit.create(ILFGServicesHelper.class);
    }

    public Call<Summoner> getSummoner(String name, Server server) {
        return servicesHelper.getSummoner(name, server);
    }


    public Call<LoginResponse> doLogin(String idToken) {
        return servicesHelper.doLogin(idToken);
    }

    public Call<LoginResponse> doRegistration(String idToken, Integer summonerId, Server server) {
        return servicesHelper.doRegistration(idToken, summonerId, server);
    }

    public Call<List<Post>> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked) {
        return servicesHelper.getSearchForMemberPosts(server, userId, map, isRanked);
    }

    @Override
    public Call<Boolean> deletePost(Integer userId, Integer postId) {
        return servicesHelper.deletePost(userId, postId);
    }

    public Call<Integer> savePost(Post post){
        return servicesHelper.savePost(post);
    }


    public Call<Boolean> applyForPost(PostApplyRequest request){
        return servicesHelper.applyForPost(request);
    }

    public Call<List<PostApplyResponse>> getApplications(Integer userId){
        return servicesHelper.getApplications(userId);
    }

    @Override
    public Call<Void> updateFirebaseId(Integer userId, String firebaseId) {
        return servicesHelper.updateFirebaseId(userId, firebaseId);
    }
}

package hu.blog.megosztanam.rest;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Miklós on 2016. 12. 10..
 */

public class LFGService implements ILFGService {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private final ILFGService servicesHelper;

    public LFGService(Context context) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson =
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                }).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okhttpClient(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        servicesHelper = retrofit.create(ILFGService.class);
    }

    public Call<Summoner> getSummoner(String name, Server server) {
        return servicesHelper.getSummoner(name, server);
    }


    public Call<LoginResponse> doLogin(String idToken) {
        return servicesHelper.doLogin(idToken);
    }

    public Call<LoginResponse> doRegistration(String idToken, String summonerId, Server server) {
        return servicesHelper.doRegistration(idToken, summonerId, server);
    }

    public Call<List<Post>> getSearchForMemberPosts(Server server, Integer userId, GameMap map, Boolean isRanked) {
        return servicesHelper.getSearchForMemberPosts(server, userId, map, isRanked);
    }

    @Override
    public Call<Boolean> deletePost(Integer userId, Integer postId) {
        return servicesHelper.deletePost(userId, postId);
    }

    public Call<Integer> savePost(Post post) {
        return servicesHelper.savePost(post);
    }


    public Call<Void> applyForPost(PostApplyRequest request) {
        return servicesHelper.applyForPost(request);
    }

    public Call<List<PostApplyResponse>> getApplications(Integer userId) {
        return servicesHelper.getApplications(userId);
    }

    @Override
    public Call<List<PostApplyResponse>> getApplicationsOfApplicant(Integer userId) {
        return servicesHelper.getApplicationsOfApplicant(userId);
    }

    @Override
    public Call<Void> updateFirebaseId(Integer userId, String firebaseId) {
        return servicesHelper.updateFirebaseId(userId, firebaseId);
    }

    @Override
    public Call<Void> deleteUser(Integer userId) {
        return servicesHelper.deleteUser(userId);
    }

    @Override
    public Call<Void> acceptApplication(Integer postId, Integer userId) {
        return servicesHelper.acceptApplication(postId, userId);
    }

    @Override
    public Call<Void> rejectApplication(Integer postId, Integer userId) {
        return servicesHelper.rejectApplication(postId, userId);
    }

    @Override
    public Call<Void> revokeApplication(Integer userId, Integer postId) {
        return servicesHelper.revokeApplication(userId, postId);
    }

    @Override
    public Call<Void> confirmApplication(Integer postId, Integer userId) {
        return servicesHelper.confirmApplication(postId, userId);
    }

    private OkHttpClient okhttpClient(Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .build();
    }
}

package hu.blog.megosztanam.rest;

import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

/**
 * Created by Miklós on 2016. 12. 10..
 */

public interface ILFGServicesHelper {

    @GET("/Summoner/{name}")
    Call<Summoner> getSummoner(@Path("name") String name);

    @POST("/login")
    Call<LoginResponse> doLogin(@Body String idToken);

    @POST("/registration/{summonerId}")
    Call<LoginResponse> doRegistration(@Body String idToken,
                                       @Path("summonerId") Integer summonerId);
    @GET("/post")
    Call<List<Post>> getSearchForMemberPosts();

    @POST("/post")
    Call<Integer> saveLookingForMemberPost(@Body Post post);
}

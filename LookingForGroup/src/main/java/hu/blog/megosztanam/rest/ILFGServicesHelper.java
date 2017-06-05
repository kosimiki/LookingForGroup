package hu.blog.megosztanam.rest;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Created by Miklós on 2016. 12. 10..
 */

public interface ILFGServicesHelper {

    @GET("/{server}/summoners/{name}")
    Call<Summoner> getSummoner(@Path("name") String name, @Path("server") Server server);

    @POST("/login")
    Call<LoginResponse> doLogin(@Body String idToken);

    @POST("/{server}/registration/{summonerId}")
    Call<LoginResponse> doRegistration(@Body String idToken,
                                       @Path("summonerId") Integer summonerId,
                                       @Path("server") Server server);

    @GET("{server}/posts")
    Call<List<Post>> getSearchForMemberPosts(@Path("server") Server server,
                                             @Query("userId") Integer userId,
                                             @Query("map") GameMap map,
                                             @Query("isRanked") Boolean isRanked
    );

    @DELETE("{userId}/posts/{postId}")
    Call<Boolean> deletePost(@Path("userId") Integer userId, @Path("postId") Integer postId);

    @POST("/post")
    Call<Integer> savePost(@Body Post post);


    @POST("/posts/apply")
    Call<Boolean> applyForPost(@Body PostApplyRequest request);

    @GET("/users/{userId}/applications")
    Call<List<PostApplyResponse>> getApplications(@Path("userId") Integer userId);
}
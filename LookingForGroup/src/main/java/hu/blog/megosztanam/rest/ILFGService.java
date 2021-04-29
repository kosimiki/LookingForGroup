package hu.blog.megosztanam.rest;

import java.util.List;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.model.shared.LoginResponse;
import hu.blog.megosztanam.model.shared.Post;
import hu.blog.megosztanam.model.shared.Summoner;
import hu.blog.megosztanam.model.shared.post.PostApplyRequest;
import hu.blog.megosztanam.model.shared.post.PostApplyResponse;
import hu.blog.megosztanam.model.shared.summoner.Server;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Miklós on 2016. 12. 10..
 */

public interface ILFGService {

    @GET("/lol/{server}/summoners/{name}")
    Call<Summoner> getSummoner(@Path("name") String name, @Path("server") Server server);

    @POST("/login")
    Call<LoginResponse> doLogin(@Body String idToken);

    @POST("/lol/{server}/registration/{summonerId}")
    Call<LoginResponse> doRegistration(@Body String idToken,
                                       @Path("summonerId") String summonerId,
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
    Call<Void> applyForPost(@Body PostApplyRequest request);

    @GET("/users/{userId}/posts/applications")
    Call<List<PostApplyResponse>> getApplications(@Path("userId") Integer userId);

    @GET("/users/{userId}/applications")
    Call<List<PostApplyResponse>> getApplicationsOfApplicant(@Path("userId") Integer userId);

    @PUT("/users/{userId}")
    Call<Void> updateFirebaseId(@Path("userId") Integer userId, @Body String firebaseId);

    @DELETE("/users/{userId}")
    Call<Void> deleteUser(@Path("userId") Integer userId);

    @PUT("/posts/{postId}/applications/{userId}")
    Call<Void> acceptApplication(@Path("postId") Integer postId, @Path("userId") Integer userId);

    @DELETE("/posts/{postId}/applications/{userId}")
    Call<Void> rejectApplication(@Path("postId") Integer postId, @Path("userId") Integer userId);

    @DELETE("/users/{userId}/applications/{postId}")
    Call<Void> revokeApplication(@Path("userId") Integer userId, @Path("postId") Integer postId);

    @PUT("/users/{userId}/applications/{postId}")
    Call<Void> confirmApplication(@Path("postId") Integer postId, @Path("userId") Integer userId);


}
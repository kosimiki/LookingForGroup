package hu.blog.megosztanam.service;

import hu.blog.megosztanam.model.shared.Post;

import java.util.List;

/**
 * Created by Miklós on 2017. 04. 20..
 */
public interface IPostService {

    Integer saveLookingForMoreNotice(Post post);
    List<Post> getSearchForMemberPosts();

}

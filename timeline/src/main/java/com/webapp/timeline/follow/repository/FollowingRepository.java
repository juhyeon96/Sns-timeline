package com.webapp.timeline.follow.repository;


import com.webapp.timeline.follow.domain.FollowId;
import com.webapp.timeline.follow.domain.Followings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FollowingRepository extends JpaRepository<Followings, FollowId> {
    @Query("select count(f) from Followings f where f.id.friendId = :uid and f.isFollow = 1")
    int findFollowNum(@Param("uid") String uid);

    @Query("select count(f) from Followings f where f.id.userId = :uid and f.isFollow = 1")
    int findFollowerNum(@Param("uid") String uid);

    @Modifying
    @Query("update Followings f set f.isFollow = 1 where f.id.friendId = :uid and f.id.userId = :fid")
    void updateNewFriend(@Param("uid")String uid, @Param("fid")String fid);

    @Query("select f.id.userId from Followings f where f.id.friendId = :uid and f.isFollow = 0 and  f.id.userId in (select fer.id.friendId from Followers fer where fer.id.userId = :uid and fer.isFollow = 1)")
    List<String> findFriendApplyList(@Param("uid")String uid);
}
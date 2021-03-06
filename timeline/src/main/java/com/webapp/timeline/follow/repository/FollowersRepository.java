package com.webapp.timeline.follow.repository;

import com.webapp.timeline.follow.domain.FollowId;
import com.webapp.timeline.follow.domain.Followers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, FollowId> {
    @Query("select count(f) from Followers f where f.id.friendId = :uid and f.isFollow = 1")
    int findFollowNum(@Param("uid") String uid);

    @Query("select count(f) from Followers f where f.id.userId = :uid and f.isFollow = 1")
    int findFollowerNum(@Param("uid") String uid);

    @Query("select count(f) from Followers f where f.id.friendId = :fid and f.id.userId = :uid and f.isFollow = 1")
    int isThisMyFollower(@Param("uid")String uid, @Param("fid")String fid);

    @Modifying
    @Query("update Followers f set f.isAlarm = 0 where f.id.friendId = :fid and f.id.userId = :uid")
    void updateAlarmtoInvalidate(@Param("uid")String uid, @Param("fid")String fid);

    @Modifying
    @Query("update Followers f set f.isFollow = 0, f.isAlarm = 0 where f.id.userId = :fid and f.id.friendId = :uid")
    void updateUnfollow(@Param("uid")String uid, @Param("fid") String fid);

    @Query("select f.id.friendId from Followers f where f.id.userId = :uid and f.isFollow = 1")
    List<String> selectFollowerIdList(@Param("uid") String uid);

    @Query("select fl.id.userId from Followers fl where fl.id.friendId = :uid and fl.isFollow = 1")
    List<String> selectFollowIdList(@Param("uid") String uid);

    @Query("select f.id.userId from Followers f where f.id.friendId = :uid and f.isFollow = 1 and f.id.userId = :fid")
    String selectIsFollowingUser(@Param("uid") String uid,@Param("fid")String fid);

}

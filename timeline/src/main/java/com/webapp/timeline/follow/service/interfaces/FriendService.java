package com.webapp.timeline.follow.service.interfaces;

import com.webapp.timeline.membership.service.response.LoggedInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface FriendService {
    void matchNewRelationship(String uid,String fid) throws RuntimeException;
    ArrayList<LoggedInfo> sendFriendApplyList(HttpServletRequest request) throws RuntimeException;
    void invalidateFriendApplyAlarm(HttpServletRequest request,String fid) throws RuntimeException;
    ArrayList<LoggedInfo> sendFriendList(HttpServletRequest request) throws RuntimeException;
    ArrayList<LoggedInfo> createFriendInfo(List<String> friendList) throws RuntimeException;
    ArrayList<String> sendFollowIdList(String id,Boolean isFollow) throws RuntimeException;
    Optional<String> sendLoginUserId(HttpServletRequest request) throws RuntimeException;
}

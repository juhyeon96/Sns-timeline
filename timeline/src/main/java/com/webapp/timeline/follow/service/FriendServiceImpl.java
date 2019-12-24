package com.webapp.timeline.follow.service;

import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.follow.repository.FollowersRepository;
import com.webapp.timeline.follow.repository.FollowingRepository;
import com.webapp.timeline.membership.domain.Profiles;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.UserService;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FriendServiceImpl implements FriendService{
    FollowingRepository followingRepository;
    FollowersRepository followersRepository;
    UsersEntityRepository usersEntityRepository;
    UserImagesRepository userImagesRepository;
    TokenService tokenService;
    UserService userService;
    public FriendServiceImpl(){}
    @Autowired
    public FriendServiceImpl(UserService userService,UserImagesRepository userImagesRepository,TokenService tokenService,UsersEntityRepository usersEntityRepository,FollowersRepository followersRepository, FollowingRepository followingRepository){
        this.followersRepository = followersRepository;
        this.followingRepository = followingRepository;
        this.usersEntityRepository = usersEntityRepository;
        this.tokenService = tokenService;
        this.userService = userService;
        this.userImagesRepository = userImagesRepository;
    }
    @Override
    @Transactional
    public void matchNewRelationship(String uid,String fid) throws RuntimeException{
        try {
            followingRepository.updateNewFriend(uid, fid);
        }catch(Exception e){
            throw new NoStoringException();
        }
    }
    @Override
    public ArrayList<LoggedInfo> sendFriendApplyList(HttpServletRequest request) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);

        List<String> friendApplyList = followingRepository.findFriendApplyList(userId);
        if(friendApplyList.isEmpty()) throw new NoMatchPointException();

        ArrayList<LoggedInfo> loggedInfoArrayList = this.createFriendInfo(friendApplyList);

        return loggedInfoArrayList;
    }
    @Transactional
    @Override
    public void invalidateFriendApplyAlarm(HttpServletRequest request,String fid) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        try {
            followingRepository.updateIsAlarmtoInvalidate(userId, fid);
            followersRepository.updateIsAlarmtoInvalidate(userId, fid);
        }catch(Exception e){
            throw new NoMatchPointException();
        }
    }
    @Override
    public ArrayList<LoggedInfo> sendFriendList(HttpServletRequest request) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        List<String> friendList = followingRepository.findFirstFriendList(userId);
        friendList.addAll(followingRepository.findSecondFriendList(userId));
        ArrayList<LoggedInfo> friendListContents = this.createFriendInfo(friendList);
        return friendListContents;
    }
    @Override
    public ArrayList<LoggedInfo> searchInFriendList(String nickname,HttpServletRequest request) throws RuntimeException{
        String userId = tokenService.sendIdInCookie(request);
        List<String> friendIdList = usersEntityRepository.findNameInFirstFriendList(userId,nickname);
        friendIdList.addAll(usersEntityRepository.findNameInSecondFriendList(userId,nickname));
        ArrayList<LoggedInfo> friendListForSearching = this.createFriendInfo(friendIdList);
        return friendListForSearching;
    }
    @Override
    public ArrayList<String> sendFriendIdList(String userId) throws RuntimeException{
        List<String> friendList = followingRepository.findFirstFriendList(userId);
        friendList.addAll(followingRepository.findSecondFriendList(userId));
        return (ArrayList<String>) friendList;
    }
    private ArrayList<LoggedInfo> createFriendInfo(List<String> friendList) throws RuntimeException{
        ArrayList<LoggedInfo> friendListContents = new ArrayList<>();
        for(String friendId : friendList) {
            LoggedInfo friendInfo = null;
            try{
                friendInfo = userService.setLoggedInfo(friendId);
            }catch(RuntimeException e){

            }
            if(friendInfo == null) continue;
            friendListContents.add(friendInfo);
        }
        if(friendListContents.isEmpty()) throw new NoMatchPointException();
        return friendListContents;
    }
}

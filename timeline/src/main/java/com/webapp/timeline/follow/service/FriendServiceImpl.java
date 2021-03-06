package com.webapp.timeline.follow.service;

import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.follow.repository.FollowersRepository;
import com.webapp.timeline.follow.repository.FollowingRepository;
import com.webapp.timeline.follow.service.interfaces.FollowService;
import com.webapp.timeline.follow.service.interfaces.FriendService;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.service.TokenService;
import com.webapp.timeline.membership.service.interfaces.UserService;
import com.webapp.timeline.membership.service.response.LoggedInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendServiceImpl implements FriendService {
    Logger log = LoggerFactory.getLogger(this.getClass());
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
        String userId = this.sendLoginUserId(request).get();
        userService.isTrueActualUser(userId);

        List<String> friendApplyList = followingRepository.findFriendApplyList(userId);
        if(friendApplyList.isEmpty()) throw new NoMatchPointException();

        ArrayList<LoggedInfo> loggedInfoArrayList = this.createFriendInfo(friendApplyList);

        return loggedInfoArrayList;
    }
    @Transactional
    @Override
    public void invalidateFriendApplyAlarm(HttpServletRequest request,String fid) throws RuntimeException{
        String userId = this.sendLoginUserId(request).get();
        userService.isTrueActualUser(userId);
        try {
            followingRepository.updateAlarmtoInvalidate(userId, fid);
            followersRepository.updateAlarmtoInvalidate(userId, fid);
        }catch(Exception e){
            throw new NoMatchPointException();
        }
    }
    @Override
    public ArrayList<LoggedInfo> sendFriendList(HttpServletRequest request) throws RuntimeException{
        String userId = this.sendLoginUserId(request).get();
        List<String> friendList = followingRepository.findFirstFriendList(userId);
        friendList.addAll(followingRepository.findSecondFriendList(userId));
        ArrayList<LoggedInfo> friendListContents = this.createFriendInfo(friendList);
        return friendListContents;
    }
    @Override
    public ArrayList<String> sendFollowIdList(String userId,Boolean isFollow) throws RuntimeException{
        List<String> idList;
        if(isFollow) {
            idList = followingRepository.selectFollowIdList(userId);
            idList.addAll(followersRepository.selectFollowIdList(userId));
        }
        else{
            idList = followingRepository.selectFollowerIdList(userId);
            idList.addAll(followersRepository.selectFollowerIdList(userId));
        }
        ArrayList<String> friendIdList = userService.sendActualUserFromList((ArrayList<String>) idList);
        return friendIdList;
    }
    @Override
    public ArrayList<LoggedInfo> createFriendInfo(List<String> friendList) throws RuntimeException{
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
    @Override
    public Optional<String> sendLoginUserId(HttpServletRequest request) throws RuntimeException{
        log.info("FriendServiceImpl.sendLoginUserId::::");
        String userId = Optional.ofNullable(tokenService.sendIdInCookie("accesstoken",request))
                .orElseGet(()->tokenService.sendIdInCookie("kakaoAccesstoken",request));
        log.info(userId);
        userService.isTrueActualUser(userId);
        return Optional.ofNullable(userId);
    }
}

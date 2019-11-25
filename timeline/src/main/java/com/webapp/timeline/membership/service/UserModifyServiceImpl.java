package com.webapp.timeline.membership.service;


import com.webapp.timeline.exception.NoInformationException;
import com.webapp.timeline.exception.NoMatchPointException;
import com.webapp.timeline.exception.NoStoringException;
import com.webapp.timeline.exception.WrongCodeException;
import com.webapp.timeline.membership.domain.Users;
import com.webapp.timeline.membership.repository.UserImagesRepository;
import com.webapp.timeline.membership.repository.UsersEntityRepository;
import com.webapp.timeline.membership.security.CustomPasswordEncoder;
import com.webapp.timeline.membership.security.SignUpValidator;
import com.webapp.timeline.membership.service.result.LoggedInfo;
import com.webapp.timeline.membership.service.result.ValidationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
@Transactional
@Service
public class UserModifyServiceImpl implements UserModifyService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    private UsersEntityRepository usersEntityRepository;
    private CustomPasswordEncoder customPasswordEncoder;
    private SignUpValidator signUpValidator;
    private UserImageS3Component userImageS3Component;
    private UserService userService;
    private UserSignService userSignService;
    private TokenService tokenService;
    private UserSignServiceImpl userSignServiceImpl;
    private UserImagesRepository userImagesRepository;
    @Autowired
    public UserModifyServiceImpl(UserSignServiceImpl userSignServiceImpl,UserImagesRepository userImagesRepository,UserSignService userSignService, UserService userService, UserImageS3Component userImageS3Component, SignUpValidator signUpValidator, CustomPasswordEncoder customPasswordEncoder, UsersEntityRepository usersEntityRepository, TokenService tokenService){
        this.signUpValidator = signUpValidator;
        this.userSignService = userSignService;
        this.userImageS3Component = userImageS3Component;
        this.customPasswordEncoder = customPasswordEncoder;
        this.usersEntityRepository = usersEntityRepository;
        this.tokenService = tokenService;
        this.userService = userService;
        this.userImagesRepository = userImagesRepository;
        this.userSignServiceImpl = userSignServiceImpl;
    }
    @Override
    public void modifyUser(Users user) throws RuntimeException{
        signUpValidator.validateForModify(user);
        if(userSignServiceImpl.loadUserByUsername(user.getId()) == null) throw new NoInformationException();
            user.setPassword(customPasswordEncoder.encode(user.getPassword()));
            try {
                usersEntityRepository.updateUser(user.getGender(), user.getComment(), user.getAddress(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhone(), user.getId());
            }
            catch(Exception e){
                throw new NoStoringException();
            }
    }
    @Override
    public LoggedInfo modifyImage(HttpServletRequest req, MultipartFile file)throws RuntimeException{
        Users user = userSignService.extractUserFromToken(req);
        if(user == null) throw new NoInformationException();
        String url = null;
        try{
            url = userImageS3Component.upload(file, user.getId());
            } catch (IOException e) {
            throw new NoStoringException();
        }
        if(url == null) throw new NoMatchPointException();
        userImagesRepository.updateProfile(user.getId(), url);
        return userService.setLoggedInfo(user.getId());
    }
    @Override
    public void modifyIdentify(HttpServletRequest request, HttpServletResponse response)throws RuntimeException{
        Users user = userSignService.extractUserFromToken(request);
        if(user == null) {
            throw new NoInformationException();
        }
        else{
            user.setAuthoritytoInactive();
            usersEntityRepository.updateUserAuthority(user.getId(),user.getAuthority());
            tokenService.removeCookie(request,response);
        }
    }


}

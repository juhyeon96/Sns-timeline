package com.webapp.timeline.follow.service.interfaces;

import com.webapp.timeline.membership.service.response.LoggedInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public interface SearchService {
    ArrayList<LoggedInfo> searchInFriendList(String nickname, HttpServletRequest request) throws RuntimeException;
   // ArrayList<LoggedInfo> searchInHeader(String nickname) throws RuntimeException;
}

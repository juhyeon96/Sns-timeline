package com.webapp.timeline;

import com.webapp.timeline.config.JpaConfig;
import com.webapp.timeline.domain.Users;
import com.webapp.timeline.repository.UsersEntityRepository;
import com.webapp.timeline.security.CustomPasswordEncoder;
import com.webapp.timeline.security.SignUpValidator;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.Signal;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertUsersTest {
    @Autowired
    private UsersEntityRepository usersEntityRepository;
    @Autowired
    private CustomPasswordEncoder passwordEncoder;
    @Autowired
    private SignUpValidator signUpValidator;

    @Test
    @Transactional
    @Rollback(false)
    public void insertUsers(){
        Users users = new Users("dgds",passwordEncoder.encode("ggdage"),"dgsdag","010-6355-4342","gdsgsdgdsga@naver.com",
                java.sql.Date.valueOf("2019-01-06"),0,"dsgsdag","dga","dgdsg",java.sql.Date.valueOf("2019-01-06"),1,2,3,4);
        users.setAuthorities();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void findUserEmail(){
        usersEntityRepository.findEmailByExistingEmail("dgas");
    }
}

package se2.trackMe.controller.authenticationController;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se2.trackMe.model.security.Authority;
import se2.trackMe.model.security.AuthorityName;
import se2.trackMe.model.security.User;
import se2.trackMe.storageController.AuthorityRepository;
import se2.trackMe.storageController.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "It's okay here")
    private Clock clock = DefaultClock.INSTANCE;

    public void addUser(String username, String password, AuthorityName authorityName){
        List<Authority> roles= new ArrayList<>();
        roles.add(authorityRepository.findByName(authorityName));
        password = passwordEncoder.encode(password);
        User user = new User(username, password,true, clock.now(),roles);
        userRepository.save(user);
    }

    public User getUser(String username){
        return userRepository.findByUsername(username);
    }

    public void updatePassword(String username, String newPassword, String oldPassword) throws Exception{
        newPassword = passwordEncoder.encode(newPassword);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        User user = userRepository.findByUsername(username);
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public Boolean checkUsername(String username, String token){
        return username.equals(jwtTokenUtil.getUsernameFromToken(token));
    }


}

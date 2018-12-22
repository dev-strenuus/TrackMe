package se2.trackMe.controller.authenticationController;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.controller.individualController.IndividualService;
import se2.trackMe.controller.thirdPartyController.ThirdPartyService;
import se2.trackMe.model.Individual;
import se2.trackMe.model.ThirdParty;
import se2.trackMe.model.security.AuthorityName;

import java.util.List;

@RestController
public class UserController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        System.out.println("prova");
        System.out.println(request.getHeader(tokenHeader));
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        return user;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/auth/individual/signUp")
    public void addIndividual(@RequestBody Individual individual){
        if(userService.getUser(individual.getFiscalCode())!=null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This user already exists");
        userService.addUser(individual.getFiscalCode(), individual.getPassword(), AuthorityName.ROLE_INDIVIDUAL);
        individualService.addIndividual(individual);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/auth/thirdParty/signUp")
    public void addThirdParty(@RequestBody ThirdParty thirdParty){
        if(userService.getUser(thirdParty.getVat())!=null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This user already exists");
        userService.addUser(thirdParty.getVat(), thirdParty.getPassword(), AuthorityName.ROLE_THIRDPARTY);
        thirdPartyService.addThirdParty(thirdParty);
    }
    



}

package se2.trackMe.controller.authenticationController;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.web.server.ResponseStatusException;
import se2.trackMe.TrackMeApplication;
import se2.trackMe.controller.individualController.IndividualService;
import se2.trackMe.controller.thirdPartyController.ThirdPartyService;
import se2.trackMe.model.Individual;
import se2.trackMe.model.ThirdParty;

import java.util.Date;

import static org.junit.Assert.*;


@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TrackMeApplication.class)
@DirtiesContext
public class SignUpAndLoginTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationRestController authenticationRestController;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Test
    public void individualSignUpAndLogin() {

        // insert an individual into the database
        Date birthDate = new Date(190000000);
        String fiscalCode = "ciaociaociaociao";
        String password = "password";

        Individual firstIndividual = new Individual(fiscalCode, "Paolo", "Romeo", password, birthDate, 40.5f, 10.0f);
        userController.addIndividual(firstIndividual);

        if (individualService.getIndividual(fiscalCode).isPresent()) {
            Individual retrievedIndividual = individualService.getIndividual(fiscalCode).get();
            assertEquals(firstIndividual.getFiscalCode(), retrievedIndividual.getFiscalCode());
            assertEquals(firstIndividual.getName(), retrievedIndividual.getName());
            assertEquals(firstIndividual.getSurname(), retrievedIndividual.getSurname());
            assertEquals(firstIndividual.getPassword(), retrievedIndividual.getPassword());
            assertEquals(firstIndividual.getBirthDate(), retrievedIndividual.getBirthDate());
            assertEquals(firstIndividual.getLatitude(), retrievedIndividual.getLatitude());
            assertEquals(firstIndividual.getLongitude(), retrievedIndividual.getLongitude());
        } else {
            fail("Individual not found");
        }

        //assertEquals(firstIndividual.toString(), individualService.getIndividual(fiscalCode).get().toString());

        // try to register another individual with the same fiscal code
        exception.expect(ResponseStatusException.class);
        Individual secondIndividual = new Individual(fiscalCode, "Giorgio", "Polla", password, birthDate, 13.f, 33.8f);
        userController.addIndividual(secondIndividual);


        // insert another individual into the database
        Individual thirdIndividual = new Individual("melamelamela2019", "Giorgio", "Romeo", password, birthDate, 40.5f, 10.0f);
        userController.addIndividual(thirdIndividual);

        if (individualService.getIndividual("melamelamela2019").isPresent()) {
            Individual retrievedIndividual = individualService.getIndividual("melamelamela2019").get();
            assertEquals(thirdIndividual.getFiscalCode(), retrievedIndividual.getFiscalCode());
            assertEquals(thirdIndividual.getName(), retrievedIndividual.getName());
            assertEquals(thirdIndividual.getSurname(), retrievedIndividual.getSurname());
            assertEquals(thirdIndividual.getPassword(), retrievedIndividual.getPassword());
            assertEquals(thirdIndividual.getBirthDate(), retrievedIndividual.getBirthDate());
            assertEquals(thirdIndividual.getLatitude(), retrievedIndividual.getLatitude());
            assertEquals(thirdIndividual.getLongitude(), retrievedIndividual.getLongitude());
        } else {
            fail("Individual not found");
        }

        // login of a registered user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(fiscalCode,password));
        final UserDetails userDetailsRegistered = userDetailsService.loadUserByUsername(fiscalCode);
        final String tokenRegistered = jwtTokenUtil.generateToken(userDetailsRegistered);
        assertEquals(userService.checkUsername(fiscalCode,tokenRegistered),true);

        // login of a registered user with wrong password
        exception.expect(BadCredentialsException.class);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(fiscalCode,"wrongpassword"));
        // login of an unregistered user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("notregistered",password));
    }


    @Test
    public void thirdPartySignUpAndLogin() {

        // insert a third party into the database
        String vatNumber = "12345678901";
        String password ="password";
        ThirdParty firstThirdParty = new ThirdParty(vatNumber, "TrackMe", password);
        userController.addThirdParty(firstThirdParty);

        if (thirdPartyService.getThirdParty(vatNumber).isPresent()) {
            ThirdParty retrievedThirdParty = thirdPartyService.getThirdParty(vatNumber).get();
            assertEquals(firstThirdParty.getVat(), retrievedThirdParty.getVat());
            assertEquals(firstThirdParty.getName(), retrievedThirdParty.getName());
            assertEquals(firstThirdParty.getPassword(), retrievedThirdParty.getPassword());
        } else {
            fail("Third party not found");
        }

        // try to register another third party with the same VAT number
        exception.expect(ResponseStatusException.class);
        ThirdParty secondThirdParty = new ThirdParty(vatNumber, "Track4Run", "password2");
        userController.addThirdParty(secondThirdParty);

        // add another third party
        ThirdParty thirdThirdParty = new ThirdParty("09876543211", "Data4Help", "password3");
        userController.addThirdParty(thirdThirdParty);

        if (thirdPartyService.getThirdParty("09876543211").isPresent()) {
            ThirdParty retrievedThirdParty = thirdPartyService.getThirdParty("09876543211").get();
            assertEquals(thirdThirdParty.getVat(), retrievedThirdParty.getVat());
            assertEquals(thirdThirdParty.getName(), retrievedThirdParty.getName());
            assertEquals(thirdThirdParty.getPassword(), retrievedThirdParty.getPassword());
        } else {
            fail("Third party not found");
        }

        // login of a registered third party
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(vatNumber,password));
        final UserDetails userDetailsRegistered = userDetailsService.loadUserByUsername(vatNumber);
        final String tokenRegistered = jwtTokenUtil.generateToken(userDetailsRegistered);
        assertEquals(userService.checkUsername(vatNumber,tokenRegistered),true);

        // login of a registered third party with wrong password
        exception.expect(BadCredentialsException.class);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(vatNumber,"wrongpassword"));
        // login of an unregistered third party
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("notregistered",password));

    }
}

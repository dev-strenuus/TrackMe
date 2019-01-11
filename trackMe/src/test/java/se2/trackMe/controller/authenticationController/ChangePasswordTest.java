package se2.trackMe.controller.individualController;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import se2.trackMe.TrackMeApplication;
import se2.trackMe.controller.authenticationController.UserController;
import se2.trackMe.controller.authenticationController.UserService;
import se2.trackMe.model.Individual;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TrackMeApplication.class)
@DirtiesContext
public class ChangePasswordTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /***
     * This test verifies the correct modification of the Password of the User into the database.
     */
    @Test
    public void ChangeLocationTest(){
        Date birthDate = new Date(000000000);
        String fiscalCode = "fiscalCodeTest00";
        String password = "password";
        Individual individual = new Individual(fiscalCode, "name", "surname", password, birthDate, 40.5f, 10.0f);
        userController.addIndividual(individual);
        String newPassword= "newPassword";
        try {
            userService.updatePassword(fiscalCode,newPassword,password);
        } catch (Exception e) {
            fail();
        }

        try {
            userService.updatePassword(fiscalCode,"newPassword","fakePassword");
            fail();
        } catch (Exception e) {
        }

        assertEquals(individualService.getIndividual(fiscalCode).get().getPassword(),newPassword);

    }
}

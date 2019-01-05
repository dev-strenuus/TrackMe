package se2.trackMe.controller.authenticationController;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import se2.trackMe.TrackMeApplication;
import se2.trackMe.controller.individualController.IndividualController;
import se2.trackMe.controller.individualController.IndividualService;
import se2.trackMe.controller.thirdPartyController.ThirdPartyService;
import se2.trackMe.model.Individual;
import se2.trackMe.model.ThirdParty;

import java.util.Date;

import static org.junit.Assert.*;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TrackMeApplication.class)
@DatabaseSetup(value = SignUpAndLoginTest.DATASET)
@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = SignUpAndLoginTest.DATASET)
@DirtiesContext
public class SignUpAndLoginTest {
    static final String DATASET = "/registrationData.xml";

    @Autowired
    private UserController userController;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Test
    public void individualSignUpAndLogin() {

        // insert and individual into the database
        Date birthDate = new Date(190000000);
        String fiscalCode = "ciaociaociaociao";

        Individual firstIndividual = new Individual(fiscalCode, "Paolo", "Romeo", "password", birthDate, 40.5f, 10.0f);
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
        Individual secondIndividual = new Individual(fiscalCode, "Giorgio", "Polla", "password", birthDate, 13.f, 33.8f);
        try {
            userController.addIndividual(secondIndividual);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // insert another individual into the database
        Individual thirdIndividual = new Individual("melamelamela2019", "Giorgio", "Romeo", "password", birthDate, 40.5f, 10.0f);
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

        // login of an unregistered user

    }


    @Test
    public void thirdPartySignUpAndLogin() {

        // insert a third party into the database
        String vatNumber = "12345678901";
        ThirdParty firstThirdParty = new ThirdParty(vatNumber, "TrackMe", "password");
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
        ThirdParty secondThirdParty = new ThirdParty(vatNumber, "Track4Run", "password2");
        try {
            userController.addThirdParty(secondThirdParty);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        // login of an unregistered third party
    }
}

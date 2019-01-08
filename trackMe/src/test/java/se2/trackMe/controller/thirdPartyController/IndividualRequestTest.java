package se2.trackMe.controller.thirdPartyController;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import se2.trackMe.TrackMeApplication;
import se2.trackMe.controller.authenticationController.UserController;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TrackMeApplication.class)
@DatabaseSetup(value = IndividualRequestTest.DATASET)
@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = IndividualRequestTest.DATASET)
@DirtiesContext
public class IndividualRequestTest {
    protected static final String DATASET = "/registrationData.xml"; //EMPTY

    @Autowired
    private UserController userController;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    public void IndividualRequestCorrectlyInserted() {
        String vatNumber = "vatNumber";
        Date birthDate = new Date(190000000);
        String fiscalCode = "ciaociaociaociao";
        String password = "password";

        Individual individual = new Individual(fiscalCode, "Paolo", "Romeo", password, birthDate, 40.5f, 10.0f);
        userController.addIndividual(individual);
        ThirdParty thirdParty = new ThirdParty(vatNumber,"TerzaParte", password);
        userController.addThirdParty(thirdParty);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(vatNumber, password));
        thirdPartyService.addIndividualRequest(thirdParty, individual, true);

        //come testare errore nel codice fiscale utente? manca step precedente: dal codice fiscale all'individuo
        assertEquals(thirdPartyService.getIndividualRequest(thirdParty, individual).isPresent(), true);
    }
}


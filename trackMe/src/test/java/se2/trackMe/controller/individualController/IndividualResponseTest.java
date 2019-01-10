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
import se2.trackMe.controller.thirdPartyController.ThirdPartyService;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TrackMeApplication.class)
@DirtiesContext
public class IndividualResponseTest {

    @Autowired
    private UserController userController;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /***
     * This test verifies the correct insertion of the Answer to the Notification related to the received Individual Request into the database.
     */
    @Test
    public void IndividualResponseTest() {


        String vatNumber = "vatNumber";
        Date birthDate = new Date(000000000);
        String fiscalCode = "fiscalCodeTest00";
        String password = "password";
        Individual individual = new Individual(fiscalCode, "name", "surname", password, birthDate, 40.5f, 10.0f);
        userController.addIndividual(individual);
        ThirdParty thirdParty = new ThirdParty(vatNumber,"thirdParty", password);
        userController.addThirdParty(thirdParty);
        thirdPartyService.addIndividualRequest(thirdParty, individual, true);
        IndividualRequest individualRequest = individualService.getIndvidualPendingRequestList(individual).get(0);
        individualRequest.setAccepted(true);
        individualService.setIndividualRequestAnswer(individualRequest);

        assertEquals(individualService.getIndividualAcceptedRequestList(individual).size(), 1);

        individualRequest.setAccepted(false);
        individualService.setIndividualRequestAnswer(individualRequest);

        assertEquals(individualService.getIndividualAcceptedRequestList(individual).size(), 0);
    }
}

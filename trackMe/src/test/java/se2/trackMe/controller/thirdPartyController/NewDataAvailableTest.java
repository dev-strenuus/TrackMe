package se2.trackMe.controller.thirdPartyController;

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
import se2.trackMe.controller.individualController.IndividualService;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualData;
import se2.trackMe.model.IndividualRequest;
import se2.trackMe.model.ThirdParty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TrackMeApplication.class)
@DirtiesContext
public class NewDataAvailableTest {

    @Autowired
    private UserController userController;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /***
     * This test verifies the correct insertion of the Notification of the Answer to an Individual Request into the database.
     */
    @Test
    public void NewDataAvailableTest() {
        String vatNumber = "vatNumber";
        Date birthDate = new Date(000000000);
        String fiscalCode = "fiscalCodeTest00";
        String password = "password";
        Individual individual = new Individual(fiscalCode, "name", "surname", password, birthDate, 40.5f, 10.0f);
        userController.addIndividual(individual);
        ThirdParty thirdParty = new ThirdParty(vatNumber,"thirdParty", password);
        userController.addThirdParty(thirdParty);
        thirdPartyService.addIndividualRequest(thirdParty, individual, true);
        IndividualData individualData = new IndividualData(new Date(00000000), 60f ,120f ,150f ,50f );
        List individualDataList= new ArrayList();
        individualDataList.add(individualData);
        individualData.setIndividual(individual);
        individualService.saveData(individualDataList);

        assertEquals(thirdPartyService.getNewDataNotificationList(thirdParty,individual).size(),1);
    }
}

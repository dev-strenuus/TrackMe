package se2.trackMe.controller.thirdPartyController;

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


import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.*;
import se2.trackMe.TrackMeApplication;
import se2.trackMe.model.AnonymousRequest;
import se2.trackMe.model.ThirdParty;
import se2.trackMe.storageController.*;

import java.text.SimpleDateFormat;

@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TrackMeApplication.class)
@DatabaseSetup(value = AnonymousRequestBuilderTest.DATASET)
@DatabaseTearDown(type = DatabaseOperation.DELETE_ALL, value = AnonymousRequestBuilderTest.DATASET)
@DirtiesContext
public class AnonymousRequestBuilderTest {
    protected static final String DATASET = "/individualData.xml";

    @Autowired
    private AnonymousRequestBuilder anonymousRequestBuilder;

    @Autowired
    private ThirdPartyNotificationRepository thirdPartyNotificationRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AnonymousRequestRepository anonymousRequestRepository;

    @Autowired
    private AnonymousAnswerRepository anonymousAnswerRepository;


    @Test
    public void getDateFromFirstData() {
        assertEquals(anonymousRequestBuilder.getDateFromFirstData().toString(), "2018-12-24 12:53:37.61");
    }

    @Test
    public void getDistinctIndividual() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        assertEquals(anonymousRequestBuilder.getDistinctIndividual(dateFormat.parse("2018-12-24 12:53:37.61"), dateFormat.parse("2018-12-24 13:53:37.61"), null, null, new Float(-10), new Float(10), new Float(-10), new Float(10)), new Integer(3));
        assertEquals(anonymousRequestBuilder.getDistinctIndividual(dateFormat.parse("2018-12-24 13:53:37.61"), dateFormat.parse("2018-12-24 14:53:37.61"), null, null, new Float(-10), new Float(10), new Float(-10), new Float(10)), new Integer(5));
        assertEquals(anonymousRequestBuilder.getDistinctIndividual(dateFormat.parse("2018-12-24 14:53:37.61"), dateFormat.parse("2018-12-24 15:53:37.61"), null, null, new Float(-10), new Float(10), new Float(-10), new Float(10)), new Integer(3));
        assertEquals(anonymousRequestBuilder.getDistinctIndividual(dateFormat.parse("2018-12-24 15:53:37.61"), dateFormat.parse("2018-12-24 16:53:37.61"), new Integer(22), new Integer(22), new Float(-10), new Float(10), new Float(-10), new Float(10)), new Integer(3));
        assertEquals(anonymousRequestBuilder.getDistinctIndividual(dateFormat.parse("2018-12-24 16:53:37.61"), dateFormat.parse("2018-12-24 17:53:37.61"), new Integer(22), new Integer(22), new Float(-10), new Float(10), new Float(-10), new Float(10)), new Integer(4));
        assertEquals(anonymousRequestBuilder.getDistinctIndividual(dateFormat.parse("2018-12-24 17:53:37.61"), dateFormat.parse("2018-12-24 18:53:37.61"), new Integer(22), new Integer(23), null, null, null, null), new Integer(5));
        assertEquals(anonymousRequestBuilder.getDistinctIndividual(dateFormat.parse("2018-12-24 17:53:37.61"), dateFormat.parse("2018-12-24 18:53:37.61"), null, null, null, null, null, null), new Integer(0));
    }

    @Test
    public void getData() throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        assertNull(anonymousRequestBuilder.getData(dateFormat.parse("2018-12-24 12:53:37.61"), dateFormat.parse("2018-12-24 13:53:37.61"), null, null, new Float(-10), new Float(10), new Float(-10), new Float(10)));
        assertThat(anonymousRequestBuilder.getData(dateFormat.parse("2018-12-24 13:53:37.61"), dateFormat.parse("2018-12-24 14:53:37.61"), null, null, new Float(-10), new Float(10), new Float(-10), new Float(10)), hasSize(5));
        assertNull(anonymousRequestBuilder.getData(dateFormat.parse("2018-12-24 14:53:37.61"), dateFormat.parse("2018-12-24 15:53:37.61"), null, null, new Float(-10), new Float(10), new Float(-10), new Float(10)));
        assertNull(anonymousRequestBuilder.getData(dateFormat.parse("2018-12-24 15:53:37.61"), dateFormat.parse("2018-12-24 16:53:37.61"), new Integer(22), new Integer(22), new Float(-10), new Float(10), new Float(-10), new Float(10)));
        assertThat(anonymousRequestBuilder.getData(dateFormat.parse("2018-12-24 16:53:37.61"), dateFormat.parse("2018-12-24 17:53:37.61"), new Integer(22), new Integer(22), new Float(-10), new Float(10), new Float(-10), new Float(10)), hasSize(5));
        assertThat(anonymousRequestBuilder.getData(dateFormat.parse("2018-12-24 17:53:37.61"), dateFormat.parse("2018-12-24 18:53:37.61"), new Integer(22), new Integer(23), null, null, null, null), hasSize(6));
        assertNull(anonymousRequestBuilder.getData(dateFormat.parse("2018-12-24 17:53:37.61"), dateFormat.parse("2018-12-24 18:53:37.61"), null, null, null, null, null, null));
    }

    @Test
    public void calculate() {
        ThirdParty thirdParty = new ThirdParty("ciao");
        thirdPartyRepository.save(thirdParty);
        assertThat(thirdPartyNotificationRepository.findAllByThirdParty(thirdParty), hasSize(0));
        AnonymousRequest anonymousRequest = new AnonymousRequest(thirdParty, new Integer(22), new Integer(23), new Float(-10), new Float(10), new Float(-10), new Float(10), false);
        anonymousRequestBuilder.calculate(anonymousRequest);
        assertThat(thirdPartyNotificationRepository.findAllByThirdParty(thirdParty), hasSize(3));
        thirdPartyNotificationRepository.deleteAll();
        anonymousAnswerRepository.deleteAll();
        anonymousRequestRepository.deleteAll();
        thirdPartyRepository.deleteAll();


    }
}

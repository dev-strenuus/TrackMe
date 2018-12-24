package se2.trackMe.controller.thirdPartyController;

import org.junit.After;
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


import static org.junit.Assert.*;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.*;
import se2.trackMe.TrackMeApplication;
import se2.trackMe.storageController.IndividualDataRepository;

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
    private IndividualDataRepository individualDataRepository;


    @Test
    public void getDateFromFirstData() {
        assertEquals(individualDataRepository.findMinimumTimestamp().toString(), "2018-12-24 14:36:37.61");
    }

    @Test
    public void getDistinctIndividual() {
    }

    @Test
    public void getData() {
    }

    @Test
    public void calculate() {
    }
}

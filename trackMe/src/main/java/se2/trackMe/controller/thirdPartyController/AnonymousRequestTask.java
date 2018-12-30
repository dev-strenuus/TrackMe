package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se2.trackMe.model.AnonymousRequest;
import se2.trackMe.model.IndividualData;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;


public class AnonymousRequestTask extends TimerTask {


    private final int fixedRange = 30000; //1000*60*60; //one hour in milliseconds

    private AnonymousRequest anonymousRequest;

    private AnonymousRequestBuilder anonymousRequestBuilder;

    private Date lastIteration;

    public AnonymousRequestTask(AnonymousRequest anonymousRequest, AnonymousRequestBuilder anonymousRequestBuilder, Date iteration){

        this.anonymousRequest = anonymousRequest;
        this.anonymousRequestBuilder = anonymousRequestBuilder;
        this.lastIteration = iteration;
    }
    @Override
    public void run() {
        Date now = new Date(lastIteration.getTime()+fixedRange);
        System.out.println("1 ->"+lastIteration+" -> "+now);
        List<IndividualData> individualDataList = anonymousRequestBuilder.getData(lastIteration, now, anonymousRequest.getStartAge(),anonymousRequest.getEndAge(),anonymousRequest.getLat1(),anonymousRequest.getLat2(),anonymousRequest.getLon1(),anonymousRequest.getLon2());
        anonymousRequestBuilder.elaborate(anonymousRequest, individualDataList, lastIteration, now);
        System.out.println("2 ->"+lastIteration+" -> "+now);
        lastIteration = now;

    }
}

package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se2.trackMe.model.AnonymousRequest;
import se2.trackMe.model.IndividualData;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;


public class AnonymousRequestTask extends TimerTask {


    private AnonymousRequest anonymousRequest;

    private AnonymousRequestBuilder anonymousRequestBuilder;

    private Date lastIteration;

    public AnonymousRequestTask(AnonymousRequest anonymousRequest, AnonymousRequestBuilder anonymousRequestBuilder){

        this.anonymousRequest = anonymousRequest;
        this.anonymousRequestBuilder = anonymousRequestBuilder;
        this.lastIteration = new Date();
    }
    @Override
    public void run() {
        Date now = new Date();
        System.out.println(lastIteration+" "+now);
        List<IndividualData> individualDataList = anonymousRequestBuilder.getData(lastIteration, now, anonymousRequest.getStartAge(),anonymousRequest.getEndAge(),anonymousRequest.getLat1(),anonymousRequest.getLat2(),anonymousRequest.getLon1(),anonymousRequest.getLon2());
        anonymousRequestBuilder.elaborate(anonymousRequest, individualDataList, lastIteration, now);
        lastIteration = now;

    }
}

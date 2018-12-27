package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import se2.trackMe.model.AnonymousRequest;
import se2.trackMe.model.IndividualData;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class AnonymousRequestTask extends TimerTask {


    private AnonymousRequest anonymousRequest;
    @Autowired
    private AnonymousRequestBuilder anonymousRequestBuilder;

    private Date lastIteration;

    public AnonymousRequestTask(AnonymousRequest anonymousRequest){

        this.anonymousRequest = anonymousRequest;
        this.lastIteration = new Date();
    }
    @Override
    public void run() {
        Date now = new Date();
        List<IndividualData> individualDataList = anonymousRequestBuilder.getData(lastIteration, now, anonymousRequest.getStartAge(),anonymousRequest.getEndAge(),anonymousRequest.getLat1(),anonymousRequest.getLat2(),anonymousRequest.getLon1(),anonymousRequest.getLon2());
        anonymousRequestBuilder.elaborate(anonymousRequest, individualDataList, lastIteration, now);
        lastIteration = now;

    }
}

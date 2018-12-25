package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se2.trackMe.model.*;
import se2.trackMe.storageController.AnonymousRequestRepository;
import se2.trackMe.storageController.IndividualDataRepository;
import se2.trackMe.storageController.ThirdPartyNotificationRepository;

import java.util.Date;
import java.util.List;

@Service
public class AnonymousRequestBuilder {

    @Autowired
    private IndividualDataRepository individualDataRepository;

    @Autowired
    private ThirdPartyNotificationRepository thirdPartyNotificationRepository;

    @Autowired
    private AnonymousRequestRepository anonymousRequestRepository;

    private final int threshold = 10;

    public Date getDateFromFirstData(){
        return individualDataRepository.findMinimumTimestamp();
    }

    public Integer getDistinctIndividual(Date startDate, Date endDate, Integer startAge, Integer endAge, Float lat1, Float lat2, Float lon1, Float lon2){
        if(startAge != null && endAge != null && lat1 != null && lat2 != null && lon1 != null && lon2 != null)
            return individualDataRepository.countDistinctByIndividualByAgeAndByPos(startDate, endDate, startAge, endAge, lat1, lat2, lon1, lon2);
        if(startAge != null && endAge != null)
            return individualDataRepository.countDistinctByIndividualByAge(startDate, endDate, startAge, endAge);
        if(lat1 != null && lat2 != null && lon1 != null && lon2 != null)
            return individualDataRepository.countDistinctByIndividualByPos(startDate, endDate, lat1, lat2, lon1, lon2);
        return 0;
    }


    public List<IndividualData> getData(Date startDate, Date endDate, Integer startAge, Integer endAge, Float lat1, Float lat2, Float lon1, Float lon2){
        if(getDistinctIndividual(startDate, endDate, startAge, endAge, lat1, lat2, lon1, lon2) < threshold)
            return null;

        if(startAge != null && endAge != null && lat1 != null && lat2 != null && lon1 != null && lon2 != null)
        return individualDataRepository.findAllByAgeAndPos(startDate, endDate, startAge, endAge, lat1, lat2, lon1, lon2);
        if(startAge != null && endAge != null)
            return individualDataRepository.findAllByAge(startDate, endDate, startAge, endAge);
        if(lat1 != null && lat2 != null && lon1 != null && lon2 != null)
            return individualDataRepository.findAllByPos(startDate, endDate, lat1, lat2, lon1, lon2);
        return null;

    }

    public void calculate(AnonymousRequest anonymousRequest){
        anonymousRequestRepository.save(anonymousRequest);
        Date now = new Date();
        Date iteration = getDateFromFirstData();
        while(iteration.before(now)){
            Date newIteration = new Date(iteration.getTime()+1000*60*60);
            List<IndividualData> individualDataList = getData(iteration, newIteration, anonymousRequest.getStartAge(),anonymousRequest.getEndAge(),anonymousRequest.getLat1(),anonymousRequest.getLat2(),anonymousRequest.getLon1(),anonymousRequest.getLon2());
            if(individualDataList != null)
                thirdPartyNotificationRepository.save(new ThirdPartyNotification(new AnonymousAnswer(iteration, newIteration, individualDataList, anonymousRequest), anonymousRequest.getThirdParty()));
            iteration = newIteration;
        }

    }
}


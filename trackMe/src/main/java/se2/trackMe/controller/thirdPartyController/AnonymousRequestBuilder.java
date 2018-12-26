package se2.trackMe.controller.thirdPartyController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se2.trackMe.model.*;
import se2.trackMe.storageController.AnonymousAnswerRepository;
import se2.trackMe.storageController.AnonymousRequestRepository;
import se2.trackMe.storageController.IndividualDataRepository;
import se2.trackMe.storageController.ThirdPartyNotificationRepository;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * This class elaborates anonymous requests without letting the third party, who made the request, to wait for an answer.
 * This is possible because, as soon as some data is ready to be delivered, these data will be added as a {@link AnonymousAnswer} inside a {@link ThirdPartyNotification}.
 * The anonymity is guaranteed by checking every <tt>fixedRange</tt> of time if there are at least <tt>threshold</tt> individuals who sent, at least one time, one {@link IndividualData}, in that range of time.
 */
@Service
public class AnonymousRequestBuilder {

    @Autowired
    private IndividualDataRepository individualDataRepository;

    @Autowired
    private ThirdPartyNotificationRepository thirdPartyNotificationRepository;

    @Autowired
    private AnonymousRequestRepository anonymousRequestRepository;

    @Autowired
    private AnonymousAnswerRepository anonymousAnswerRepository;


    /**
     * Minimum number of individuals requested to be in the group in order to guarantee anonymity.
     */
    private final int threshold = 4;

    /**
     * The check of anonymity and the retrieval of data are done every this quantity of time.
     */
    private final int fixedRange = 1000*60*60; //one hour in milliseconds

    /**
     * @return the timestamp of the first {@link IndividualData} inserted in DB (this could be improved by checking only the group)
     */
    public Date getDateFromFirstData(){
        return individualDataRepository.findMinimumTimestamp();
    }

    /**
     * Check how many distinct individuals are present in the requested group.
     * @param startDate
     * @param endDate
     * @param startAge
     * @param endAge
     * @param lat1
     * @param lat2
     * @param lon1
     * @param lon2
     * @return
     */
    public Integer getDistinctIndividual(Date startDate, Date endDate, Integer startAge, Integer endAge, Float lat1, Float lat2, Float lon1, Float lon2){
        if(startAge != null && endAge != null && lat1 != null && lat2 != null && lon1 != null && lon2 != null)
            return individualDataRepository.countDistinctByIndividualByAgeAndByPos(startDate, endDate, startAge, endAge, lat1, lat2, lon1, lon2);
        if(startAge != null && endAge != null)
            return individualDataRepository.countDistinctByIndividualByAge(startDate, endDate, startAge, endAge);
        if(lat1 != null && lat2 != null && lon1 != null && lon2 != null)
            return individualDataRepository.countDistinctByIndividualByPos(startDate, endDate, lat1, lat2, lon1, lon2);
        return 0;
    }


    /**
     * Get the Data from the database only if anonymity is guaranteed.
     * @param startDate this request asks for timestamp >= startDate
     * @param endDate this request asks for timestamp < startDate
     * @param startAge
     * @param endAge
     * @param lat1
     * @param lat2
     * @param lon1
     * @param lon2
     * @return
     */
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

    /**
     * From the timestamp of the first {@link IndividualData} in DB it will check, until now, with a fixed range of one hour, if there is a group that satisfy the anonymous request.
     * If the third party, who made the request, subscribed to new data, this operation will be done every hour from so on.
     * @param anonymousRequest
     */
    public void calculate(AnonymousRequest anonymousRequest){
        anonymousRequestRepository.save(anonymousRequest);
        Date now = new Date();
        Date iteration = getDateFromFirstData();
        while(iteration.before(now)){
            Date newIteration = new Date(iteration.getTime()+fixedRange);
            List<IndividualData> individualDataList = getData(iteration, newIteration, anonymousRequest.getStartAge(),anonymousRequest.getEndAge(),anonymousRequest.getLat1(),anonymousRequest.getLat2(),anonymousRequest.getLon1(),anonymousRequest.getLon2());
            if(individualDataList != null){
                AnonymousAnswer anonymousAnswer = new AnonymousAnswer(iteration, newIteration, individualDataList, anonymousRequest);
                anonymousAnswerRepository.save(anonymousAnswer);
                thirdPartyNotificationRepository.save(new ThirdPartyNotification(anonymousAnswer, anonymousRequest.getThirdParty()));
            }
            iteration = newIteration;
        }
        if(anonymousRequest.getSubscribedToNewData() == true){
            //TODO
        }
    }


}


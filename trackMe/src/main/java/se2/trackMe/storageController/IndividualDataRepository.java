package se2.trackMe.storageController;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.Individual;
import se2.trackMe.model.IndividualData;

import java.util.Date;
import java.util.List;

public interface IndividualDataRepository extends CrudRepository<IndividualData, Long> {
    @Query("SELECT COUNT(DISTINCT individual.fiscalCode) FROM IndividualData WHERE (timestamp BETWEEN ?1 AND ?2) AND (age BETWEEN ?3 AND ?4) ")
    Integer countDistinctByIndividualByAge(Date startDate, Date endDate, Integer startAge, Integer endAge);
    @Query("SELECT COUNT(DISTINCT individual.fiscalCode) FROM IndividualData WHERE (timestamp BETWEEN ?1 AND ?2) AND (lat BETWEEN ?3 AND ?4) AND (lon BETWEEN ?5 AND ?6)")
    Integer countDistinctByIndividualByPos(Date startDate, Date endDate, Float lat1, Float lat2, Float lon1, Float lon2);
    @Query("SELECT COUNT(DISTINCT individual.fiscalCode) FROM IndividualData WHERE (timestamp BETWEEN ?1 AND ?2) AND (age BETWEEN ?3 AND ?4) AND (lat BETWEEN ?5 AND ?6) AND (lon BETWEEN ?7 AND ?8)")
    Integer countDistinctByIndividualByAgeAndByPos(Date startDate, Date endDate, Integer startAge, Integer endAge, Float lat1, Float lat2, Float lon1, Float lon2);
    @Query("SELECT i FROM IndividualData i WHERE (i.timestamp BETWEEN ?1 AND ?2) AND (i.age BETWEEN ?3 AND ?4)")
    List<IndividualData> findAllByAge(Date startDate, Date endDate, Integer startAge, Integer endAge);
    @Query("SELECT i FROM IndividualData i WHERE (i.timestamp BETWEEN ?1 AND ?2) AND (lat BETWEEN ?3 AND ?4) AND (lon BETWEEN ?5 AND ?6)")
    List<IndividualData> findAllByPos(Date startDate, Date endDate, Float lat1, Float lat2, Float lon1, Float lon2);
    @Query("SELECT i FROM IndividualData i WHERE (i.timestamp BETWEEN ?1 AND ?2) AND (i.age BETWEEN ?3 AND ?4) AND (lat BETWEEN ?5 AND ?6) AND (lon BETWEEN ?7 AND ?8)")
    List<IndividualData> findAllByAgeAndPos(Date startDate, Date endDate, Integer startAge, Integer endAge, Float lat1, Float lat2, Float lon1, Float lon2);
    @Query("SELECT MIN(timestamp) FROM IndividualData")
    Date findMinimumTimestamp();
}

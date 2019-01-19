package se2.trackMe.model;

import com.fasterxml.jackson.annotation.JsonView;
import se2.trackMe.model.profileJSON.Profile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class IndividualData {

    @Id
    @GeneratedValue
    private Long id;

    //@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="GMT")
    @JsonView(Profile.AnonymousRequestPublicView.class)
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date timestamp;

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @Column
    @NotNull
    private Float heartRate;

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @Column
    @NotNull
    private Float systolicBloodPressure;

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @Column
    @NotNull
    private Float diastolicBloodPressure;

    @JsonView(Profile.AnonymousRequestPublicView.class)
    @Column
    @NotNull
    private Float oxygenPercentage;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @ManyToOne
    @NotNull
    private Individual individual;
    
    @Column
    @NotNull
    private Integer age;

    @Column
    @NotNull
    private Float latitude;

    @Column
    @NotNull
    private Float longitude;

    public IndividualData() {
    }


    public IndividualData(@NotNull Date timestamp, @NotNull Float heartRate, @NotNull Float systolicBloodPressure, @NotNull Float diastolicBloodPressure, @NotNull Float oxygenPercentage) {
        this.timestamp = timestamp;
        this.heartRate = heartRate;
        this.systolicBloodPressure = systolicBloodPressure;
        this.diastolicBloodPressure = diastolicBloodPressure;
        this.oxygenPercentage = oxygenPercentage;
    }

    public Long getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Float getHeartRate() {
        return heartRate;
    }

    public Float getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public Float getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    public Float getOxygenPercentage() {
        return oxygenPercentage;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}

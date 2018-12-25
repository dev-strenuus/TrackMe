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
    @JsonView(Profile.ThirdPartyPublicView.class)
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date timestamp;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @Column
    @NotNull
    private Float heartRate;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @Column
    @NotNull
    private Float systolicBloodPressure;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @Column
    @NotNull
    private Float diastolicBloodPressure;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @Column
    @NotNull
    private Float oxygenPercentage;

    @JsonView(Profile.ThirdPartyPublicView.class)
    @ManyToOne
    @NotNull
    private Individual individual;

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
}

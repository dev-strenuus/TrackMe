package se2.trackMe.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class AnonymousRequest {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private ThirdParty thirdParty;

    @Column
    private Integer startAge;

    @Column
    private Integer endAge;

    @Column
    private Float lat1;

    @Column
    private Float lat2;

    @Column
    private Float lon2;

    @Column
    private Float lon1;

    @Column
    @NotNull
    private Boolean subscribedToNewData;

    public AnonymousRequest(){}

    public AnonymousRequest(@NotNull ThirdParty thirdParty, Integer startAge, Integer endAge, Float lat1, Float lat2, Float lon1, Float lon2, Boolean subscribedToNewData) {
        this.thirdParty = thirdParty;
        this.startAge = startAge;
        this.endAge = endAge;

        this.lat1 = lat1;
        this.lat2 = lat2;
        this.lon2 = lon2;
        this.lon1 = lon1;
        this.subscribedToNewData = subscribedToNewData;
    }

    public Long getId() {
        return id;
    }

    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public Integer getStartAge() {
        return startAge;
    }

    public Integer getEndAge() {
        return endAge;
    }

    public Float getLat1() {
        return lat1;
    }

    public Float getLat2() {
        return lat2;
    }

    public Float getLon2() {
        return lon2;
    }

    public Float getLon1() {
        return lon1;
    }

    public void setThirdParty(ThirdParty thirdParty){
        this.thirdParty = thirdParty;
    }

    public Boolean getSubscribedToNewData() {
        return subscribedToNewData;
    }
}

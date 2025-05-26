package com.parkingapp.backendapi.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum State {
    AL("Alabama"), AK("Alaska"), AZ("Arizona"), AR("Arkansas"), CA("California"),
    CO("Colorado"), CT("Connecticut"), DE("Delaware"), FL("Florida"), GA("Georgia"),
    HI("Hawaii"), ID("Idaho"), IL("Illinois"), IN("Indiana"), IA("Iowa"), KS("Kansas"),
    KY("Kentucky"), LA("Louisiana"), ME("Maine"), MD("Maryland"), MA("Massachusetts"),
    MI("Michigan"), MN("Minnesota"), MS("Mississippi"), MO("Missouri"), MT("Montana"),
    NE("Nebraska"), NV("Nevada"), NH("New Hampshire"), NJ("New Jersey"), NM("New Mexico"),
    NY("New York"), NC("North Carolina"), ND("North Dakota"), OH("Ohio"), OK("Oklahoma"),
    OR("Oregon"), PA("Pennsylvania"), RI("Rhode Island"), SC("South Carolina"), SD("South Dakota"),
    TN("Tennessee"), TX("Texas"), UT("Utah"), VT("Vermont"), VA("Virginia"), WA("Washington"),
    WV("West Virginia"), WI("Wisconsin"), WY("Wyoming");

    private final String fullName;

    private static final Map<String, State> FULL_NAME_MAP = new HashMap<>();

    static{
        for(State state: values()){
            FULL_NAME_MAP.put(state.fullName.toLowerCase(), state);
        }
    }

    // constructor
    State(String fullName) {
        this.fullName = fullName;
    }

    // given an enum get full name
    public String getFullName() {
        return fullName;
    }

    // get enum by full name
    public static State fromFullName(String fullName){
        return FULL_NAME_MAP.get(fullName.toLowerCase());
    }
}

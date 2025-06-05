package com.parkingapp.backendapi.jurisdiction.service;

import com.parkingapp.backendapi.common.enums.State;
import com.parkingapp.backendapi.jurisdiction.entity.Jurisdiction;
import com.parkingapp.backendapi.jurisdiction.repository.JurisdictionRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j // logging
@AllArgsConstructor
public class JurisdictionCacheService {

    private final JurisdictionRepository jurisdictionRepository;

    private final Map<String, Jurisdiction> jurisdictionCache = new ConcurrentHashMap<>();

    @PostConstruct // called automatically after the bean is constructed
    public void preloadJurisdictions(){
        log.info("Preloading jurisdiction data into cache...");

        List<Jurisdiction> allJurisdictions = jurisdictionRepository.findAll();

        for (Jurisdiction jurisdiction : allJurisdictions){
            // state enum converts to String type
            String key = buildJurisdictionKey(jurisdiction.getState().toString(), jurisdiction.getCity());
            jurisdictionCache.put(key, jurisdiction);
        }
        log.info("Preloaded {} jurisdictions.", jurisdictionCache.size());
    }

    // key follows "StateInitials-City" format per client
    private String buildJurisdictionKey(String state, String city) {
        return (state + "-" + city).toUpperCase();
    }

    public Optional<Jurisdiction> findJurisdictionByStateAndCity(String state, String city){
        String key = buildJurisdictionKey(state, city);
        return Optional.ofNullable(jurisdictionCache.get(key));
    }
}

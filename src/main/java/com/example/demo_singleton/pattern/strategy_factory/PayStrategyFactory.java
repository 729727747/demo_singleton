package com.example.demo_singleton.pattern.strategy_factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PayStrategyFactory {

    @Autowired
    private Map<String , PayStrategyService> payStrategyMap;


    public PayStrategyService getPayStrategyService(String payCode){
        if(payCode == null){
            throw new IllegalArgumentException("payCode is null");
        }
        for (PayStrategyService payStrategyService:payStrategyMap.values()) {
            if(payStrategyService.getPayCode().equalsIgnoreCase(payCode)){
                return payStrategyService;
            }
        }
        throw new IllegalArgumentException("payCode not found");
    }



}

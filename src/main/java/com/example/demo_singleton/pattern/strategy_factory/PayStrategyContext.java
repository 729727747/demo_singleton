package com.example.demo_singleton.pattern.strategy_factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PayStrategyContext {

    @Autowired
    private PayStrategyFactory payStrategyFactory;

    public void pay(String payCode, BigDecimal amount){
        PayStrategyService payStrategyService = payStrategyFactory.getPayStrategyService(payCode);
        String pay = payStrategyService.pay(amount);
        System.out.println(pay);
    }

}

package com.example.demo_singleton.pattern.strategy_factory;

import java.math.BigDecimal;

public interface PayStrategyService {


    public String pay(BigDecimal amount);

    public String getPayCode();

}

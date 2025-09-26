package com.example.demo_singleton.pattern.strategy_factory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AliPayStrategyServiceImpl implements PayStrategyService {
    @Override
    public String pay(BigDecimal amount) {
        return "支付宝支付"+amount;
    }

    @Override
    public String getPayCode() {
        return "aliPay";
    }
}

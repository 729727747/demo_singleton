package com.example.demo_singleton.pattern.strategy_factory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WechatPayStrategyServiceImpl implements PayStrategyService {
    @Override
    public String pay(BigDecimal amount) {
        return "微信支付" + amount;
    }

    @Override
    public String getPayCode() {
        return "wechatPay";
    }
}

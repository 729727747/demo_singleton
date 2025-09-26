package com.example.demo_singleton;

import com.example.demo_singleton.pattern.strategy_factory.PayStrategyContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class DemoSingletonApplicationTests {

    @Autowired
    private PayStrategyContext payStrategyContext;

    @Test
    void test() {
        payStrategyContext.pay("wechatPay", new BigDecimal("100.00"));
    }



}

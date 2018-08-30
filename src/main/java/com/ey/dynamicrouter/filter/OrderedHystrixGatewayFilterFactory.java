package com.ey.dynamicrouter.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.HystrixGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.DispatcherHandler;

import java.util.List;

@Component
public class OrderedHystrixGatewayFilterFactory
        extends AbstractGatewayFilterFactory<HystrixGatewayFilterFactory.Config> {

    private HystrixGatewayFilterFactory hystrixGatewayFilterFactory;

    public OrderedHystrixGatewayFilterFactory(DispatcherHandler dispatcherHandler) {
        super(HystrixGatewayFilterFactory.Config.class);
        this.hystrixGatewayFilterFactory = new HystrixGatewayFilterFactory(dispatcherHandler);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return hystrixGatewayFilterFactory.shortcutFieldOrder();
    }

    @Override
    public GatewayFilter apply(HystrixGatewayFilterFactory.Config config) {
        return new OrderedGatewayFilter(
                hystrixGatewayFilterFactory.apply(config), OrderedGatewayFilter.LOWEST_PRECEDENCE - 1);
    }
}


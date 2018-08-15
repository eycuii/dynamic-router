package com.ey.dynamicrouter.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.HystrixGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.DispatcherHandler;

import java.util.List;
import java.util.function.Consumer;

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

    public GatewayFilter apply(String routeId, Consumer<HystrixGatewayFilterFactory.Config> consumer) {
        return hystrixGatewayFilterFactory.apply(routeId, consumer);
    }

    @Override
    public GatewayFilter apply(HystrixGatewayFilterFactory.Config config) {
        return new OrderedGatewayFilter(
                hystrixGatewayFilterFactory.apply(config), Integer.MAX_VALUE);
    }
}


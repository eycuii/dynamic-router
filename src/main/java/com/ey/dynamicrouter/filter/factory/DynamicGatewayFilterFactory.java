package com.ey.dynamicrouter.filter.factory;


import com.ey.dynamicrouter.entities.RouterConfig;
import com.ey.dynamicrouter.service.IRouterConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.AbstractChangeRequestUriGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DynamicGatewayFilterFactory
        extends AbstractChangeRequestUriGatewayFilterFactory<DynamicGatewayFilterFactory.Config> {

    @Value("${server.port}")
    private String port;

    private String errorUrl = "http://localhost:%s/error?message=%s";

    private IRouterConfigService routerConfigService;

    public DynamicGatewayFilterFactory(IRouterConfigService routerConfigService) {
        super(Config.class);
        this.routerConfigService = routerConfigService;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("regex");
    }

    @Override
    protected Optional<URI> determineRequestUri(ServerWebExchange exchange,
                                                Config config) {
        URI redirectUri;
        try {
            redirectUri = getRedirectUri(exchange, config);
        } catch (Exception e) {
            e.printStackTrace();
            redirectUri = getErrorUri(e);
        }
        System.out.println("redirectUrl::" + redirectUri);
        return Optional.of(redirectUri);
    }

    private URI getRedirectUri(ServerWebExchange exchange, Config config) throws Exception {
        ServerHttpRequest req = exchange.getRequest();
        MultiValueMap<String, String> params = req.getQueryParams();

        String requestCode = getParam(params, "requestCode");
        if (StringUtils.isEmpty(requestCode)) {
            throw new RuntimeException("requestCode不能为空");
        }
        RouterConfig routerConfig = routerConfigService.getByRequestCode(requestCode);
        if (routerConfig == null) {
            throw new RuntimeException("路由配置信息不存在");
        }

        return getRedirectUri(req, config, routerConfig);
    }

    private String getParam(MultiValueMap<String, String> params, String name) {
        List<String> list = params.get(name);
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return list.get(0);
    }

    private URI getRedirectUri(ServerHttpRequest request, Config config, RouterConfig routerConfig) throws Exception {
        URI redirectUri = (new URL(routerConfig.getRedirectUrl())).toURI();
        URI requestUri = request.getURI();
        String requestPath = requestUri.getRawPath().replaceFirst(config.getRegex(), "");
        boolean encoded = ServerWebExchangeUtils.containsEncodedParts(redirectUri);
        return UriComponentsBuilder.fromUri(redirectUri)
                .path(requestPath)
                .query(requestUri.getRawQuery())
                .build(encoded)
                .toUri();
    }

    private URI getErrorUri(Exception e) {
        URI uri = null;
        try {
            String message = URLEncoder.encode(e.getMessage(), "utf-8");
            String url = String.format(errorUrl, port, message);
            uri = (new URL(url)).toURI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return uri;
    }

    public static class Config {

        private String regex;

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }
    }
}

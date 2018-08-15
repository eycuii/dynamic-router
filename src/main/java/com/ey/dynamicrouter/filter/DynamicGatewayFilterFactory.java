package com.ey.dynamicrouter.filter;


import com.ey.dynamicrouter.entities.RouterConfig;
import com.ey.dynamicrouter.service.IRouterConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.AbstractChangeRequestUriGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

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
        String redirectUrl;
        try {
            redirectUrl = getRedirectUrl(exchange, config);
        } catch (Exception e) {
            e.printStackTrace();
            redirectUrl = getErrorUrl(e);
        }
        System.out.println("redirectUrl::" + redirectUrl);
        return Optional.ofNullable(redirectUrl).map((url) -> {
            try {
                return (new URL(url)).toURI();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private String getRedirectUrl(ServerWebExchange exchange, Config config) throws Exception {
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

        return getRedirectUrl(req, config, routerConfig);
    }

    private String getParam(MultiValueMap<String, String> params, String name) {
        List<String> list = params.get(name);
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return list.get(0);
    }

    private String getRedirectUrl(ServerHttpRequest req, Config config, RouterConfig routerConfig) {
        URI uri = req.getURI();
        String path = uri.getRawPath();
        String redirectPath = path.replaceFirst(config.getRegex(), "");
        return routerConfig.getRedirectUrl() + redirectPath + "?" + uri.getRawQuery();
    }

    private String getErrorUrl(Exception e) {
        String url = "";
        try {
            String message = URLEncoder.encode(e.getMessage(), "utf-8");
            url = String.format(errorUrl, port, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return url;
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

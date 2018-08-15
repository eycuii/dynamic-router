用spring-cloud-gateway实现动态路由。    

如，requestCode=CODE1，redirectUrl=https://www.baidu.com，如果访问http://localhost:8071/router/duty?requestCode=CODE1，会跳到https://www.baidu.com/duty?requestCode=CODE1

​        
使用的过滤器：

- DynamicGatewayFilterFactory：根据数据库里的配置信息做动态路由
- OrderedHystrixGatewayFilterFactory：引入Hystrix，并把该过滤器顺序放到最后。
​    

sql：

```mysql
CREATE TABLE t_router_config (
    ID int(11) NOT NULL AUTO_INCREMENT,
    REQUEST_CODE varchar(50) NOT NULL,
    REDIRECT_URL varchar(2000) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO t_router_config VALUES ('1', 'CODE1', 'https://www.baidu.com');
```



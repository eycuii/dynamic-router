package com.ey.dynamicrouter.service;

import com.ey.dynamicrouter.entities.RouterConfig;

public interface IRouterConfigService {

    RouterConfig getByRequestCode(String requestCode);
}

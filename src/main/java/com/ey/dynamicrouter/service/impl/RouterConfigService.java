package com.ey.dynamicrouter.service.impl;

import com.ey.dynamicrouter.dao.IRouterConfigDao;
import com.ey.dynamicrouter.entities.RouterConfig;
import com.ey.dynamicrouter.service.IRouterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouterConfigService implements IRouterConfigService {

    @Autowired
    private IRouterConfigDao targetDao;

    public RouterConfig getByRequestCode(String requestCode) {
        return targetDao.getByRequestCode(requestCode);
    }
}

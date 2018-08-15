package com.ey.dynamicrouter.dao;

import com.ey.dynamicrouter.entities.RouterConfig;

public interface IRouterConfigDao {

    RouterConfig getByRequestCode(String requestCode);
}

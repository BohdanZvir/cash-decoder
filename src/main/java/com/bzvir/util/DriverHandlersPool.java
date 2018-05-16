package com.bzvir.util;

import java.util.concurrent.Callable;

public interface DriverHandlersPool {

    void initPool(Callable<WebDriverHandler> initService);

    void cleanDrivers();

    void allocateDriver4User(Long userId);

    WebDriverHandler getDriverByUser(Long userId);

    void release(long userId);

    WebDriverHandler delete(Long userId);
}

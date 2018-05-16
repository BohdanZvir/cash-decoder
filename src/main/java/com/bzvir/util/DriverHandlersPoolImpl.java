package com.bzvir.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.nonNull;

@Slf4j
public class DriverHandlersPoolImpl implements DriverHandlersPool {

    private static final int POOL_SIZE = 1;

    private Map<Long, DriverLocker> driversByUser;
    private Queue<Future<WebDriverHandler>> poolDrivers;
    private Callable<WebDriverHandler> initService;

    public DriverHandlersPoolImpl() {
        driversByUser = new HashMap<>();
        poolDrivers = new LinkedBlockingQueue<>();
    }

    @Override
    public void initPool(final Callable<WebDriverHandler> initService) {
        this.initService = initService;
        log.info("init pool size before {}", poolDrivers.size());
        while (POOL_SIZE > poolDrivers.size()) {
            poolDrivers.add(executeInThread(initService));
        }
    }

    @Override
    public void cleanDrivers() {
        int size = poolDrivers.size();
        log.debug("Cleaning, pool size {}", size);
        if (size != 0 && nonNull(initService)) {
            List<Future<WebDriverHandler>> futures = new ArrayList<>(poolDrivers);
            poolDrivers.clear();
            initPool(initService);
            futures.forEach(this::killHandlerFuture);
        }
    }

    @SneakyThrows
    private void killHandlerFuture(Future<WebDriverHandler> f) {
        WebDriverHandler handler = f.get();
        if (Util.isDriverUp(handler)) {
            handler.closeDriver();
        }
    }

    @Override
    public void allocateDriver4User(Long userId) {
        try {
            if (!poolDrivers.isEmpty()) {
                Future<WebDriverHandler> poll = poolDrivers.poll();
                if (nonNull(poll)) {
                    driversByUser.put(userId, new DriverLocker(poll));
                }
            }
            initPool(initService);
        } catch (Exception e) {
            log.error("allocateDriver4User", e);
        }
    }

    @Override
    public WebDriverHandler getDriverByUser(Long userId) {
        WebDriverHandler handler = Optional.ofNullable(driversByUser.get(userId))
                .map(DriverLocker::get)
                .filter(WebDriverHandler::isUp)
                .orElseGet(() -> {
                    allocateDriver4User(userId);
                    return getDriverByUser(userId);
                });
        log.debug("User {}, handler {}", userId, handler.getSessionId());
        return handler;
    }

    @Override
    public void release(long userId) {
        Optional.ofNullable(driversByUser.get(userId)).ifPresent(DriverLocker::release);
    }

    @Override
    public WebDriverHandler delete(Long userId) {
        DriverLocker locker = driversByUser.remove(userId);
        if (nonNull(locker)) {
            return locker.get();
        }
        return null;
    }

    private static class DriverLocker {
        private ReentrantLock locker = new ReentrantLock();
        private Future<WebDriverHandler> handlerFuture;

        DriverLocker(Future<WebDriverHandler> handlerFuture) {
            this.handlerFuture = handlerFuture;
        }

        @SneakyThrows
        WebDriverHandler get() {
            lock();
            return handlerFuture.get();
        }

        void lock() {
            locker.lock();
        }

        void release() {
            locker.unlock();
        }
    }
}

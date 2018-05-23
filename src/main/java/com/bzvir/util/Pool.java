package com.bzvir.util;

import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Pool<T> {

    private static final int INIT_DELAY = 20;
    private ConcurrentLinkedQueue<T> pool;
    private ScheduledExecutorService executorService;

    /**
     * @param config left - minIdle,
     *               middle - maxIdle,
     *               right - intervalInSeconds
     */
    protected Pool(final Triple<Integer, Integer, Long> config, ScheduledExecutorService executorService) {
        //todo refactored as Builder
        final int minIdle = config.getLeft();
        final int maxIdle = config.getMiddle();
        initPool(minIdle);

        this.executorService = executorService;
        this.executorService.scheduleWithFixedDelay(() -> {
            System.out.println("== Execute pool check ==");
            cleanObjects();
            int size = pool.size();
            if (size < minIdle) {
                int numberToBeAdded = minIdle - size;
                for (int i = 0; i < numberToBeAdded; i++) {
                    pool.add(createObject());
                }
            } else if (size > maxIdle) {
                int sizeToBeRemoved = size - maxIdle;
                for (int i = 0; i < sizeToBeRemoved; i++) {
                    pool.poll();
                }
            }
        }, INIT_DELAY, config.getRight(), TimeUnit.SECONDS);

    }

    private void cleanObjects() {
        pool.stream().filter(this::checkToDestroy).forEach(this::destroyObject);
    }

    private void initPool(final int minIdle) {
        pool = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < minIdle; i++) {
            pool.add(createObject());
        }
    }

    public T borrowObject() {
        T object = pool.poll();
        if (object == null) {
            object = createObject();
        }

        return object;
    }

    public void returnObject(T object) {
        if (object == null) {
            return;
        }

        this.pool.offer(object);
    }

    @PreDestroy
    public void shutdown() {
        pool.forEach(this::destroyObject);
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    protected abstract boolean checkToDestroy(T t);

    protected abstract void destroyObject(T t);

    protected abstract T createObject();
}

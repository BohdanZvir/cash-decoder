package com.bzvir.util.load.restrict;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
//@Component
//@Scope(BeanDefinition.SCOPE_SINGLETON)
public class RequestHandler<T> {

    private AtomicBoolean isPostTurn;
    private AtomicInteger counter;
    private Queue<Pair<Integer, Callable<T>>> inputGet;
    private Queue<Pair<Integer, Callable<T>>> inputPost;
    private Map<Integer, Future<T>> output;

    public RequestHandler() {
        this.isPostTurn = new AtomicBoolean(true);
        this.counter = new AtomicInteger();
        this.inputGet = new LinkedBlockingQueue<>();
        this.inputPost = new LinkedBlockingQueue<>();
        this.output = new ConcurrentHashMap<>();
    }

    public boolean isAnyRequest() {
        return inputPost.isEmpty() || inputGet.isEmpty();
    }

    public int addRequest(final Callable<T> callable, boolean isItPost) {
        int responseId = counter.getAndIncrement();
        if (isItPost) {
            inputPost.add(Pair.of(responseId, callable));
        } else {
            inputGet.add(Pair.of(responseId, callable));
        }
        log.info("added {}", responseId);
        return responseId;
    }

    public Pair<Integer, Callable<T>> getRequest() {
        Pair<Integer, Callable<T>> pair;
        if (inputGet.isEmpty() || (!inputPost.isEmpty() && isPostTurn.get())) {
            pair = inputPost.poll();
            isPostTurn.set(false);
            log.info("return higher");
            return pair;
        }
        else if (!inputGet.isEmpty()) {
            pair = inputGet.poll();
            isPostTurn.set(true);
            log.info("return regular");
            return pair;
        }
        return null;
    }

    public void saveResult(Integer key, Future<T> future) {
        output.put(key, future);
        log.info("Saved result for {}", key);
    }

    public Future<T> getResponse(@NonNull Integer key) {
        Future<T> response = output.remove(key);
        log.info("return response for {}", key);
        return response;
    }
}

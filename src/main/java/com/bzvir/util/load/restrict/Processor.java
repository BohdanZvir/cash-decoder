package com.bzvir.util.load.restrict;

import com.bzvir.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bzvir.util.Util.executeInThread;
import static java.util.Objects.nonNull;

//@Component
@Slf4j
public class Processor<T> {

    private AtomicBoolean invoked;

//    @Autowired
    private RequestHandler<T> requestHandler;

    public Processor() {
        invoked = new AtomicBoolean(false);
    }

    public void invoke() {
        if (!invoked.get()) {
            startProcessing();
        }
    }

    private void startProcessing() {
        this.invoked.set(true);
        while (requestHandler.isAnyRequest()) {
            Pair<Integer, Callable<T>> pair = requestHandler.getRequest();
            if (nonNull(pair)) {
                Callable<T> call = pair.getValue();
                Integer key = pair.getKey();
                if (nonNull(call) && nonNull(key)) {
                    requestHandler.saveResult(key, executeInThread(call));
                    log.info("Processed for {}", key);
                } else {
                    log.info("Can't process");
                }
            } else {
                log.info("No requests to process");
            }
            Util.wait(10, TimeUnit.SECONDS);
        }
        this.invoked.set(false);
    }
}

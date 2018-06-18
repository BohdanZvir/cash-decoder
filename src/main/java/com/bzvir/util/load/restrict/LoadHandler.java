package com.bzvir.util.load.restrict;

import com.bzvir.util.Util;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

//@Component
@Slf4j
public class LoadHandler {

//    @Autowired
    private RequestHandler<String> requestHandler;
//    @Autowired
    private Processor<String> processor;

    public int addRequest(Callable<String> request, boolean higher){
        int key = requestHandler.addRequest(request, higher);
        log.info("added request {}", key);
        processor.invoke();
        return key;
    }

    @SneakyThrows
    public String getResult(Integer key) {
        for (int i = 0; i < 10; i++) {
            Util.wait(5, TimeUnit.SECONDS);
            Future<String> response = requestHandler.getResponse(key);
            if (nonNull(response)) {
                return response.get();
            }
        }
        return null;
    }

}

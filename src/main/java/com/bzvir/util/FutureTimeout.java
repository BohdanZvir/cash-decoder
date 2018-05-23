package com.bzvir.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FutureTimeout {
    static class MyCallable implements Callable<String> {
        private long waitTime;
        MyCallable(int timeInMillis){
            this.waitTime=timeInMillis;
        }
        @Override
        public String call() throws Exception {
            Thread.sleep(waitTime);
            return Thread.currentThread().getName();
        }
    }

    public static void main(String[] args) throws Exception {
        MyCallable callable1 = new MyCallable(500);
        MyCallable callable2 = new MyCallable(1000);
        FutureTask<String> futureTask1 = new FutureTask<String>(callable1);
        FutureTask<String> futureTask2 = new FutureTask<String>(callable2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(futureTask2);
        executor.execute(futureTask1);

        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);
        while (true) {
            try {
                boolean done1 = futureTask1.isDone();
                boolean done2 = futureTask2.isDone();

                if(done1 && done2){
                    System.out.println("--Done");
                    executor.shutdown();
                    return;
                }

                System.out.printf("1: %s\t2: %s%n", done1, done2);

                String x = futureTask1.get(100L, TimeUnit.MILLISECONDS);

                System.out.printf("1:task %d output=%s%n", counter1.getAndIncrement(), x);

                String s = futureTask2.get(200L, TimeUnit.MILLISECONDS);
                System.out.printf("2:task %d output=%s%n", counter2.getAndIncrement(), s);

                Thread.sleep(100);
            } catch(TimeoutException e) {
                System.out.println("timeout");
            }
        }
    }
}
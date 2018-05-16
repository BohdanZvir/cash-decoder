package com.bzvir.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static final Boolean DRIVER_HEADLESS_OPTION = Boolean.TRUE;
    public static final BigDecimal FIFTY = new BigDecimal(50);
    public static final BigDecimal HUNDRED = new BigDecimal(100);
    public static final BigDecimal TWO_HUNDREDS = new BigDecimal(200);
    public static final String EMPTY_STRING = "";

    @SneakyThrows
    public static void wait(int duration, TimeUnit timeUnit) {
        Thread.sleep(timeUnit.toMillis(duration));
    }

    public static <T> Future<T> executeInThread(Callable<T> callable) {
        return Executors.newFixedThreadPool(1).submit(callable);
    }

    public static boolean isDriverUp(WebDriverHandler driverHandler) {
        return nonNull(driverHandler) && driverHandler.isUp();
    }

    public static String getEnvVar(String name, String defaultValue) {
        return Optional.ofNullable(System.getenv(name)).orElse(defaultValue);
    }

}

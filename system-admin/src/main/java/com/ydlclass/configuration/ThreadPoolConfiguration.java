package com.ydlclass.configuration;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;


@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public ExecutorService executorService() {

        // 线程池的 7 个参数
//        ThreadPoolExecutor(
//            int corePoolSize,
//            int maximumPoolSize,
//            long keepAliveTime,
//            TimeUnit unit,
//            BlockingQueue<Runnable> workQueue,
//            ThreadFactory threadFactory,
//            RejectedExecutionHandler handler)

        return new ThreadPoolExecutor(
                10,
                20,
                120,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100),
                new BasicThreadFactory.Builder().namingPattern("ydlclasslog-%d").daemon(true).build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}

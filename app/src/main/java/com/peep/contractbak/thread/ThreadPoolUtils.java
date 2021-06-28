package com.peep.contractbak.thread;

import com.luoxudong.app.threadpool.ThreadPoolHelp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程池控制实现类
 * */
public class ThreadPoolUtils {

    private static ExecutorService executorService;
    private static ScheduledExecutorService scheduleService;

    private ThreadPoolUtils(){};
    /**
     * 线程池初始化
     * @param exePoolSize 线程池大小
     * @param schPoolSize 定时线程池大小
     * */
    public static void init(int exePoolSize,int schPoolSize){
        if(exePoolSize <= 0){
            exePoolSize = 1;
        }
        if(schPoolSize <= 0){
            schPoolSize = 1;
        }
        //创建定时线程池（一般用于定时延迟任务）
        executorService = ThreadPoolHelp.Builder
                .fixed(exePoolSize)
                .name("exePool")
                .builder();
        //创建定时线程池（一般用于定时延迟任务）
        scheduleService = ThreadPoolHelp.Builder
                .schedule(schPoolSize)
                .name("schPool")
                .scheduleBuilder();
    }

    /**
     * 线程池任务函数
     * */
    public static void execute(Runnable runnable){
        executorService.execute(runnable);
    }
    /**
     * 延迟线程池任务函数
     * */
    public static void schedule(Runnable runnable,long delayTime){
        scheduleService.schedule(runnable,delayTime,TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟线程池任务函数
     * */
    public static void schedule(Runnable runnable){
        scheduleService.schedule(runnable,0,TimeUnit.MILLISECONDS);
    }
}

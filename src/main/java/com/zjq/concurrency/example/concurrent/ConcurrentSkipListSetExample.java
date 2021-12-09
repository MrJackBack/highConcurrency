package com.zjq.concurrency.example.concurrent;

import com.zjq.concurrency.anno.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.*;

/**
 * @author zjq
 * @date 2021/12/8 20:38
 * <p>title: CopyOnWriteArraySet</p>
 * <p>description:</p>
 */
@Slf4j
@ThreadSafe
public class ConcurrentSkipListSetExample {

    /**
     * 请求总数
     */
    public static int clientTotal = 5000;

    /**
     * 同时并发执行的线程数
     */
    public static int threadTotal = 200;

    public static Set<Integer> set = new ConcurrentSkipListSet<>();

    public static void main(String[] args) throws Exception {
        //创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //信号量（并发线程数）
        final Semaphore semaphore = new Semaphore(threadTotal);
        //计数器  （把请求计数）
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal ; i++) {
            final int count = i;
            executorService.execute(() -> {
                try {
                    //信号量  判断进程是否执行
                    semaphore.acquire();
                    add(count);
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                //计数器减1
                countDownLatch.countDown();
            });
        }
        //当所有请求结束
        countDownLatch.await();
        executorService.shutdown();
        log.info("size:{}", set.size());
    }

    private static void add(int i) {
        set.add(i);
    }

}

package com.mytest.juctest.Cllable;

import java.util.concurrent.*;

public class CallTest{

    static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CallableTest callableTest = new CallableTest();

        FutureTask<Integer> futureTask = new FutureTask<>(callableTest);
        Thread thread = new Thread(futureTask);
        thread.start();
        //Future<Integer> futureTask = executorService.submit(callableTest);

        Integer integer = futureTask.get();
        System.out.println("计算结果： "+ integer);

        System.out.println("main结束");
    }
}

class CallableTest implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        Thread.sleep(1000);
        int num = 0;
        for (int i = 1; i <= 100; i++) {
            num += i;
        }

        return num;
    }
}


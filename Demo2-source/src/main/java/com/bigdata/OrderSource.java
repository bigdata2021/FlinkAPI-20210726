package com.bigdata;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

public class OrderSource extends RichParallelSourceFunction<Order> {

    private boolean flag = true;
    private Random r = new Random();

    @Override
    public void run(SourceContext<Order> sourceContext) throws Exception {

        while (flag){
            String orderId = UUID.randomUUID().toString();
            int userId = r.nextInt(3);
            int money = r.nextInt(103);
            long createTime = System.currentTimeMillis();
            Timestamp time = new Timestamp(System.currentTimeMillis());

            Order order = new Order(orderId, userId, money, createTime, time);
            sourceContext.collect(order);

            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }
}

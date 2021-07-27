package com.bigdata;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CustomSource {
    public static void main(String[] args) throws Exception {
        //TODO 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 2.Source
        DataStream<Order> orderSource = env.addSource(new OrderSource()).setParallelism(3);

        //TODO 3.transaction

        //TODO 4.Sink
        orderSource.print();

        //TODO 5.execute
        env.execute();
    }
}

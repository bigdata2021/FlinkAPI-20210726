package com.bigdata;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SocketSource {
    public static void main(String[] args) throws Exception {
        //TODO 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 2.Source
        DataStream<String> socketSource = env.socketTextStream("node1", 8888);

        //TODO 3.transaction

        //TODO 4.Sink
        socketSource.print();

        //TODO 5.execute
        env.execute();
    }
}

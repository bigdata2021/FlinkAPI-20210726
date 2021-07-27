package com.bigdata;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

public class collectionSource {
    public static void main(String[] args) throws Exception {
        //TODO 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 2.Source
        //env.fromElements(可变参数);
        DataStream<String> elemSource = env.fromElements("hadoop", "spark", "flink");

        //env.fromColletion(各种集合);
        DataStream<String> listSource = env.fromCollection(Arrays.asList("hadoop", "spark", "flink"));

        //env.generateSequence(开始,结束); 已过时
        DataStream<Long> seqSource1 = env.generateSequence(1, 10);

        //env.fromSequence(开始,结束);
        DataStream<Long> seqSource2 = env.fromSequence(1, 10);

        //TODO 3.transaction

        //TODO 4.Sink
        elemSource.print();
        listSource.print();
        seqSource1.print();
        seqSource2.print();

        //TODO 5.execute
        env.execute();
    }
}







































package com.bigdata;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class OptionDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> wordSource = env.addSource(new WordSource()).setParallelism(1);

        DataStream<String> wordDS = wordSource.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String line, Collector<String> collector) throws Exception {
                String[] words = line.split(",");
                for (String word : words) {
                    collector.collect(word);
                }
            }
        });

        DataStream<String> filterDS = wordDS.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String word) throws Exception {
                return !word.equals("heihei");
            }
        });

        DataStream<Tuple2<String, Integer>> tupleDS = filterDS.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String word) throws Exception {
                return Tuple2.of(word, 1);
            }
        });

        //KeyedStream<Tuple2<String, Integer>, Tuple> groupedDS = tupleDS.keyBy(0);   已过时
        KeyedStream<Tuple2<String, Integer>, String> groupedDS = tupleDS.keyBy(t -> t.f0);

        DataStream<Tuple2<String, Integer>> aggDS1 = groupedDS.sum(1);
        aggDS1.print();

        DataStream<Tuple2<String, Integer>> aggDS2 = groupedDS.reduce(new ReduceFunction<Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> t1, Tuple2<String, Integer> t2) throws Exception {
                return Tuple2.of(t1.f0, t1.f1 + t2.f1);
            }
        });
        aggDS2.print();

        env.execute();
    }
}



























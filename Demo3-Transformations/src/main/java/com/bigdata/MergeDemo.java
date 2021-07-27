package com.bigdata;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class MergeDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> source1 = env.fromElements("hadoop", "spark", "flink");
        DataStream<String> source2 = env.fromElements("hadoop", "spark", "flink");
        DataStream<Long> source3 = env.fromElements(1L, 2L, 3L);

        DataStream<String> unionDS = source1.union(source2);
        unionDS.print();

        ConnectedStreams<String, Long> connectDS = source2.connect(source3);
        DataStream<String> mapDS = connectDS.map(new CoMapFunction<String, Long, String>() {
            @Override
            public String map1(String value) throws Exception {
                return "String->String:" + value;
            }

            @Override
            public String map2(Long value) throws Exception {
                return "Long->String:" + value.toString();
            }
        });
        mapDS.print();

        env.execute();
    }
}






























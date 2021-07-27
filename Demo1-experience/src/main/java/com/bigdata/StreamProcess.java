package com.bigdata;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class StreamProcess {
    public static void main(String[] args) throws Exception {

        //TODO 1. env (获得一个Flink程序执行的环境（构建environment对象）)
        // 注意：新版本的流批统一API,既支持流处理也支持批处理
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 指定对接数据源类型：
        //env.setRuntimeMode(RuntimeExecutionMode.BATCH);         // 批
        //env.setRuntimeMode(RuntimeExecutionMode.STREAMING);     // 流
        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);     // 自动识别（默认）
        // 设置并行度：
        env.setParallelism(1);

        //TODO 2. source (加载/创建初始化数据（构建Source对象）)
        DataStream<String> source = env.fromElements("hadoop spark flink", "hadoop spark flink");
        
        //TODO 3. transform (指定操作数据的transaction算子)
        DataStream<String> wordDS = source.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String line, Collector<String> collector) throws Exception {
                String[] words = line.split(" ");
                for (String word : words) {
                    collector.collect(word);
                }
            }
        });

        DataStream<Tuple2<String, Integer>> kvDS = wordDS.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String word) throws Exception {
                return Tuple2.of(word, 1);
            }
        });

        KeyedStream<Tuple2<String, Integer>, String> groupedDS = kvDS.keyBy(t -> t.f0);

        DataStream<Tuple2<String, Integer>> aggDS = groupedDS.sum(1);

        //TODO 4. sink (指定结果数据输出目的地)
        aggDS.print();

        //TODO 5. execute (调用execute()触发执行程序；注意：Flink程序是延迟计算的，只有最后调用execute()方法的时候才会真正触发执行程序。但批处理无需指定)
        env.execute();
    }
}

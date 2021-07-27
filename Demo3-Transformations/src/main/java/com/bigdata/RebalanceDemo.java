package com.bigdata;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RebalanceDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

        DataStream<Long> seqDS = env.fromSequence(0, 100);

        //下面的操作相当于将数据随机分配一下,有可能出现数据倾斜
        DataStream<Long> filteredDS = seqDS.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long value) throws Exception {
                return value > 1;
            }
        });

        DataStream<Tuple2<Integer, Integer>> indexDS = filteredDS.map(new RichMapFunction<Long, Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> map(Long value) throws Exception {

                // 获取分区编号/子任务编号
                RuntimeContext runtimeContext = this.getRuntimeContext();
                int index = runtimeContext.getIndexOfThisSubtask();

                return Tuple2.of(index, 1);
            }
        });

        KeyedStream<Tuple2<Integer, Integer>, Integer> groupedDS = indexDS.keyBy(t -> t.f0);
        DataStream<Tuple2<Integer, Integer>> aggDS1 = groupedDS.sum(1);
        //aggDS1.print("aggDS1: ");

        // 在输出前进行了rebalance重分区平衡,解决了数据倾斜
        DataStream<Long> rebalanceDS = filteredDS.rebalance();
        SingleOutputStreamOperator<Tuple2<Integer, Integer>> mapDS = rebalanceDS.map(new RichMapFunction<Long, Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> map(Long value) throws Exception {

                int index = this.getRuntimeContext().getIndexOfThisSubtask();
                return Tuple2.of(index, 1);
            }
        });
        DataStream<Tuple2<Integer, Integer>> aggDS2 = mapDS.keyBy(t -> t.f0).sum(1);
        aggDS2.print("aggDS2: ");

        env.execute();
    }
}

package com.bigdata;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class SplitDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<Integer> elemSource = env.fromElements(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        //1. 定义两个输出标签:
        OutputTag<Integer> tag_even = new OutputTag<>("偶数", TypeInformation.of(Integer.class));
        OutputTag<Integer> tag_odd = new OutputTag<Integer>("奇数") { };

        //2. 侧输出流数据处理：
        SingleOutputStreamOperator<Integer> tagResult = elemSource.process(new ProcessFunction<Integer, Integer>() {
            @Override
            public void processElement(Integer i, Context context, Collector<Integer> collector) throws Exception {
                if (i % 2 == 0){
                    context.output(tag_even , i);
                }else {
                    context.output(tag_odd , i);
                }
            }
        });

        //3. 取出标记好的数据
        DataStream<Integer> evenResult = tagResult.getSideOutput(tag_even);
        evenResult.print("偶数：");
        DataStream<Integer> oddResult = tagResult.getSideOutput(tag_odd);
        oddResult.print("奇数：");

        env.execute();
    }
}















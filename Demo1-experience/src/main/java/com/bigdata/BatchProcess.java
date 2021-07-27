package com.bigdata;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class BatchProcess {
    public static void main(String[] args) throws Exception {

        //TODO 1. env (获得一个Flink程序执行的环境（构建environment对象）)
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //TODO 2. source (加载/创建初始化数据（构建Source对象）)
        //注意：读取文件数据：Flink1.12之前返回类型：DataSet<String> ， 之后则统一返回DataSource<String>
        DataSource<String> source = env.readTextFile("testData/wc.txt");

        //TODO 3. transform (指定操作数据的transaction算子)
        FlatMapOperator<String, Tuple2<String, Integer>> flatMap = source.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String line, Collector<Tuple2<String, Integer>> collector) throws Exception {
                String[] words = line.split(" ");
                for (String word : words) {
                    collector.collect(Tuple2.of(word, 1));
                }
            }
        });

        // 分组：0表示按照tuple中的索引为0的字段,也就是key(单词)进行分组
        UnsortedGrouping<Tuple2<String, Integer>> grouped = flatMap.groupBy(0);

        // 累加：1表示按照tuple中的索引为1的字段也就是按照数量进行聚合累加!
        AggregateOperator<Tuple2<String, Integer>> agg = grouped.sum(1);

        //TODO 4. sink (指定结果数据输出目的地)
        agg.print();

        //TODO 5. execute (调用execute()触发执行程序；注意：Flink程序是延迟计算的，只有最后调用execute()方法的时候才会真正触发执行程序。但批处理无需指定)
    }
}





















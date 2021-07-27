package com.bigdata;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class fileSource {
    public static void main(String[] args) throws Exception {
        //TODO 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 2.Source
        DataStreamSource<String> localFileSource = env.readTextFile("testData/wc.txt");
        DataStreamSource<String> localDirSource = env.readTextFile("testData");

        DataStreamSource<String> hdfsFileSource = env.readTextFile("hdfs://HdfsCluster/input/wc.txt");

        //TODO 3.transaction

        //TODO 4.Sink
        localFileSource.print();
        localDirSource.print();
        hdfsFileSource.print();

        //TODO 5.execute
        env.execute();
    }
}

package com.bigdata;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WordSource extends RichParallelSourceFunction<String> {

    private boolean flag = true;
    private Random r = new Random();
    private String[] words =  {"hadoop" , "heihei" , "spark" , "flink"};

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {

        while (flag){
            String word1 = words[r.nextInt(words.length)];
            String word2 = words[r.nextInt(words.length)];
            String word3 = words[r.nextInt(words.length)];

            sourceContext.collect(word1 +","+ word2 +","+ word3);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }
}

package com.geektcp.example.flink.socket;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * @author geektcp on 2023/3/28 13:57.
 */
public class SocketWordCount {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> text = env.socketTextStream("127.0.0.1", 9999, "\n");

        SingleOutputStreamOperator<WordWithCount> singleOutputStreamOperator = text.flatMap(
                new FlatMapFunction<String, WordWithCount>() {
                    public void flatMap(String s, Collector<WordWithCount> collector) {
                        for (String word : s.split("\\s")) {
                            collector.collect(new WordWithCount(word, 1L));
                        }
                    }
                }
        );

        DataStream<WordWithCount> windowCounts = singleOutputStreamOperator
                .keyBy("word")
                .timeWindow(Time.seconds(5), Time.seconds(1))
                .reduce(new ReduceFunction<WordWithCount>() {
                    public WordWithCount reduce(WordWithCount a, WordWithCount b) {
                        return new WordWithCount(a.word, a.count + b.count);
                    }
                });

        windowCounts.print().setParallelism(1);
        env.execute("flink job title");
    }

    public static class WordWithCount {
        public String word;
        public long count;

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        public String toString() {
            return word + " : " + count;
        }
    }

}
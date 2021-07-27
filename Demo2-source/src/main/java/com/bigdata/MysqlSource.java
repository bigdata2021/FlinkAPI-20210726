package com.bigdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

/**
 * @author 石永鑫大数据工作室
 * @className: MysqlSource
 * @description: TODO
 * @date: 2021/7/26 17:15
 * @version: 1.0
 */
public class MysqlSource {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Student {
        private Integer id;
        private String name;
        private Integer age;
    }

    public static class MySQLSource extends RichParallelSourceFunction<Student> {

        private boolean flag = true;

        private Connection conn = null;
        private PreparedStatement pstmt = null;
        private ResultSet rs = null;

        private String url = "jdbc:mysql://node1:3306/test?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false";
        private String username = "root";
        private String passwd = "123456";

        // 资源申请：
        @Override
        public void open(Configuration conf) throws Exception {

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, passwd);
            String querySql = "select * from t_student";
            pstmt = conn.prepareStatement(querySql);
        }

        @Override
        public void run(SourceContext<Student> sourceContext) throws Exception {

            while (flag) {
                rs = pstmt.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");

                    sourceContext.collect(new Student(id , name , age));
                }
                TimeUnit.SECONDS.sleep(3);
            }
        }

        @Override
        public void cancel() {
            flag = false;
        }

        // 资源释放：
        @Override
        public void close() throws Exception {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    public static void main(String[] args) throws Exception {
        //TODO 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 2.Source
        DataStream<Student> mysqlSource = env.addSource(new MySQLSource()).setParallelism(1);

        //TODO 3.transaction

        //TODO 4.Sink
        mysqlSource.print();

        //TODO 5.execute
        env.execute();
    }
}

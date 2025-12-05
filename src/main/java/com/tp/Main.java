package com.tp;

import org.apache.spark.internal.config.R;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws Exception {
        SparkSession ss=SparkSession.builder()
                .appName("Structured streaming App")
                .getOrCreate();
        ss.sparkContext().setLogLevel("WARN");
        StructType schema = new StructType(new StructField[]{
                new StructField("order_id", DataTypes.LongType, false, Metadata.empty()),
                new StructField("client_id", DataTypes.StringType, false, Metadata.empty()), // CORRIGÉ: Long -> String
                new StructField("client_name", DataTypes.StringType, false, Metadata.empty()),
                new StructField("product", DataTypes.StringType, false, Metadata.empty()),
                new StructField("quantity", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("price", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("order_date", DataTypes.StringType, false, Metadata.empty()),
                new StructField("status", DataTypes.StringType, false, Metadata.empty()),
                new StructField("total", DataTypes.DoubleType, false, Metadata.empty())
        });

        Dataset<Row> inputDF = ss.readStream().schema(schema).option("header",true).csv("hdfs://namenode:8020/data");
        inputDF.show();
        Dataset<Row> outputDF = inputDF.groupBy("order_id").sum("total");
        StreamingQuery query= outputDF.writeStream()
                .format("console")
                .outputMode(OutputMode.Complete())
                .start();
        query.awaitTermination();

    }
}
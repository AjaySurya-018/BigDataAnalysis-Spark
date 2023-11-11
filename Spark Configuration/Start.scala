package com.sparkProjects
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

  ///   Initializes and configures a Spark application

object Start {

  val conf = new SparkConf().setMaster("local").setAppName("StudentExamPerformance")
  val context = new SparkContext(conf)

  val data = context.textFile("/home/ajay/BigData/StudentsPerformance.csv")

  // The RDD[String] returned by the getData() method represents a distributed collection of strings in Apache Spark.
  def getData(): RDD[String] = data

  /** By representing the data as an RDD of strings,
   *  Spark enables distributed processing and provides
   *  various operations and transformations that can be applied to the RDD. */
}

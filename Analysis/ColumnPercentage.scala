package com.sparkProjects

import org.apache.spark.rdd.RDD

object ColumnPercentage {
  var rdd: RDD[Member] = null

  def calculateColumnPercentage(): Unit = {
    val totalCount = rdd.count()

    val genderPercentage = calculatePercentage(rdd.map(_.gender), totalCount)
    val ethnicityPercentage = calculatePercentage(rdd.map(_.ethnicity), totalCount)
    val educationPercentage = calculatePercentage(rdd.map(_.education), totalCount)
    val lunchPercentage = calculatePercentage(rdd.map(_.lunch), totalCount)
    val preparationCoursePercentage = calculatePercentage(rdd.map(_.preparationCourse), totalCount)

    println("Column Percentage: ")
    println(s"Gender: $genderPercentage")
    println(s"Ethnicity: $ethnicityPercentage")
    println(s"Education: $educationPercentage")
    println(s"Lunch: $lunchPercentage")
    println(s"Preparation Course: $preparationCoursePercentage")
  }

  private def calculatePercentage(column: RDD[String], totalCount: Long): collection.Map[String, Double] = {
    val counts = column.countByValue()
    val percentage = counts.mapValues(count => (count.toDouble / totalCount) * 100)
    percentage
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd

  def getRdd(): RDD[Member] = rdd
}

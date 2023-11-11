package com.sparkProjects

import org.apache.spark.rdd.RDD

object HighlyEducatedParents {
  var rdd: RDD[Member] = null

  def findGroupWithMostEducatedParents(): Unit = {
    val groupWithHighEducation = getRdd()
      .map(student => (student.ethnicity, student.education))
      .groupBy(_._1)
      .mapValues(_.map(_._2))
      .mapValues(_.count(_.toLowerCase == "master's degree"))

    val maxEducationCount = groupWithHighEducation.values.max()
    val groupsWithHighestEducation = groupWithHighEducation.filter(_._2 == maxEducationCount).keys

    println("Groups with the highest educated parents:")
    groupsWithHighestEducation.foreach(group => println(group))
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd

  def getRdd(): RDD[Member] = rdd
}

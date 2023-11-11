package com.sparkProjects

import org.apache.spark.rdd.RDD

object AllStudentFilter {
  val students = DataMaping.getDataMap()
  def getStudents():RDD[Member]=students

}

package com.sparkProjects

import org.apache.spark.rdd.RDD

object BestPerformanceStudentsFilter {

  val performanceLimit = 95
  val bestPerformanceStudents = DataMaping.getDataMap().filter(f => {
    f.mathScore > performanceLimit && f.writingScore > performanceLimit && f.readingScore > performanceLimit
  })

  def getBestPerformanceStudents():RDD[Member]=bestPerformanceStudents
}

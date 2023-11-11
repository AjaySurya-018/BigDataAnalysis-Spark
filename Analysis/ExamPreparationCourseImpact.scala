package com.sparkProjects
import org.apache.spark.rdd.RDD

object ExamPreparationCourseImpact {
  var rdd: RDD[Member] = null

  def analyzeCourseImpact(): Unit = {
    val withPreparationCourse = getRdd().filter(_.preparationCourse == "completed")
    val withoutPreparationCourse = getRdd().filter(_.preparationCourse == "none")

    // DISPLAYING TOTAL NUMBER OF STUDENTS
    println(s"Total number of students with preparation course: ${withPreparationCourse.count()}")
    println(s"Total number of students without preparation course: ${withoutPreparationCourse.count()}")

    val countByEthnicityWithCourse = countStudentsByEthnicity(withPreparationCourse)
    val countByEthnicityWithoutCourse = countStudentsByEthnicity(withoutPreparationCourse)

    // DISPLAYING THE STUDENT COUNT WHO TOOK COURSE FROM EACH ETHNIC GROUP
    println("Number of Students by Ethnicity with Exam Preparation Course:")
    printStudentCounts(countByEthnicityWithCourse)
    println()

    println("Number of Students by Ethnicity without Exam Preparation Course:")
    printStudentCounts(countByEthnicityWithoutCourse)

    // DISPLAYING AVERAGE SCORES OF EACH ETHNIC GROUP WHO DID AND DIDN'T TAKE

    println(s"Average Scores of Students who took Preparation Course by Ethnicity:")
    EthnicityPerformance.setRdd(withPreparationCourse)
    EthnicityPerformance.analyzeEthnicityPerformance()

    println(s"Average Scores of Students who DIDN'T take Preparation Course by Ethnicity:")
    EthnicityPerformance.setRdd(withPreparationCourse)
    EthnicityPerformance.analyzeEthnicityPerformance()

    // DISPLAYING THE AVG SCORES FOR ALL GROUP TYPES
    val avgWithCourse = calculateAverageScore(withPreparationCourse)
    val avgWithoutCourse = calculateAverageScore(withoutPreparationCourse)

    println("Average Scores with Exam Preparation Course:")
    printAverageScores(avgWithCourse)
    println()

    println("Average Scores without Exam Preparation Course:")
    printAverageScores(avgWithoutCourse)
    println()
  }

  def calculateAverageScore(data: RDD[Member]): (Double, Double, Double) = {
    val mathAvg = data.map(_.mathScore).mean()
    val readingAvg = data.map(_.readingScore).mean()
    val writingAvg = data.map(_.writingScore).mean()
    (mathAvg, readingAvg, writingAvg)
  }

  def printAverageScores(scores: (Double, Double, Double)): Unit = {
    val (mathAvg, readingAvg, writingAvg) = scores
    println(s"Math: $mathAvg")
    println(s"Reading: $readingAvg")
    println(s"Writing: $writingAvg")
  }

  def countStudentsByEthnicity(data: RDD[Member]): RDD[(String, Int)] = {
    data.map(member => (member.ethnicity, 1)).reduceByKey(_ + _)
  }

  def printStudentCounts(counts: RDD[(String, Int)]): Unit = {
    counts.foreach { case (ethnicity, count) =>
      println(s"Ethnicity: $ethnicity, Count: $count")
    }
  }

  def setRdd(rdd: RDD[Member]): Unit = this.rdd = rdd
  def getRdd(): RDD[Member] = rdd
}
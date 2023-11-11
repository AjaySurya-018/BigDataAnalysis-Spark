package com.sparkProjects

object Main {
  def main(args: Array[String]): Unit = {

    // Calculate column percentage
    ColumnPercentage.setRdd(AllStudentFilter.getStudents())
    ColumnPercentage.calculateColumnPercentage()

    // Best Performance students and its count
    println("Students with >95 in each section: "+BestPerformanceStudentsFilter.bestPerformanceStudents.count())
    BestPerformanceStudentsFilter.bestPerformanceStudents.foreach(f => {
      println(f.gender + "   " + f.ethnicity + " " + f.education + "     " + f.mathScore + " " + f.readingScore + " " + f.writingScore)
    })

    // Finding gender and ethnicity percent of best performance students
    GenderPercent.setRdd(BestPerformanceStudentsFilter.getBestPerformanceStudents())
    GenderPercent.getPercent()
    EthnicityPercent.setRdd(BestPerformanceStudentsFilter.getBestPerformanceStudents())
    EthnicityPercent.getPercent()

    // Finding overall percentage of students{scores._1}
    OverallPercentage.setRdd(BestPerformanceStudentsFilter.getBestPerformanceStudents())
    OverallPercentage.calculateOverallPercentage()

    // Finding class statistics in each section
    AverageMark.setRdd(AllStudentFilter.getStudents())
    AverageMark.calculateStatistics()

    // Analyzing the impact of exam preparation course
    ExamPreparationCourseImpact.setRdd(AllStudentFilter.getStudents())
    ExamPreparationCourseImpact.analyzeCourseImpact()

    // Analyzing ethnicity and  its impact on student performance
    EthnicityPerformance.setRdd(AllStudentFilter.getStudents())
    EthnicityPerformance.analyzeEthnicityPerformance()

    // Analyzing parents education level and its impact on student performance
    EducationPerformance.setRdd(AllStudentFilter.getStudents())
    EducationPerformance.analyzeEducationPerformance()

    // Group with most educated parents
    HighlyEducatedParents.setRdd(AllStudentFilter.getStudents())
    HighlyEducatedParents.findGroupWithMostEducatedParents()

    // Compare gender and lunch
    GenderLunchComparison.setRdd(AllStudentFilter.getStudents())
    GenderLunchComparison.compareGenderLunch()

    // Compare average marks by gender
    GenderComparison.setRdd(AllStudentFilter.getStudents())
    GenderComparison.compareAverageMarksByGender()


    /* GenderPercent.setRdd(AllStudentFilter.getStudents())
    GenderPercent.getPercent()
    EthnicityPercent.setRdd(AllStudentFilter.getStudents())
    EthnicityPercent.getPercent() */


  }
}

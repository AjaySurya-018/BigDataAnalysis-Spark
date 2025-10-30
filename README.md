# Student Academic Performance Analysis using Apache Spark

---
## 1. Problem Statement & Motivation
Understanding how contextual variables (nutrition proxy via lunch type, parental educational attainment, structured preparation) correlate with standardized performance aids:
* Targeted academic interventions
* Resource allocation (course offerings, tutoring, prep programs)
* Early warning systems for underperformance
* Equity analyses across demographic cohorts

---
## 2. Dataset Description
| Aspect | Detail |
|--------|--------|
| Instances | 1,001 student records (fully observed; no nulls) |
| Source | Local CSV (`StudentsPerformance.csv`) – structurally similar to common educational benchmark datasets |
| Format | Comma‑separated, 8 columns |
| Granularity | One row per student test outcome |
| Target Variables | None (current phase = descriptive analytics); potential future target: composite performance score |

### 2.1 Schema (Domain Model `Member`)
| Column | Type | Example | Description |
|--------|------|---------|-------------|
| gender | String | `female` | Student gender (binary in current data; extensible) |
| ethnicity | String | `group C` | Cohort grouping proxy (could encode social / geographic stratification) |
| education | String | `bachelor's degree` | Highest parental education (socio‑economic & cultural capital proxy) |
| lunch | String | `free/reduced` | Lunch assistance status (proxy for SES / nutritional support) |
| preparationCourse | String | `completed` / `none` | Participation in exam preparation program |
| mathScore | Int | `72` | Standardized math score (0–100 scale) |
| readingScore | Int | `90` | Standardized reading score |
| writingScore | Int | `88` | Standardized writing score |

---
## 3. Analytical Objectives & Module Mapping
| Objective | Spark Module / Object | Core Transformations |
|-----------|-----------------------|----------------------|
| Column distribution (%) | `ColumnPercentage` | `map`, `countByValue` |
| High performer segment (>95 all subjects) | `BestPerformanceStudentsFilter` | `filter` |
| Gender / Ethnicity proportion (subset) | `GenderPercent`, `EthnicityPercent` | `map`, `reduceByKey`, normalization |
| Overall individual percentage | `OverallPercentage` | per‑row aggregate + scaling |
| Subject statistics | `AverageMark` | `map`, custom std dev (variance via mean) |
| Exam prep impact | `ExamPreparationCourseImpact` | stratified filters + reuse of ethnicity performance module |
| Ethnicity performance means | `EthnicityPerformance` | keyed aggregation (`reduceByKey`) |
| Parental education impact | `EducationPerformance` | `groupByKey` (noting alt: `aggregateByKey` for scalability) |
| Highly educated parents cluster | `HighlyEducatedParents` | grouping + frequency count |
| Gender vs Lunch cross counts | `GenderLunchComparison` | composite key reduce |
| Average scores by gender | `GenderComparison` | filtered means |

---
## 4. Selected Findings (Illustrative Samples)
| Insight | Observation (Approximate) | Interpretation |
|---------|--------------------------|---------------|
| Exam Prep Effect | Students completing prep show higher mean across all subjects | Structured preparation correlates with multi‑subject uplift |
| Parental Education | Master's / Bachelor's strata tend to elevate averages | Home academic capital likely supports performance |
| High Performer Diversity | High performer subset spans multiple ethnicity groups but skewed toward groups C & D | Suggests partial equity yet uneven distribution |
| Lunch Status | `standard` lunch more prevalent among top performers | Socio‑economic nutrition proxy effect |
| Gender Mean Diff | Reading/Writing female edge; Math near parity | Aligns with typical literacy performance gaps |

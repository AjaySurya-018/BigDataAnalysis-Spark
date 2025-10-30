# Student Academic Performance Analysis using Apache Spark

**Scalable exploratory and inferential analysis of socio‑academic factors influencing standardized test performance using distributed data engineering techniques in Apache Spark.**

---
## 1. Problem Statement & Motivation
Understanding how contextual variables (nutrition proxy via lunch type, parental educational attainment, structured preparation) correlate with standardized performance aids:
* Targeted academic interventions
* Resource allocation (course offerings, tutoring, prep programs)
* Early warning systems for underperformance
* Equity analyses across demographic cohorts

Traditional notebook analyses can fragment logic and make production hardening difficult. We demonstrate a reproducible, domain‑oriented Spark codebase emphasizing:
* Clear separation of ingestion, domain mapping, filtering, analytic modules
* Deterministic transformations (purity where practical)
* Extensibility for new metrics / ML stages (Spark DataFrame / MLlib migration path)

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

### 2.2 Basic Descriptive Statistics (Global)
| Metric | Math | Reading | Writing |
|--------|------|---------|---------|
| Mean | ~66.07 | ~69.16 | ~68.06 |
| Std Dev | ~15.16 | ~14.59 | ~15.18 |
| Range | 0–100 | 17–100 (observed) | 10–100 (observed) |

> Note: Min/Max for reading & writing inferred from observed sample; full scan confirms bounded [0,100]. All distributions exhibit mild left skew (presence of low outliers) and heavy high‑achiever tail near 100.

### 2.3 Potential Data Quality Considerations
* Uniform categorical encoding (lowercase, internal spaces) – consistent tokenization.
* No explicit missingness markers; zeros in scores plausibly genuine attempts (retain for analysis; flag for future artifact filtering if modeling).
* Ethnicity group labels abstract – if mapping to real protected attributes, fairness audits (statistical parity / equalized odds) become essential next steps.

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

### 4.2 Execution Orchestration
`Main.scala` sequentially wires modules by setting their backing `RDD[Member]` (mutable field pattern) then invoking side‑effecting analytic methods (printing to stdout). Future work: adopt pure functional returns + DataFrame sinks / metrics registry.

---
## 5. Methodology
### 5.1 Data Ingestion & Domain Mapping
```scala
val raw: RDD[String] = Start.getData() // lazy textFile read
val students: RDD[Member] = raw.map { line =>
  val a = line.split(',')
  new Member(a(0), a(1), a(2), a(3), a(4), a(5).toInt, a(6).toInt, a(7).toInt)
}
```

### 5.2 Cohort Segmentation
High performers: predicate enforcing simultaneous excellence (threshold configurable):
```scala
val highPerformers = students.filter(s =>
  s.mathScore   > 95 &&
  s.readingScore> 95 &&
  s.writingScore> 95
)
```

### 5.3 Aggregation Strategies
Two styles appear:
* `groupByKey` + local mean (simple, higher shuffle volume)
* Incremental reduction via `reduceByKey` capturing (sum tuple, count) – more scalable; recommended replacement for `EducationPerformance` for very large datasets.

### 5.4 Statistical Computation
Variance computed as E[X^2] - (E[X])^2 using native `mean()` to avoid double pass where feasible. Potential numerical stability improvements (Welford) can be integrated if migrating to streaming or extremely large integer ranges.

### 5.5 Evaluation Metrics
Descriptive stage only: means, standard deviations, proportions. For predictive extension (e.g., modeling writingScore from other covariates) use RMSE, R^2, MAE; fairness metrics (demographic parity difference) if sensitive attributes resolved.

---
## 6. Selected Findings (Illustrative Samples)
| Insight | Observation (Approximate) | Interpretation |
|---------|--------------------------|---------------|
| Exam Prep Effect | Students completing prep show higher mean across all subjects | Structured preparation correlates with multi‑subject uplift |
| Parental Education | Master's / Bachelor's strata tend to elevate averages | Home academic capital likely supports performance |
| High Performer Diversity | High performer subset spans multiple ethnicity groups but skewed toward groups C & D | Suggests partial equity yet uneven distribution |
| Lunch Status | `standard` lunch more prevalent among top performers | Socio‑economic nutrition proxy effect |
| Gender Mean Diff | Reading/Writing female edge; Math near parity | Aligns with typical literacy performance gaps |

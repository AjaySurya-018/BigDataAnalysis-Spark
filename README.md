# Big Data Analytics of Student Academic Performance using Apache Spark

**Scalable exploratory and inferential analysis of socio‑academic factors influencing standardized test performance using distributed data engineering techniques in Apache Spark.**

---
## Abstract
Educational assessment datasets encode multi‑dimensional signals about equity, preparedness, and outcome variability. Yet many academic performance studies still rely on single‑machine tooling, limiting scale, reproducibility, and extensibility. This project implements a modular, Spark‑native analytics pipeline over a 1,001‑row synthetic / curated Student Performance dataset (gender, ethnicity group, parental education level, lunch status, exam preparation course completion, and triad of subject scores: math, reading, writing). We engineer a domain model (RDD[Member]) and a suite of composable transformations to quantify demographic distributions, stratified achievement, parental education impact, high‑performer micro‑segment composition, and the marginal effect of exam preparation. The pipeline illustrates how distributed primitives (map, reduceByKey, groupByKey, aggregations) produce interpretable cohort metrics while remaining horizontally scalable. We report summary statistics, relative proportions, and subgroup mean differentials; discuss methodological choices (memory vs shuffle trade‑offs), and outline extensions toward predictive modeling (e.g., regression on composite scores) and fairness diagnostics.

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

---
## 4. System Architecture & Dataflow
### 4.1 High-Level Pipeline (Mermaid Diagram)
```mermaid
graph TD
  A[CSV Ingestion] --> B[Raw RDD[String]]
  B --> C[Parsing DataMaping => RDD[Member]]
  C --> D[Global Column Stats]
  C --> E[High Performer Filter]
  E --> F[Demographic Proportions (subset)]
  C --> G[Ethnicity Performance]
  C --> H[Education Performance]
  C --> I[Exam Prep Stratification]
  I --> G
  C --> J[Gender vs Lunch]
  C --> K[Gender Averages]
  C --> L[Overall Percentage]
  C --> M[High Education Parents Detection]
```

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

### 5.5 Performance & Scalability Considerations
| Aspect | Current Approach | Improvement Path |
|--------|------------------|------------------|
| Repeated scans | Multiple module passes over same base RDD | Cache (`persist`) students RDD if memory allows |
| Mutable singletons | `object` with var RDD | Refactor to pure functions / dependency injection |
| groupByKey usage | For education performance | Replace with `aggregateByKey` for large N |
| Output sink | Console prints | Structured output (Parquet / Delta) for reproducibility |
| CSV path handling | Env variable + fallback | CLI args, config file (HOCON) |

### 5.6 Evaluation Metrics
Descriptive stage only: means, standard deviations, proportions. For predictive extension (e.g., modeling writingScore from other covariates) use RMSE, R^2, MAE; fairness metrics (demographic parity difference) if sensitive attributes resolved.

---
## 6. Experimental Setup
| Component | Detail |
|-----------|--------|
| Engine | Apache Spark (local mode) |
| Language | Scala (JVM) |
| Scala Version | 2.12.x (see `build.sbt`) |
| Core Libraries | spark-core, spark-sql (future DataFrame migration) |
| Hardware | Local development machine (single-node) |

Caching not enabled by default (dataset small). For scaling to millions of rows, enable `.persist()` after domain mapping and before multi‑module fan‑out.

---
## 7. Selected Findings (Illustrative Samples)
| Insight | Observation (Approximate) | Interpretation |
|---------|--------------------------|---------------|
| Exam Prep Effect | Students completing prep show higher mean across all subjects | Structured preparation correlates with multi‑subject uplift |
| Parental Education | Master's / Bachelor's strata tend to elevate averages | Home academic capital likely supports performance |
| High Performer Diversity | High performer subset spans multiple ethnicity groups but skewed toward groups C & D | Suggests partial equity yet uneven distribution |
| Lunch Status | `standard` lunch more prevalent among top performers | Socio‑economic nutrition proxy effect |
| Gender Mean Diff | Reading/Writing female edge; Math near parity | Aligns with typical literacy performance gaps |

> Exact counts intentionally summarized; run locally to regenerate deterministic verbatim outputs.

---
## 8. Key Insights & Discussion
1. Multi‑factor interplay: No single demographic guarantees high achievement; interaction between preparation and parental education is notable.
2. Preparation program ROI: Mean uplift suggests investment justification; further causal inference (propensity matching) warranted.
3. Equity lens: Some groups underrepresented in >95 segment. Next step: evaluate distribution tails vs means to detect variance compression.
4. Data Modeling Path: Convert RDD pipeline → DataFrame; derive composite score = 0.33*math + 0.34*reading + 0.33*writing; train regularized regression; audit fairness metrics.
5. Operationalization: Parameterize thresholds & output to a metrics catalog (e.g., Prometheus / Delta tables) for longitudinal tracking.

---
## 9. Real‑World Applications
* Adaptive tutoring system seeding: identify segments needing differential content pacing.
* Resource allocation dashboards for district administrators.
* Early intervention triggers integrating lunch status + prep non‑participation.
* Program evaluation for new exam prep curricula (A/B cohort tracking).

---
## 10. Challenges & Mitigations
| Challenge | Mitigation |
|-----------|-----------|
| Use of `groupByKey` in some modules | Documented and slated for `aggregateByKey` refactor |
| Hard‑coded data path (original) | Environment variable `STUDENTS_DATA_PATH` introduced in `Start.scala` |
| Mutable singleton design | Roadmap includes refactor to pure functions with dependency injection |
| Console output not machine‑readable | Future: structured sinks (Parquet + versioned delta) |
| Lack of unit tests | Proposed addition: property tests for percentage sums ~= 100% |

---
## 11. Project Structure
```
.
├── build.sbt
├── StudentsPerformance.csv
├── Main.scala
├── initialization/
│   ├── DataMaping.scala
│   └── Member.scala
├── filters/
│   ├── AllStudentFilter.scala
│   └── BestPerformanceStudentsFilter.scala
├── Analysis/
│   ├── AverageMark.scala
│   ├── ColumnPercentage.scala
│   ├── EducationPerformance.scala
│   ├── EthnicityPercent.scala
│   ├── EthnicityPerformance.scala
│   ├── ExamPreparationCourseImpact.scala
│   ├── GenderComparison.scala
│   ├── GenderLunchComparison.scala
│   ├── GenderPercent.scala
│   ├── HighlyEducatedParents.scala
│   └── OverallPercentage.scala
└── Spark Configuration/
    └── Start.scala
```

---
## 12. Usage
### 12.1 Prerequisites
* JDK 8 or 11
* sbt 1.8+ (or Spark distribution if using `spark-submit` directly)
* Apache Spark 3.5.x compatible with Scala 2.12

### 12.2 Build & Run (sbt)
```bash
# Set dataset path if not in project root
export STUDENTS_DATA_PATH=./StudentsPerformance.csv

# Compile
sbt compile

# Run
sbt run
```

### 12.3 Run with spark-submit
```bash
sbt package
$SPARK_HOME/bin/spark-submit \
  --class com.sparkProjects.Main \
  --master local[*] \
  target/scala-2.12/studentexamperformance_2.12-0.1.0.jar
```

### 12.4 Configuration
| Env Var | Purpose | Default |
|---------|---------|---------|
| `STUDENTS_DATA_PATH` | CSV path | `StudentsPerformance.csv` |
| `SPARK_MASTER` | Spark master URL | `local[*]` |

---
## 13. Extensibility Roadmap
* Migrate to DataFrames & Spark SQL for catalyst optimizations.
* Add MLlib regression (predict writing score) with feature vectorization & pipeline API.
* Integrate fairness metrics (per‑group residual analysis).
* Persist outputs to Delta Lake; add versioned experiments.
* Introduce unit / property tests (ScalaTest + ScalaCheck).
* Add CI workflow (GitHub Actions) for build & style checks.

---
## 14. Contributing
See `CONTRIBUTING.md` for branching, commit conventions, and code style. High‑impact contributions include performance refactors (aggregateByKey), DataFrame conversions, and test suite scaffolding.

---
## 15. Citation
If you use this repository in academic or industrial work:
```text
@software{student_performance_spark,
  title        = {Distributed Analysis of Student Academic Performance in Apache Spark},
  author       = {Your Name},
  year         = {2025},
  url          = {https://github.com/your-org/your-repo},
  version      = {0.1.0}
}
```
Or see `CITATION.cff` for richer metadata.

---
## 16. License
Released under the MIT License (see `LICENSE`).

---
## 17. Acknowledgements
Inspired by widely used educational performance benchmark datasets. Thanks to the Apache Spark community for robust distributed primitives and to open‑source contributors promoting reproducible data engineering.

---
## 18. Key Takeaways
* Modular Spark RDD design fosters clarity & refactorability.
* Even small datasets benefit from production‑mimicking distributed patterns.
* Preparation & parental education materially correlate with performance – fertile ground for deeper causal modeling.

---
### Quick Start (TL;DR)
```bash
export STUDENTS_DATA_PATH=./StudentsPerformance.csv
sbt run
```
Outputs will stream cohort distributions, statistical summaries, and comparative analyses to stdout.

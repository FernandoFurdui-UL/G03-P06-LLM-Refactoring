# Event B prediction (written before any Event B analysis)

**New pressure:** the affected code now has a public-API compatibility requirement.

**Refactoring revisited:** M4, ExtractMethod-3 (`readField(field, target)` helper in commons-lang `ReflectionDiffBuilder`, commit `4949adec`).

**Prediction:** The new public-API requirement will affect our verification workflow, not the M4 refactoring itself. The developers' `readField` extraction is private, so we expect no API change, but our M4 checks were unit tests only (`ReflectionDiffBuilderTest` 13/13, `DiffBuilderTest` 34/34) and can't detect API breaks. Prediction: japicmp will show 0 breaks for the developers' version, the LLM's whole-class rewrites (`event_a/raw/ExtractMethod-3/`) are the likely risk, and the Week 9 decision will be partly supported.

**Frozen at:** tag `EVENT_B_PLAN`.

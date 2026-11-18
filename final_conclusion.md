# Final conclusion (P06, Week 11)

## RQ1: reproduced
Naming the refactoring type in the prompt (P2) raised GPT-5.6 luna's rate of making the developers' refactoring from 3/12 (P1) to 7/12 (P2), a gap of 4 against the paper's 5 on the same 12 cases. The frozen rule (gap of 3 or more) was met, and the result fell within our prediction (`results/RQ1_result.md`). Which individual cases succeeded did not reproduce: we matched the paper on 10/12 cases for P1 and 7/12 for P2.

## Event A (transfer to 8 unseen cases): the conclusion held
P1 0/8, P2 6/8, gap 6 (`event_a/result.md`). Structural refactorings (Extract Class, and Extract Method under P2) stayed the weak spot, because the model picked a different, larger unit from the developers. Our prediction for P1 was wrong (predicted 1 to 3, got 0).

## Design decision (M4) and Event B
The refactoring we kept is ExtractMethod-3 (`readField` helper in commons-lang `ReflectionDiffBuilder`, commit `4949adec`). Unit tests passed on both versions (47/47: 13 + 34, `m4/`). Event B added a public-API compatibility requirement. japicmp found 0 changes for the developers' version and for both LLM rewrites. Two planted breaks (class made `final`, constructor parameter widened) passed all 47 tests but were caught by japicmp, and one crashed a pre-compiled client with `NoSuchMethodError` (`event_b/result.md`). The refactoring decision stands, but "tests pass" was not enough evidence under an API requirement. The workflow was adapted by adding an API diff check. The code was not changed.

## RQ2: pending
RQ2 (the P3-style prompt on the 9 RefactoringGuru examples, with compile/test checks and a 0-4 design rating) is still planned and is being run by a group member. This section is replaced with its result before the FINAL tag. [If it is not completed before FINAL, replace this with what was and was not done, and add the matching row to `deviations.md`.]

## Main limitation / threat
[RQ2 limitation: fill in once the RQ2 outcome is known.] All LLM results come from small samples (12 and 8 cases, one run per prompt, default website settings, no temperature control) with a different model from the paper's, and the scoring had moderate agreement (kappa 0.50 in M2; the Event A markings were not fully independent). One case moves a rate by 8 to 12.5 percentage points, and the RQ1 verdict rests on a gap one case above the threshold.

## How to re-run or audit
- **RQ1 and Event A scoring:** no script, scores were entered by hand. Follow `README.md`: `coding.csv` -> raw answer in `raw/<case>/` -> prompt in `sample/<case>/` -> developers' change in `after.java`.
- **M4 tests:** commons-lang at `4949adec`, `ReflectionDiffBuilderTest` (13/13) and `DiffBuilderTest` (34/34), reports in `m4/`.
- **Event B API check:** `bash event_b/run_api_check.sh [work_dir]`. Needs git, curl and JDK 17+ (tested on OpenJDK 21), no Maven. Uses `:` classpath separators, so run on macOS, Linux or WSL. Output goes to `event_b/evidence/`. Event B was run once, on 13 Nov 2026 (see `deviations.md`). Re-running it was not repeated for the final submission.

## AI use
The final conclusion and the RQ2 deviation entry were drafted with help from Claude, using only the repo files and the group tracker as sources. Cormac checked each claim against the repo before submission.

# RQ2 plan (frozen before any prompt is sent)

Recorded 19 Nov 2026 from the M1 plan in the group tracker (RQs and Milestones, G03 / P06, cells D, F, G, H). Nothing here changes the M1 plan.

## RQ2 (verbatim, cell D)
"When the same LLM (GPT-5.6 luna) refactors the 9 RefactoringGuru Java examples that match the study's refactoring types, how often do it's solutions compile and pass a behaviour test and equivalent in design to the RefactoringGuru reference (>=3 on the paper's 0-4 scale). What unsafe changes remain afterwards, and what does that imply for whether to accept, modify or reject an LLM proposed refactoring?"

## Cases (cell F)
The 9 RefactoringGuru Java examples (https://github.com/RefactoringGuru/refactoring-examples, simple/java, commit 1008820d87819681ee4f42c103a7ba4c6e67687e): extract-class, extract-method (3 variants), extract-variable (2 variants), inline-method, inline-temp, rename-method.

## Method and decision rule (cell G)
- Same model (GPT-5.6 luna), P3-style prompt from the paper's replication package (bitselab/LLM4Refactoring @ 28108cd), on the 9 examples wrapped to compile. One run per prompt, new temporary chat each, ChatGPT website, default settings, first valid answer only (as in Event A).
- Safe = compiles and passes a pre-written test. Quality = 0-4 rating against the RefactoringGuru reference by three people.
- Accept if safe and rated 3+; modify if safe but below 3, or fixable in 3 lines or fewer; otherwise reject.
- Useful helper if 6+/9 are accepted or modified and there are no silent behaviour changes.

## Prediction (cell H, frozen at M1)
Better than RQ1 (short code plus hints): 7-9/9 pass tests, 6-7/9 rated 3+, at most 1-2 unsafe (mostly behaviour changes; renames weakest). 6+/9 accepted or modified, but every answer still needs testing.

## What was prepared before running (this commit)
- Behaviour tests, written before any LLM answer: `rq2/<Case>/<Case>Test.java`. Each passes on the wrapped original and on the wrapped reference (`bash rq2/run_all.sh`, run locally), and each fails when a bug is injected into the reference (9/9).
- The wrapped code and the 9 prompts exist locally. They are adaptations of CC BY-NC-ND 4.0 material, so they are not published in this public repo. Their SHA-256 hashes are in `rq2/MANIFEST.sha256`, so the exact files used can be verified later.
- Prompt wording follows the paper's P3 structure. The paper's problem sentences assume its own dataset, so two were adapted: extract method (x3) says "group a code fragment into a well-named method" instead of "remove duplicate code", and extract variable (x2) says "complex expression ... meaningful names" instead of "repeated expression". Inline temp uses the paper's "inline variable" type. See `deviations.md`.

## Known limits of the tests
- Rename method: the new name is unknown in advance, so the test finds the accessor by reflection and checks behaviour only.
- ExtractVariable-1: the reference itself throws NullPointerException for a null browser on a non-Mac platform (eager evaluation); the original does not. Printed as a non-gating DIAGNOSTIC line.
- "Passes" means passes these tests only.

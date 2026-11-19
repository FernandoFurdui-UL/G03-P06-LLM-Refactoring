# RQ2 result (P06): DRAFT, design ratings pending

> Fill the [RATING] cells once three raters have scored all 9 answers (`rating_sheet.md`, kept locally). Remove this banner when done.

**RQ2 (frozen at M1, tracker cell D):** how often GPT-5.6 luna's solutions to the 9 RefactoringGuru Java examples compile, pass a behaviour test and are equivalent in design to the reference (3+ on the 0-4 scale); what unsafe changes remain; and what that implies for accepting, modifying or rejecting an LLM proposal.

**Plan and prediction:** `plan.md`, committed (f88f2c2) before any prompt was sent. Prediction: 7-9/9 pass tests, 6-7/9 rated 3+, at most 1-2 unsafe.

## Method (as planned)
- P3-style prompt (paper's replication package), one run per prompt, new temporary ChatGPT chat each, default settings, first valid answer only. Run on 19 Nov 2026; times were not recorded (`run_log.csv`). Answers are stored unedited in `raw/`.
- The examples were wrapped to compile and each given a behaviour test before any answer was seen. A case is safe if the answer compiles and passes that test.
- Deviations: see `deviations.md` (P3 wording adapted for 5 of 9 cases; wrapped code not published; run times and model name not recorded).

## Result against the frozen tests

| | Predicted | Actual |
|---|---|---|
| Compile and pass the behaviour test | 7-9/9 | **8/9** |
| Rated 3+ against the reference | 6-7/9 | [RATING] |
| Unsafe | at most 1-2 | **1** (ExtractClass) |

Per case: ExtractMethod-1, ExtractMethod-2, ExtractMethod-3, ExtractVariable-1, ExtractVariable-2, InlineMethod, InlineTemp and RenameMethod passed. ExtractClass did not compile against the frozen test.

## Unsafe change
- **ExtractClass:** the answer moved `damage`, `weaponStatus` and `getDamage()` into a new `Weapon` class, as the reference does, but changed the public constructor from `Soldier(int, int, int)` to `Soldier(int, Weapon)`. Existing callers no longer compile. Its own demo ran and printed the right value, so this is an API break, not a logic error. The reference keeps the old constructor. This is the same kind of risk that Event B's japicmp check is designed to catch.
- No behaviour change was found in the other 8 answers.

## Design ratings and decisions

| Case | Rating (rater 1 / 2 / 3) | Decision |
|---|---|---|
| ExtractClass | [RATING] | [accept / modify / reject] |
| ExtractMethod-1 | [RATING] | [ ] |
| ExtractMethod-2 | [RATING] | [ ] |
| ExtractMethod-3 | [RATING] | [ ] |
| ExtractVariable-1 | [RATING] | [ ] |
| ExtractVariable-2 | [RATING] | [ ] |
| InlineMethod | [RATING] | [ ] |
| InlineTemp | [RATING] | [ ] |
| RenameMethod | [RATING] | [ ] |

Rule (frozen): accept if safe and rated 3+; modify if safe but below 3, or fixable in 3 lines or fewer; otherwise reject. Useful helper if 6+/9 accepted or modified with no silent behaviour changes: [RESULT].

## What the answers show (from reading them, to be confirmed by the ratings)
- The simplest refactorings (inline method, inline variable, rename method) matched the reference closely.
- Extract method and extract variable passed, but chose different shapes: a printing method instead of a returning one, two variables instead of three, one variable instead of three.
- ExtractVariable-1: the answer kept the original short-circuit order, so a null browser on a non-Mac platform does not throw. The reference throws in that case (eager evaluation), so the answer is safer on this edge than the reference.
- Extract class is again the weak spot, as in M2 and Event A: the one failure is the structural refactoring, and the failure is an API change.

## Implication for accept, modify or reject
[Write once the ratings are in. Draft position from the evidence so far: 8 of 9 answers preserved behaviour on the tests, so most are usable after review, but the one structural refactoring broke a public constructor, which the tests caught only because they call it. Accept small local refactorings with tests; check public signatures on structural ones, as in Event B.]

## Limitations
- 9 tiny examples (3 to 18 lines before wrapping), one run each, default website settings, no temperature control. One case moves the rate by 11 percentage points.
- The tests check the behaviours written in each test file only. The rename test finds the accessor by reflection and does not check the name.
- Wrapping changed the examples (constructors and helper classes added), so results apply to the wrapped code. The prompt wording was adapted for 5 of the 9 cases.
- The model name was not visible in the pasted answers; it is recorded as GPT-5.6 luna per the plan, not confirmed.
- Ratings are by [three people]; [say whether any rater saw the first-pass notes before rating].

## Evidence
| What | Where |
|---|---|
| Plan and prediction | `plan.md` (commit f88f2c2) |
| Behaviour tests | `<Case>/<Case>Test.java` |
| Hashes of wrapped code and prompts | `MANIFEST.sha256` |
| Raw answers (unedited) | `raw/<Case>/P3_response.txt` |
| Run log | `run_log.csv` |
| Ratings | `coding.csv` |

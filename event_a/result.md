# Event A result: transfer test (P06)

**Task.** Apply the frozen M2 method, unchanged, to 8 unseen refactoring cases from previously unused project snapshots, and compare the result with M2.

**Plan and prediction:** `plan.md`, frozen at tag `EVENT_A_PLAN` before any prompt was sent.

## Held-out material
- **Cases:** 8 cases, ID 3 of the first 8 refactoring types. All come from **commons-lang**, a project not used in M2.
- **Source:** LLM4Refactoring @ `28108cd`.
- **Details:** `sample/sample_ids.csv`.

## Method (unchanged from M2)
- The paper's P1 and P2 prompts, word for word.
- GPT-5.6 luna on the ChatGPT website, a new temporary chat per prompt, default settings, one run per prompt.
- Run on 30 Oct 2026 between 09:34 and 09:53 (`run_log.csv`). Answers are stored unedited in `raw/`.
- Same scoring rule: an answer scores 1 only if it actually performs the developers' refactoring on the same element.
- Same decision rule: a gap (P2 − P1) of 3 or more means M2 holds.
- No retuning.

## Result

| | Paper (ChatGPT) on these 8 | **Ours, Event A** | Ours, M2 (12 cases) |
|---|---|---|---|
| P1 identified | 1/8 (13%) | **0/8 (0%)** | 3/12 (25%) |
| P2 identified | 5/8 (63%) | **6/8 (75%)** | 7/12 (58%) |
| Gap (P2 − P1) | 4 | **6 (75 pp)** | 4 (33 pp) |

**Decision: the M2 conclusion transfers.** Naming the refactoring type in the prompt helps a lot. The gap of 6 is above the threshold of 3 and larger than in M2.

**Against our prediction** (`plan.md`):

| Measure | Predicted | Actual | Outcome |
|---|---|---|---|
| P2 | 5–7 | 6 | Correct |
| Gap | 3–5 | 6 | Above the range |
| P1 | 1–3 | 0 | **Wrong**: lower than predicted |
| Extract Class missed again | yes | missed | Correct |

## Case by case

| Case | Paper P1 | Ours P1 | Paper P2 | Ours P2 |
|---|---|---|---|---|
| ExtractClass-3 | 0 | 0 | 0 | 0 |
| ExtractMethod-3 | 0 | 0 | 1 | **0** |
| ExtractVariable-3 | 0 | 0 | 0 | **1** |
| InlineMethod-3 | 0 | 0 | 1 | 1 |
| InlineVariable-3 | 0 | 0 | 0 | **1** |
| RenameAttribute-3 | 0 | 0 | 1 | 1 |
| RenameMethod-3 | 1 | **0** | 1 | 1 |
| RenameParameter-3 | 0 | 0 | 1 | 1 |
| **Total** | **1** | **0** | **5** | **6** |

## What transfers and what does not

**Transfers: naming the type helps.**
- P2 beats P1 on every type except Extract Class and Extract Method.
- The gap of 6 is larger than both M2's (4) and the paper's (4) on these cases.
- *Strongest evidence:* all 6 non-extract-class/method cases went from 0 under P1 to 1 under P2.

**Transfers: structural refactorings remain the weak spot.**
- Extract Class failed in M2 (cases 1 and 2) and fails again here.
- Extract Method fails here under P2.
- In both, the model chose a *different, larger* unit from the developers:
  - It moved the whole nested test class to its own file instead of extracting the counter.
  - It extracted `appendField()` instead of a `readField()` helper.

**Does not transfer: "the newer model is better than the paper on P1".**
- In M2 our P1 was 3/12 against the paper's 1/12. Here it is 0/8 against the paper's 1/8.
- The only P1 success in the paper (RenameMethod-3) was not repeated.
- With an open-ended prompt, the model produced general "improvement" lists and never hit the developers' change. The M2 P1 advantage looks like sample-specific variation rather than a model effect.

**Does not transfer: case-level agreement with the paper.**
- P1 matches the paper on 7/8 cases. P2 matches on only 5/8 cases (we gained 2 and lost 1), as in M2.
- The aggregate effect is stable, but which individual cases succeed is not.

## Design and maintainability implication

The answers show two things that matter for whether LLM refactorings can be trusted. Both relate to the module's idea that **a refactoring must preserve behaviour and should be a small, reviewable step**.

1. **Open-ended prompts mix refactoring with behaviour changes.** Several P1 answers presented bug fixes as "refactoring":
   - ExtractVariable-3 P1 changed `> 0` to `>= 0` in a `binarySearch` check.
   - ExtractMethod-3 P1 and RenameParameter-3 P1 changed constructor null handling.

   These may be improvements, but they are not behaviour-preserving, so merging them as "refactoring" would hide functional changes from review.
2. **Even targeted prompts over-apply.** P2 hit the target element in 6/8 cases, but often changed far more than asked:
   - RenameMethod-3 P2 renamed all 10 test methods.
   - InlineVariable-3 P2 inlined about 7 variables.
   - RenameAttribute-3 P2 renamed 5 identifiers.

   Larger diffs mean more review effort and more risk.

**Implication.**
- An LLM refactoring suggestion should be treated as a proposal, accepted only one refactoring at a time, behind the existing tests plus human review. It should not be applied wholesale.
- It is most usable for local refactorings (rename, extract/inline variable, inline method), where P2 succeeded on 6/6 here.
- Structural decisions (what to extract into a new class or method) depend on cohesion and responsibility judgements. The model got these wrong in every case in both M2 and Event A, so they should stay with the developer.
- We do not introduce a design pattern. Nothing in the evidence calls for one, and the problem is process (verification and review), not structure.

## Scoring, agreement and limitations
- **Agreement:** markers A and B agreed on all 16 answers. No tie-break was needed.
- **The markings were not fully independent** (see `deviations.md`, 30 Oct):
  - Marker B's scores were entered in the shared `coding.csv` on GitHub before marker A's (10:20 vs 10:29).
  - Marker A also used an AI-written summary of each answer and an AI opinion on each score.
  - So 16/16 overstates independent agreement. The scores were checked against the raw answers in `raw/`, and the reason for each judgement call is in `coding.csv`.
- **The one borderline case is ExtractMethod-3 P2.** We read the rule as "at least half of the code the LLM extracted must match the developers' extracted code", which gives 0. Under the opposite reading it would score 1 (P2 7/8, gap 7). The conclusion is the same either way.
- **Other limitations:**
  - 8 cases, all from one project, so one case moves a rate by 12.5 pp.
  - One run per prompt on the website, with no temperature control.
  - Possible training-data exposure.
  - Some answers lost formatting when copied (`run_log.csv` notes).
- **Verification layer:** not run. The frozen first-run result above is the Event A result.

## Evidence

| What | Where |
|---|---|
| Plan and prediction | `plan.md` (tag `EVENT_A_PLAN`) |
| Held-out cases, ground truth, prompts | `sample/`, `sample/sample_ids.csv` |
| Paper's answers and scores | `original_results/` |
| Raw answers (unedited) | `raw/<case>/P1_response.txt`, `P2_response.txt` |
| Run log | `run_log.csv` |
| Scores | `coding.csv` |
| Freeze | tag `EVENT_A` |

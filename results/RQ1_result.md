# RQ1 result (M2: bounded reproduction)

**RQ1:** Does naming the expected refactoring type in the prompt (P2) increase GPT-5.6 luna's opportunity-identification rate over the open-ended prompt (P1), and does the increase match the original study?

## Result

| | Paper (ChatGPT, same 12 cases) | Ours (GPT-5.6 luna) |
|---|---|---|
| P1 identified | 1/12 (8%) | **3/12 (25%)** |
| P2 identified | 6/12 (50%) | **7/12 (58%)** |
| Gap (P2 − P1) | 5 | **4** |

**Decision rule (frozen at M1):** gap ≥ 3 = REPRODUCED; gap ≤ 2 = NOT REPRODUCED; INCONCLUSIVE if no usable answers.

**Verdict: REPRODUCED.** Naming the refactoring type still helps substantially (gap of 4 against the paper's 5). Both rates are higher than the paper's. The result lies within our preregistered prediction (P1 2–4, P2 6–9, gap 3–4).

For context, across all 180 instances the paper reports 15.6% for P1 and 52.2% for P2. On our 12 cases the paper's figures were 8% and 50%, so our sample is broadly in line with the full dataset.

## Scoring and agreement

- Marker A and marker B scored all 24 answers independently against the developers' refactoring (`sample/sample_ids.csv`, `after.java`).
- **Agreement: 18/24 (75%), Cohen's κ = 0.50** (moderate).
- Six disagreements were resolved by a third member. All six went with marker A. The reasons are in `results/coding.csv` (column `note`).
- **Pattern in the disagreements:** marker B credited answers that named the right type of refactoring but either applied it to a different element or declined to do it:
  - ExtractVariable-1 P1/P2
  - InlineMethod-1 P2
  - RenameMethod-1 P2

  Marker B also did not credit two answers that made the developers' change in code without naming it:
  - InlineVariable-1 P1: `origQuery` removed
  - RenameParameter-1 P1: `severityLevelNameLength` → `severityLevelName`

  The tie-break applied the frozen rule: an answer scores 1 only if it actually performs the developers' refactoring on the same element. Naming it is not enough, and the label the LLM uses does not matter.
- Marker A's totals (P1 3, P2 7) match the final totals. Marker B's own totals were P1 2, P2 10, a gap of 8. The verdict is REPRODUCED under either marker.

## Case by case

| Case | Paper P1 | Ours P1 | Paper P2 | Ours P2 |
|---|---|---|---|---|
| ExtractClass-1 | 0 | 0 | 0 | 0 |
| ExtractMethod-1 | 0 | **1** | 1 | 1 |
| ExtractVariable-1 | 0 | 0 | 0 | 0 |
| InlineMethod-1 | 0 | 0 | 0 | 0 |
| InlineVariable-1 | 0 | **1** | 0 | **1** |
| RenameAttribute-1 | 0 | 0 | 0 | **1** |
| RenameMethod-1 | 0 | 0 | 1 | **0** |
| RenameParameter-1 | 1 | 1 | 1 | 1 |
| RenameVariable-1 | 0 | 0 | 1 | 1 |
| ExtractClass-2 | 0 | 0 | 1 | **0** |
| ExtractMethod-2 | 0 | 0 | 1 | 1 |
| ExtractVariable-2 | 0 | 0 | 0 | **1** |
| **Total** | **1** | **3** | **6** | **7** |

The aggregate effect reproduces, but which specific cases succeed does not:
- **P1:** we match the paper on 10/12 cases.
- **P2:** we match on only 7/12. We gained 3 cases and lost 2.

The newer model often declined to refactor ("no refactoring is necessary") or chose a different target from the developers'. For example:
- **RenameMethod-1 P2:** it renamed `isName()` instead of `relativizeAndNormalizePath()`.
- **ExtractClass-1 and ExtractClass-2:** it treated the code as test code best left alone.

## Deviations (see `deviations.md`)

- GPT-5.6 luna instead of the paper's GPT-4, which is no longer available.
- ChatGPT website instead of the API:
  - temperature 0 was planned but could not be set, so the default was used;
  - the exact model version is not visible;
  - temporary chats leave no shareable link.
- One run per prompt; the first valid answer is used. For ExtractClass-1 P2, two attempts were discarded because the wrong text was pasted (logged in `run_log.csv`).
- The scoring rule ("must actually perform the refactoring") was applied by marker A and by the tie-break, but it was only written down in `deviations.md` on 29 Oct, after marker B had scored. Marker B applied a looser reading in 6 cases (see Scoring and agreement).
- The M1 workbook stated the paper's P2 baseline as 4/12. The correct figure is 6/12. The threshold (gap ≥ 3) was kept unchanged.

## Threats to validity

- **Model and setting:** a different model and default website settings mean the run cannot be exactly repeated, and a re-run could give different answers.
- **Contamination:** the dataset is public (2024–25) and may be in the model's training data.
- **Sample size:** with 12 cases, one case moves a rate by about 8 percentage points. The verdict rests on a gap of 4, one case above the threshold.
- **Scoring subjectivity:** κ = 0.50, and all disagreements were resolved toward one marker. The rule wording ("performs" vs "identifies") decided 6 of the 24 scores. Under marker B's looser reading, the gap would be larger, not smaller.
- **Copying from the website:** some code formatting was lost when answers were pasted into `results/raw/`.
- **Construct:** we score whether the change was *performed*. The paper's "Validation" column scores whether the opportunity was *identified*. The two may not line up exactly.

## Evidence

| What | Where |
|---|---|
| Sample (code, ground truth, prompts) | `sample/`, `sample/sample_ids.csv` |
| Paper baseline | `original_results/original_gpt_rq1.csv` |
| Raw answers (unedited) | `results/raw/<case>/P1_response.txt`, `P2_response.txt` |
| Run log | `results/run_log.csv` |
| Scores (A, B, agreed, final, tie-break notes) | `results/coding.csv` |
| Deviations | `deviations.md` |
| Freeze | tags `M2_REPRO`, `PRE_EVENT_A` |

# Event A plan (frozen before execution)

**Group:** G03 / P06. **Freeze:** commit this file and tag `EVENT_A_PLAN` **before** sending any prompt.

## Event A task (P06)
> Select eight refactoring cases from later or previously unused project snapshots in the linked material. Use the same frozen prompting/evaluation rule as M2. You may add one verification layer after the frozen run, but report the original and verification-enhanced results separately.

## 1. Held-out material
- **Source:** the authors' replication package, https://github.com/bitselab/LLM4Refactoring @ `28108cd` (the same artefact as M2).
- **Selection rule.** We take the lowest unused dataset ID for each refactoring type, from a project not used in M2. M2 used checkstyle, commons-io, hibernate-search and lucene. We keep the first 8 types in our frozen M1 type order.
- **Which cases this gives.** The rule picks ID 3 for every type, and every ID-3 case comes from **commons-lang**: a project and commit snapshots that were never part of M2. Rename Variable is the 9th type in our order, so it is not used.
- **No case was chosen by looking at results.** The rule picks the cases, and the paper's scores for them are listed below only as the baseline for comparison.

| Case | Project | Commit | LOC | Developers' refactoring | Paper P1 | Paper P2 |
|---|---|---|---|---|---|---|
| ExtractClass-3 | commons-lang | 5d146cd | 341 | Extract Class `CloseableCounter` | 0 | 0 |
| ExtractMethod-3 | commons-lang | 4949ade | 181 | Extract Method `readField(...)` | 0 | 1 |
| ExtractVariable-3 | commons-lang | 35e3828 | 184 | Extract Variable `localeStr` | 0 | 0 |
| InlineMethod-3 | commons-lang | bddf9a3 | 25 | Inline Method `validateNullToStringStyleRegistry()` | 0 | 1 |
| InlineVariable-3 | commons-lang | aa4eef8 | 165 | Inline Variable `expectedOutput` | 0 | 0 |
| RenameAttribute-3 | commons-lang | faa1a1e | 514 | `objectsEquals` → `equals` | 0 | 1 |
| RenameMethod-3 | commons-lang | b1b409a | 147 | `testNullStyle` → `testDefaultStyle` | 1 | 1 |
| RenameParameter-3 | commons-lang | 4949ade | 185 | `excludeFieldNamesParam` → `excludeFieldNames` | 0 | 1 |
| **Paper total** | | | | | **1/8** | **5/8** (gap 4) |

Full details are in `sample/sample_ids.csv`. The paper's original answers are in `original_results/`.

## 2. Method: identical to M2, no retuning
- **Prompts:** the paper's P1 and P2, word for word (`sample/<case>/P1_prompt.txt`, `P2_prompt.txt`).
- **Model and interface:** GPT-5.6 luna on the ChatGPT website, a new temporary chat per prompt, default settings, one run per prompt. We use the first valid answer and log any discarded attempt with its reason.
- **Saving answers:** each answer is saved unedited in `raw/<case>/P1_response.txt` and `P2_response.txt`. Every run is recorded in `run_log.csv`.
- **Coding rule (same as M2):**
  - **1** = the answer actually performs the developers' refactoring on the same element. The new name does not matter. For extract refactorings, at least half of the extracted code must match.
  - **0** = it says no refactoring is needed, picks a different target, or names the refactoring without performing it.
- **Marking:** marker A and marker B score independently. B does not see A's scores or notes. A third member resolves disagreements and records the reason, based on the rule.
- **Decision rule (same as M2):** gap (P2 − P1) ≥ 3 means naming the refactoring type helps (consistent with M2). A gap ≤ 2 does not support it.
  - *Note:* with 8 cases, a gap of 3 is 37.5 percentage points, against 25 points in M2, so the frozen threshold is stricter here. We keep it unchanged as Event A requires, and we report the percentages alongside.

## 3. Prediction (before execution)
- **Our result:** P1 **1–3/8**, P2 **5–7/8**, gap **3–5**, so **the M2 conclusion transfers**.
- **Reasoning:**
  - In M2, GPT-5.6 luna scored above the paper on both prompts, and the gap was close to the paper's.
  - These files are mostly smaller (25–514 LOC), while M2's misses were concentrated in the large files.
  - Several cases are test classes. The model refused test fixtures in M2 (ExtractClass-1 and -2), so we expect ExtractClass-3 to be missed again.
- **What would challenge M2:** a gap ≤ 2, or P2 ≤ P1. Either would mean that naming the type does not reliably help on unseen projects. A large drop in P2 below the paper's 5/8 would also challenge our M2 observation that the newer model is at least as good as the paper's.

## 4. Verification layer (after the frozen run, reported separately)
- **What we do:** a second, independent run. Each of the 16 prompts is sent once more in a new temporary chat and scored with the same rule and the same two markers plus tie-break.
- **Verified result:** a case counts as **verified-identified** only if it scores 1 in **both** runs.
- **Why:** this tests our M2 threat that a single run on the website may not be stable.
- **Reporting:** the frozen first-run result is the Event A result. The verified result is reported separately, alongside it. Neither replaces the other.

## 5. Design implication (to complete after the result)
We expect the evidence to show that the LLM is more reliable on local, low-risk refactorings (Rename, Extract/Inline Variable) than on structural ones (Extract Class). Structural ones depend on cohesion and coupling judgements and on context beyond the file.

The candidate implication: treat LLM refactoring suggestions as proposals behind a verification gate (behaviour-preserving tests plus human review), rather than applying them automatically. Limit unattended use to local refactorings. We will introduce no design pattern unless the evidence calls for it. This will be confirmed or revised against the actual result.

## 6. Outputs
| What | Where |
|---|---|
| Raw answers | `raw/` |
| Run log | `run_log.csv` |
| Scores | `coding.csv` (first run), `coding_verification.csv` (second run) |
| Result | `result.md` |
| Tags | `EVENT_A_PLAN` (this plan), `EVENT_A` (result) |

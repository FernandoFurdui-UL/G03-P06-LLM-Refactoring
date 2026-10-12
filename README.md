
## Study
Liu et al., "Exploring the potential of general purpose LLMs in automated software refactoring: an empirical study", Automated Software Engineering (2025). https://doi.org/10.1007/s10515-025-00500-0

## Source
Replication package: https://github.com/bitselab/LLM4Refactoring, commit 28108cde5d802181e934411aa39207af00cbc669 (accessed 7 Oct 2026). Files in `sample/` and `original_results/` are copied unchanged from it.

## Folders
- `sample/sample_ids.csv` – the 12 frozen RQ1 cases and the developers' real refactoring (answer key)
- `sample/<case>/` – before.java (LLM input), after.java (ground truth), P1/P2 prompts
- `original_results/` – the paper's own GPT answers and 0/1 scores for these cases (P1 1/12, P2 6/12)
- `results/raw/<case>/` – our raw ChatGPT answers (never edited)
- `results/run_log.csv` – one row per prompt sent
- `results/coding.csv` – Marker A / Marker B / final mark per answer
- `deviations.md` – any change from the frozen M1 plan

## Evidence chain (RQ1)
sample_ids.csv → sample/<case>/P?_prompt.txt → results/raw/<case>/P?_response.txt (+ run_log.csv) → results/coding.csv → P1/P2 totals → Tracker

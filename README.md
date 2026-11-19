## Paper
We are reproducing part of Liu et al. (2025), "Exploring the potential of general purpose LLMs in automated software refactoring: an empirical study", published in Automated Software Engineering. https://doi.org/10.1007/s10515-025-00500-0

## Where the data comes from
All code, prompts and original answers come from the authors' replication package on GitHub: https://github.com/bitselab/LLM4Refactoring (commit 28108cd, downloaded 7 Oct 2026). We copied these files over without changing them, only renaming them. The two CSV files (sample_ids.csv and original_gpt_rq1.csv) were made by us from the authors' spreadsheets.

## What's in each folder
sample: our 12 chosen cases. Each folder has the code we give ChatGPT (before.java), the developers' actual refactoring (after.java) and the paper's two prompts. sample_ids.csv lists the cases and what refactoring the developers did.

original_results: the paper's ChatGPT answers for our 12 cases, and their scores (1/12 with P1, 6/12 with P2).

results: our ChatGPT answers go in raw (we never edit these), run_log.csv records each prompt we send, and coding.csv is where we mark each answer.

deviations.md: anything we did differently from our Week 5 plan.

## How to check a result
Every score can be traced back. Start from a mark in coding.csv, find the matching answer in results/raw, then the prompt it came from in sample, and the case in sample_ids.csv.

## Other folders
event_a/, m4/, event_b/: the Event A, M4 design decision and Event B work. rq2/: our RQ2 extension. final_conclusion.md: the final conclusion.

## RQ2 and the RefactoringGuru examples
RQ2 uses 9 examples from RefactoringGuru (https://refactoring.guru/refactoring/catalog), which are licensed CC BY-NC-ND 4.0. To make them compile and testable we wrapped them in small classes. Because that is an adaptation, we do not publish the wrapped code or the prompts here. This repo has only the plan, the behaviour tests, the run log, a hash list of the wrapped files (rq2/MANIFEST.sha256) and ChatGPT's unedited answers in rq2/raw, which repeat parts of the wrapped code. The wrapped code and prompts are in the Brightspace submission so RQ2 can be re-run.

## AI use
ChatGPT was the model under test in RQ1 and RQ2, and its answers are kept unedited. Claude was used to help with the RQ2 test harness, the wrapped examples and drafts of the write-up. Every claim was checked against the repo, the tests and the results before it was kept. AI output is not treated as evidence by itself. Ratings are made by people, not by AI.

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

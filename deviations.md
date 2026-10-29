# Deviations from the frozen M1 plan
Record each one BEFORE re-running. Never delete earlier entries.

| Date | What changed | Why | Effect on interpretation |
|---|---|---|---|
| 7 Oct 2026 (at M1) | Single current model (GPT-5.6 luna) instead of the paper's ChatGPT + Gemini | Original model versions no longer available; pack specifies one current LLM | Higher P1/P2 scores may be represented because the of the more knowledgable model |
| 12 Oct 2026 | ChatGPT website used | No API access | We will use first answer given as it may change on re-run|
| 29 Oct 2026 | Temperature 0 (stated in the M1 method) was not used; the website's default setting was used | The ChatGPT website has no temperature setting (follows from the 12 Oct entry) | Answers may vary between runs; only the first valid answer is used |
| 29 Oct 2026 | The exact model version is unknown; the website only shows "GPT-5.6 luna" | The website does not show the model string | Results are tied to the model as served on 12 Oct 2026 |
| 29 Oct 2026 | Scoring rule made explicit: an answer scores 1 only if it actually performs the developers' refactoring on the same element. Naming it, or declining to do it, scores 0 | The M1 wording ("does the expected refactoring in the right place") was applied this way by marker A and by the tie-break, but it was only written down here on 29 Oct, after marker B scored on 23 Oct | Marker B used a looser reading in 6 of 24 answers. The final verdict is REPRODUCED under either reading (marker A gap 4, marker B gap 8) |
| 29 Oct 2026 | The M1 decision rule stated the paper's P2 baseline as 4/12 (gap 3); the correct figure from evaluations.xlsx is 6/12 (gap 5) | Counting error at M1 | The threshold (gap ≥ 3) was left unchanged, so the rule was not adjusted after seeing results |
| 29 Oct 2026 | The tracker's sample cell lists only 9 cases and says "covers all 9 types"; the 12 cases actually used are all 9 types at ID 1 plus Extract Class, Extract Method and Extract Variable at ID 2 | Transcription error in the tracker: Rename Attribute 1, Rename Method 1 and Rename Parameter 1 were left out of the list | None. The 12 cases match the stated count of 12 and the stated "all 9 types"; the sample was fixed before any run (commit e0b9690) |
| 29 Oct 2026 | The M1_PLAN tag was created late, on commit 2b2cb0a (the last commit before any LLM run) | The tag was missed in Week 5 | Plan content unchanged; the tag date shows it was added on 29 Oct |
| 29 Oct 2026 | The Event A brief was read before the PRE_EVENT_A tag was created | The tags were created late | M2 scoring was final at commit 16110df (23 Oct) and was not changed after the Event A brief was read |

# rq2/

RQ2 materials. The plan and prediction are in `plan.md`.

- `<Case>/<Case>Test.java`: behaviour tests (our own code).
- `run_case.sh`, `run_all.sh`: compile a candidate against a test and run it (needs a JDK). They expect `before.java` and `reference_after.java` next to the test.
- `MANIFEST.sha256`: hashes of the wrapped code and prompts used.
- `raw/<Case>/P3_response.txt`: ChatGPT's answers, unedited (added after the run).
- `run_log.csv`, `coding.csv`: run log and 0-4 ratings.

The wrapped examples and prompts are adaptations of Refactoring.Guru's examples (CC BY-NC-ND 4.0, https://github.com/RefactoringGuru/refactoring-examples), so they are not republished here. They can be supplied to markers on request.

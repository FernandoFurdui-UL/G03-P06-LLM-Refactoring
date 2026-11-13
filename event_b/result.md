# Event B result: public-API compatibility (P06)

**New pressure.** The affected code now has a public-API compatibility requirement. We revisit the M4 refactoring and ask whether our verification workflow would detect a compatibility risk.

**Prediction:** `prediction.md`, frozen at tag `EVENT_B_PLAN` before any analysis.

## Refactoring revisited (pre-Event-B state, tag `PRE_EVENT_B`)

| | |
|---|---|
| Case | ExtractMethod-3: `private readField(Field, Object)` extracted from `appendFields` in commons-lang `ReflectionDiffBuilder` (upstream commit `4949adec`) |
| Week 9 decision | The refactoring is acceptable because it preserves behaviour |
| Week 9 evidence | Unit tests only: `ReflectionDiffBuilderTest` 13/13, `DiffBuilderTest` 34/34 on both versions (`m4/`) |

## Options considered

| Option | What it means | Main trade-off |
|---|---|---|
| **A. No change** | Keep M4 as-is. Argue by reading the code that the helper is `private`, so the API is unaffected | No cost, but it depends on someone reading the code by hand and gives no evidence for LLM rewrites of whole classes |
| **B. Add an API check to verification** | Run japicmp (binary and source compatibility of the public and protected API) next to the existing unit tests | Small setup cost and a compiled jar per version. It checks signatures only, not behaviour, so it adds to the tests rather than replacing them |
| **C. Compatibility shims** | Keep any changed public signature as a `@Deprecated` method or constructor that calls the new one | Keeps old callers working, but adds API surface. Only justified if B finds a break |

**Chosen: B, done as analysis, with no change to the refactored code.** It's the smallest response that produces evidence. C was not needed because B found no break. Changing the LLM prompt to say "preserve the API" was rejected because it would alter the frozen M2 method.

## What we ran

`run_api_check.sh` (OpenJDK 21, no Maven) builds commons-lang @ `4949adec` once for each version of `ReflectionDiffBuilder` below. For each build it:
1. runs the M4 tests;
2. runs japicmp against the baseline version, covering the public and protected API;
3. runs a small client (`evidence/client/Client.java`) that was **compiled once against the baseline jar**, the same way a downstream user's existing build would be.

| Version | Source |
|---|---|
| `baseline` | `event_a/sample/ExtractMethod-3/before.java` (the version before the refactoring) |
| `developer` | `event_a/sample/ExtractMethod-3/after.java` (the M4 refactoring) |
| `llm_p1` / `llm_p2` | The code blocks from `event_a/raw/ExtractMethod-3/P1_response.txt` (lines 19–229) and `P2_response.txt` (lines 31–241), unedited |
| `canary_final` | `developer` with the class made `final`, a planted break to test the checks |
| `canary_param` | `developer` with the public constructor's `ToStringStyle style` parameter widened to `Object`, a planted break to test the checks |

## Result: pre-Event-B vs post-response

| Version | M4 tests (pre-Event-B check) | japicmp (added check) | Pre-compiled client |
|---|---|---|---|
| baseline | 47/47 pass | (reference) | runs |
| **developer (M4)** | 47/47 pass | **0 changes** | runs |
| llm_p1 | 47/47 pass | 0 changes | runs |
| llm_p2 | 47/47 pass | 0 changes | runs |
| canary_final | **47/47 pass** | **BREAK: `CLASS_NOW_FINAL`** | runs, but any subclass would fail |
| canary_param | **47/47 pass** | **BREAK: `CONSTRUCTOR_REMOVED`** | **`NoSuchMethodError`** |

The `javap` public signatures of `developer`, `llm_p1` and `llm_p2` are identical to `baseline` (`evidence/api/`).

**Key findings**
- **The M4 refactoring is API-safe.** The prediction was correct: the developers' change leaves the public API unchanged.
- **The LLM rewrites are API-safe too.** Both ExtractMethod-3 LLM answers kept the public API byte-for-byte, including `@Deprecated` markers. P1 said it was "preserving the existing public API", and that claim is now verified rather than assumed. The prediction that they were *the likely risk* was not borne out.
- **The Week 9 verification has a blind spot.** Both planted breaks passed all 47 M4 tests. Unit tests are recompiled against the new source each time, so they cannot see binary breaks, or breaks in API they don't exercise. `canary_param` passes every test yet crashes an existing caller with `NoSuchMethodError` (`evidence/client/client_run.txt`). japicmp caught both breaks.
- **Context: the real upstream commit was tangled.** Upstream commit `4949adec` contains the M4 refactoring, but it also added a public `ReflectionDiffBuilder.Builder` API and deprecated the public constructor and `setExcludeFieldNames`. japicmp reports this as additive with 0 incompatibilities (`evidence/japicmp/japicmp_real_commit_4949adec.txt`). The upstream developers used option C ("deprecate and add") themselves. The isolated Extract Method that our dataset scores hides that wider API change.

## Change spread

| | Pre-Event-B | Post-response |
|---|---|---|
| Production code changed | `ReflectionDiffBuilder.java` (private helper) | **None** (the refactoring is kept as-is) |
| Verification | Unit tests (2 classes, 47 tests) | Unit tests **+ japicmp API diff + pre-compiled client check** |
| New files | none | `event_b/` (script, evidence, this file) |

## Conclusion

**The Week 9 decision is only partly supported.**
- **Supported:** the refactoring decision itself. ExtractMethod-3 preserves both behaviour (47/47) and the public API (japicmp: 0 changes), so it is **kept unchanged**.
- **Not supported:** the verification behind that decision. Under a public-API requirement, "tests pass" is not enough evidence. Two planted API breaks passed every test, and only the API diff caught them. The workflow is **adapted** by adding japicmp; the code is not changed.

**Trade-off accepted.** An extra build-and-compare step for every refactoring, which only checks signatures, in exchange for detecting binary and source breaks mechanically.

**Anti-pattern avoided.** Adding compatibility shims or wrapper layers (option C) "just in case" when no break exists.

**Risk still remaining.**
- japicmp does not detect *behavioural* API changes (same signature, different result). Those still depend on the tests.
- The isolated, refactoring-only view in the dataset can hide API changes that ship in the same commit, as the tangled `4949adec` commit shows.

## Evidence

| What | Where |
|---|---|
| Prediction (frozen) | `prediction.md`, tag `EVENT_B_PLAN` |
| Reproduction script | `run_api_check.sh` |
| One-line summary per version | `evidence/summary.txt` |
| Code of each version | `evidence/variants/` |
| japicmp reports (txt + html) | `evidence/japicmp/` |
| Public signatures (`javap`) | `evidence/api/` |
| Test runs | `evidence/tests/` |
| Pre-compiled client and its output | `evidence/client/` |
| Pre-Event-B state | tag `PRE_EVENT_B`, `m4/` |
| Event B result | tag `EVENT_B` |

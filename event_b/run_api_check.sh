#!/usr/bin/env bash
# Event B: public-API compatibility check for the M4 refactoring (ExtractMethod-3,
# ReflectionDiffBuilder.readField). Needs: git, curl, JDK 17+ (tested on OpenJDK 21). No Maven.
#
# Usage:  bash event_b/run_api_check.sh [work_dir]
# Builds commons-lang @ 4949adec with each ReflectionDiffBuilder variant swapped in, then for each:
#   1. runs the M4 tests (ReflectionDiffBuilderTest + DiffBuilderTest),
#   2. runs japicmp against the baseline (pre-refactoring) jar,
#   3. runs a small client compiled against the baseline jar.
# Outputs go to event_b/evidence/.
set -u
REPO="$(cd "$(dirname "$0")/.." && pwd)"
EB="$REPO/event_b"
EV="$EB/evidence"
WORK="${1:-$(mktemp -d)}"
mkdir -p "$WORK/tools" "$EV/japicmp" "$EV/api" "$EV/tests" "$EV/client"

JAPICMP="$WORK/tools/japicmp-0.23.1-jar-with-dependencies.jar"
JUNIT="$WORK/tools/junit-platform-console-standalone-1.10.1.jar"
HAMCREST="$WORK/tools/hamcrest-2.2.jar"
M2=https://repo1.maven.org/maven2
[ -f "$JAPICMP" ]  || curl -sfL -o "$JAPICMP"  $M2/com/github/siom79/japicmp/japicmp/0.23.1/japicmp-0.23.1-jar-with-dependencies.jar
[ -f "$JUNIT" ]    || curl -sfL -o "$JUNIT"    $M2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1.jar
[ -f "$HAMCREST" ] || curl -sfL -o "$HAMCREST" $M2/org/hamcrest/hamcrest/2.2/hamcrest-2.2.jar

CL="$WORK/commons-lang"
[ -d "$CL" ] || git clone -q --filter=blob:none https://github.com/apache/commons-lang.git "$CL"
git -C "$CL" checkout -q 4949adec4
( cd "$CL" && find src/main/java -name '*.java' | grep -v 'builder/ReflectionDiffBuilder.java' | sed "s|^|$CL/|" ) > "$WORK/srcs.txt"
TST="$CL/src/test/java/org/apache/commons/lang3"

VARIANTS="baseline developer llm_p1 llm_p2 canary_final canary_param"

# Client compiled once against the baseline, then run against every variant (binary compatibility).
CLIENT_OUT="$WORK/client"
: > "$EV/client/client_run.txt"

for v in $VARIANTS; do
  B="$WORK/build/$v"; rm -rf "$B"; mkdir -p "$B/classes" "$B/test-classes" "$B/src/org/apache/commons/lang3/builder"
  cp "$EV/variants/$v.java" "$B/src/org/apache/commons/lang3/builder/ReflectionDiffBuilder.java"
  if ! javac -nowarn -encoding UTF-8 --release 8 -d "$B/classes" @"$WORK/srcs.txt" "$B/src/org/apache/commons/lang3/builder/ReflectionDiffBuilder.java" 2> "$B/javac.log"; then
    echo "$v: COMPILE FAILED (see $B/javac.log)"; continue
  fi
  ( cd "$B/classes" && jar cf "$B/commons-lang3-$v.jar" . )

  # 1. M4 tests
  javac -nowarn -encoding UTF-8 -d "$B/test-classes" -cp "$B/classes:$JUNIT:$HAMCREST" \
    "$TST/AbstractLangTest.java" "$TST/builder/ReflectionDiffBuilderTest.java" "$TST/builder/DiffBuilderTest.java"
  java -jar "$JUNIT" -cp "$B/classes:$B/test-classes:$HAMCREST" \
    --select-class org.apache.commons.lang3.builder.ReflectionDiffBuilderTest \
    --select-class org.apache.commons.lang3.builder.DiffBuilderTest \
    --details=summary --disable-banner > "$EV/tests/tests_$v.txt" 2>&1

  # Public/protected signatures
  javap -protected -cp "$B/classes" org.apache.commons.lang3.builder.ReflectionDiffBuilder \
    'org.apache.commons.lang3.builder.ReflectionDiffBuilder$Builder' > "$EV/api/api_$v.txt"

  # 2. japicmp vs baseline
  if [ "$v" != baseline ]; then
    java -jar "$JAPICMP" -o "$WORK/build/baseline/commons-lang3-baseline.jar" -n "$B/commons-lang3-$v.jar" \
      -a protected -m --html-file "$EV/japicmp/japicmp_$v.html" > "$EV/japicmp/japicmp_$v.txt" 2>&1
    java -jar "$JAPICMP" -o "$WORK/build/baseline/commons-lang3-baseline.jar" -n "$B/commons-lang3-$v.jar" \
      -a protected -m -b --error-on-binary-incompatibility --error-on-source-incompatibility >> "$EV/japicmp/japicmp_$v.txt" 2>&1
    JC=$?
  else
    JC=0
    mkdir -p "$CLIENT_OUT"
    javac -nowarn -d "$CLIENT_OUT" -cp "$B/commons-lang3-baseline.jar" "$EV/client/Client.java" 2>/dev/null
  fi

  # 3. pre-compiled client
  printf '%-13s ' "$v" >> "$EV/client/client_run.txt"
  java -cp "$CLIENT_OUT:$B/commons-lang3-$v.jar" Client 2>&1 | head -1 >> "$EV/client/client_run.txt"

  echo "$v: tests: $(grep -E 'tests (successful|failed)' "$EV/tests/tests_$v.txt" | tr -s ' ' | tr '\n' ' ') | japicmp exit=$JC (0 = no incompatibility) | client: $(tail -1 "$EV/client/client_run.txt" | cut -c15-)"
done | tee "$EV/summary.txt"

# Context: the real upstream commit that contains the M4 refactoring, vs its parent (full source trees).
for c in aa4eef85c 4949adec4; do
  git -C "$CL" checkout -q $c
  R="$WORK/build/real_$c"; rm -rf "$R"; mkdir -p "$R/classes"
  ( cd "$CL" && find src/main/java -name '*.java' | sed "s|^|$CL/|" ) > "$WORK/real_srcs.txt"
  javac -nowarn -encoding UTF-8 --release 8 -d "$R/classes" @"$WORK/real_srcs.txt" 2>/dev/null
  ( cd "$R/classes" && jar cf "$R/real.jar" . )
done
git -C "$CL" checkout -q 4949adec4
java -jar "$JAPICMP" -o "$WORK/build/real_aa4eef85c/real.jar" -n "$WORK/build/real_4949adec4/real.jar" \
  -a protected -m --html-file "$EV/japicmp/japicmp_real_commit_4949adec.html" > "$EV/japicmp/japicmp_real_commit_4949adec.txt" 2>&1
echo "real commit aa4eef85c -> 4949adec4: see evidence/japicmp/japicmp_real_commit_4949adec.txt" | tee -a "$EV/summary.txt"

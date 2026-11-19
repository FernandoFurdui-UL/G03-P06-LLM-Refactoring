#!/usr/bin/env bash
# Usage: bash run_case.sh <Case> [before|reference|<dir with candidate .java files>]
# Compiles the case's test with the chosen implementation and runs it. Prints COMPILE FAILED, PASS or FAIL.
set -u
HERE="$(cd "$(dirname "$0")" && pwd)"
C="$1"; WHAT="${2:-reference}"
T="$(echo "$C" | tr -d '-')Test"
OUT="$(mktemp -d)"
mkdir -p "$OUT/src"
case "$WHAT" in
  before)    cp "$HERE/$C/before.java" "$OUT/src/Impl.java" ;;
  reference) cp "$HERE/$C/reference_after.java" "$OUT/src/Impl.java" ;;
  *)         cp "$WHAT"/*.java "$OUT/src/" ;;
esac
cp "$HERE/$C/$T.java" "$OUT/src/"
if ! javac -nowarn -encoding UTF-8 -d "$OUT/classes" "$OUT"/src/*.java 2> "$OUT/javac.log"; then
  echo "$C [$WHAT]: COMPILE FAILED"; head -5 "$OUT/javac.log"; exit 2
fi
java -cp "$OUT/classes" "$T" | sed "s|^|$C [$WHAT]: |"

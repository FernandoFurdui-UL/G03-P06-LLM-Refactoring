#!/usr/bin/env bash
# Sanity check for the tests themselves: they must PASS on both the given code and the reference solution.
HERE="$(cd "$(dirname "$0")" && pwd)"
for C in ExtractClass ExtractMethod-1 ExtractMethod-2 ExtractMethod-3 ExtractVariable-1 ExtractVariable-2 InlineMethod InlineTemp RenameMethod; do
  for W in before reference; do bash "$HERE/run_case.sh" "$C" "$W"; done
done

Tested on commons-lang in IntelliJ.
Before: line 193 of ReflectionDiffBuilder.java changed to the two inlined FieldUtils.readField(..., true) calls, with the readField helper removed.
After: the developers’ original version with the readField(field, target) helper. 
Both versions passed ReflectionDiffBuilderTest 13/13 and DiffBuilderTest 34/34, so the refactoring preserved behaviour.

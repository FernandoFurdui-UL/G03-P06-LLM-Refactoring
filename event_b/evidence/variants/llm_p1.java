package org.apache.commons.lang3.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.commons.lang3.ArraySorter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionDiffBuilder<T> implements Builder<DiffResult<T>> {

    /**
     * Constructs a new instance.
     *
     * @param <T> type of the left and right object.
     * @since 3.15.0
     */
    public static final class Builder<T> {

        private String[] excludeFieldNames = ArrayUtils.EMPTY_STRING_ARRAY;
        private DiffBuilder<T> diffBuilder;

        /**
         * Builds a new configured ReflectionDiffBuilder.
         *
         * @return a new configured ReflectionDiffBuilder.
         */
        public ReflectionDiffBuilder<T> build() {
            return new ReflectionDiffBuilder<>(diffBuilder, excludeFieldNames);
        }

        /**
         * Sets the DiffBuilder.
         *
         * @param diffBuilder the DiffBuilder.
         * @return this.
         */
        public Builder<T> setDiffBuilder(final DiffBuilder<T> diffBuilder) {
            this.diffBuilder = diffBuilder;
            return this;
        }

        /**
         * Sets field names to exclude from output.
         *
         * @param excludeFieldNames field names to exclude.
         * @return this.
         */
        public Builder<T> setExcludeFieldNames(final String... excludeFieldNames) {
            this.excludeFieldNames = toExcludeFieldNames(excludeFieldNames);
            return this;
        }
    }

    /**
     * Constructs a new Builder.
     *
     * @param <T> type of the left and right object.
     * @return a new Builder.
     * @since 3.15.0
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    private static String[] toExcludeFieldNames(final String[] excludeFieldNames) {
        if (excludeFieldNames == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return ArraySorter.sort(
                ReflectionToStringBuilder.toNoNullStringArray(excludeFieldNames));
    }

    private final DiffBuilder<T> diffBuilder;

    /**
     * Field names to exclude from output.
     */
    private String[] excludeFieldNames;

    private ReflectionDiffBuilder(
            final DiffBuilder<T> diffBuilder,
            final String[] excludeFieldNames) {

        this.diffBuilder = diffBuilder;
        this.excludeFieldNames = toExcludeFieldNames(excludeFieldNames);
    }

    /**
     * Constructs a builder for the specified objects with the specified style.
     *
     * @param left this object.
     * @param right the object to diff against.
     * @param style the output style.
     * @throws IllegalArgumentException if left or right is null.
     * @deprecated Use {@link Builder}.
     */
    @Deprecated
    public ReflectionDiffBuilder(
            final T left,
            final T right,
            final ToStringStyle style) {

        this(
                DiffBuilder.<T>builder()
                        .setLeft(left)
                        .setRight(right)
                        .setStyle(style)
                        .build(),
                null);
    }

    /**
     * Determines whether a field should be included in the diff.
     *
     * @param field the field to evaluate.
     * @return true if the field should be included.
     */
    private boolean accept(final Field field) {
        final int modifiers = field.getModifiers();

        return field.getName().indexOf(
                        ClassUtils.INNER_CLASS_SEPARATOR_CHAR) == -1
                && !Modifier.isTransient(modifiers)
                && !Modifier.isStatic(modifiers)
                && Arrays.binarySearch(
                        excludeFieldNames, field.getName()) < 0
                && !field.isAnnotationPresent(DiffExclude.class);
    }

    /**
     * Appends eligible fields to the DiffBuilder.
     *
     * @param clazz the class whose fields should be processed.
     */
    private void appendFields(final Class<?> clazz) {
        for (final Field field : FieldUtils.getAllFields(clazz)) {
            if (!accept(field)) {
                continue;
            }

            try {
                final String fieldName = field.getName();

                final Object leftValue =
                        FieldUtils.readField(field, getLeft(), true);

                final Object rightValue =
                        FieldUtils.readField(field, getRight(), true);

                diffBuilder.append(fieldName, leftValue, rightValue);

            } catch (final IllegalAccessException e) {
                throw new IllegalArgumentException(
                        "Unexpected IllegalAccessException: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public DiffResult<T> build() {
        final T left = getLeft();
        final T right = getRight();

        if (left == right || left.equals(right)) {
            return diffBuilder.build();
        }

        appendFields(left.getClass());

        return diffBuilder.build();
    }

    /**
     * Gets the field names that should be excluded from the diff.
     *
     * @return the excluded field names.
     * @since 3.13.0
     */
    public String[] getExcludeFieldNames() {
        return excludeFieldNames.clone();
    }

    private T getLeft() {
        return diffBuilder.getLeft();
    }

    private T getRight() {
        return diffBuilder.getRight();
    }

    /**
     * Sets the field names to exclude.
     *
     * @param excludeFieldNames field names to exclude.
     * @return this.
     * @since 3.13.0
     * @deprecated Use {@link Builder#setExcludeFieldNames(String[])}.
     */
    @Deprecated
    public ReflectionDiffBuilder<T> setExcludeFieldNames(
            final String... excludeFieldNames) {

        this.excludeFieldNames = toExcludeFieldNames(excludeFieldNames);

        return this;
    }
}

package org.bseber.haushalt.tags;

import jakarta.annotation.Nullable;
import org.bseber.haushalt.core.Budget;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a not yet created {@link Tag}.
 */
public sealed class NewTag permits Tag {

    private final TagName name;
    private final TagDescription description;
    private final TagColor color;
    private final Budget budget;

    /**
     * Creates a default NewTag with no budget and color set to {@linkplain TagColor#LIME}.
     *
     * @param name name of the tag
     */
    public NewTag(TagName name) {
        this(name, TagDescription.empty(), TagColor.LIME, null);
    }

    /**
     *
     * @param name name of the tag
     * @param description description of the tag
     * @param color color of the tag
     */
    public NewTag(TagName name, TagDescription description, TagColor color) {
        this(name, description, color, null);
    }

    /**
     *
     * @param name name of the tag
     * @param description description of the tag
     * @param color color of the tag
     * @param budget optional budget, can be {@code null}
     */
    public NewTag(TagName name, TagDescription description, TagColor color, @Nullable Budget budget) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.budget = budget;
    }

    public TagName name() {
        return name;
    }

    public TagDescription description() {
        return description;
    }

    public TagColor color() {
        return color;
    }

    public Optional<Budget> budget() {
        return Optional.ofNullable(budget);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewTag newTag = (NewTag) o;
        return Objects.equals(name, newTag.name)
            && Objects.equals(description, newTag.description)
            && color == newTag.color
            && Objects.equals(budget, newTag.budget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, color, budget);
    }

    @Override
    public String toString() {
        return "NewTag{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", color=" + color +
            ", budget=" + budget +
            '}';
    }
}

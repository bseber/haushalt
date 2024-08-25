package org.bseber.haushalt.tags;

import jakarta.annotation.Nullable;
import org.bseber.haushalt.core.Budget;
import org.bseber.haushalt.transactions.Transaction;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * A Tag can be used to group {@linkplain  Transaction Transactions} and optionally to define a {@linkplain  Budget}.
 *
 * <p>
 * A tag is identified either by its {@linkplain Tag.Id} or by its {@linkplain TagName} which is unique.
 */
public final class Tag extends NewTag {

    private final Tag.Id id;

    /**
     *
     * @param id id of the tag
     * @param name name of the tag
     * @param description description of the tag
     * @param color color of the tag
     * @param budget optional budget, can be {@code null}
     */
    public Tag(Id id, TagName name, TagDescription description, TagColor color, @Nullable Budget budget) {
        super(name, description, color, budget);
        this.id = id;
    }

    public Id id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tag{" +
            "id=" + id +
            '}';
    }

    /**
     * Identifier of a {@link Tag}.
     *
     * @param value
     */
    public record Id(Long value) {
        public Id {
            Assert.notNull(value, "tag id value must not be null.");
        }
    }
}

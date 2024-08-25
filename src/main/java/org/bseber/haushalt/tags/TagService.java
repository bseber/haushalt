package org.bseber.haushalt.tags;

import org.bseber.haushalt.transactions.Transaction;
import org.bseber.haushalt.transactions.TransactionId;

import java.util.Collection;
import java.util.List;

public interface TagService {

    /**
     * Creates a new {@link Tag}.
     *
     * @param newTag new tag to create
     * @return the created {@link Tag}
     */
    Tag createNewTag(NewTag newTag);

    /**
     * Create new {@link Tag Tags}.
     *
     * @param newTags new tags to create
     * @return created tags in same order
     */
    List<Tag> createNewTag(List<NewTag> newTags);

    /**
     * Update {@link Tag Tags}.
     *
     * @param tags existing tags to update
     * @return updated tags
     */
    List<Tag> updateTags(List<Tag> tags);

    /**
     * Return all {@link Tag Tags} sorted by name.
     *
     * @return list of all {@link Tag Tags} sorted by name
     */
    List<Tag> findAllTags();

    /**
     * Return all {@link Tag Tags} for the given {@link Transaction} sorted by {@linkplain Tag#name() name}.
     *
     * @param transactionId id of the {@linkplain Transaction}
     * @return all {@link Tag Tags} for the given {@link Transaction} sorted by {@linkplain Tag#name() name}
     */
    List<Tag> findAllTagsForTransaction(TransactionId transactionId);

    /**
     * Adds the given tag to the {@link Transaction}.
     *
     * <p>
     * A new {@link Tag} will be created, if the {@code tagName} does not exist yet.
     *
     * @param transactionId id of the {@linkplain Transaction}
     * @param tagName name of an existing {@linkplain Tag} or the new {@linkplain Tag} created
     */
    void addTagToTransaction(TransactionId transactionId, TagName tagName);

    /**
     * Updates the assigned {@link Tag Tags} of a {@link Transaction}.
     *
     * <p>
     * Removes all currently assigned {@linkplain Tag Tags} that are not in the given {@code tagNames} list.
     *
     * <p>
     * Creates a new {@linkplain Tag} when the name does not exist yet.
     *
     * @param transactionId id of the {@linkplain Transaction}
     * @param tagNames new assigned {@link Tag Tags}
     */
    void updateTagsOfTransaction(TransactionId transactionId, Collection<TagName> tagNames);
}

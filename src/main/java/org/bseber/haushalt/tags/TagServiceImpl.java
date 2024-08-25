package org.bseber.haushalt.tags;

import org.bseber.haushalt.core.Budget;
import org.bseber.haushalt.core.Money;
import org.bseber.haushalt.transactions.TransactionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.function.Predicate.not;
import static java.util.stream.StreamSupport.stream;

@Service
class TagServiceImpl implements TagService {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    private final TagRepository repository;

    TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag createNewTag(NewTag newTag) {
        return createNewTag(List.of(newTag)).getFirst();
    }

    @Override
    public List<Tag> createNewTag(List<NewTag> newTags) {

        if (newTags.size() == 1) {
            LOG.info("Create new tag {}", newTags.getFirst());
        } else {
            LOG.info("Create {} new tags {}", newTags.size(), newTags);
        }

        final List<TagEntity> entities = newTags.stream()
            .map(TagServiceImpl::toEntity)
            .toList();

        final Iterable<TagEntity> saved = repository.saveAll(entities);
        return map(saved, TagServiceImpl::toTag);
    }

    @Override
    public List<Tag> updateTags(List<Tag> tags) {
        // TODO implement me
        return List.of();
    }

    @Override
    public List<Tag> findAllTags() {
        return map(repository.findAll(), TagServiceImpl::toTag);
    }

    @Override
    public List<Tag> findAllTagsForTransaction(TransactionId transactionId) {
        return map(repository.findAllByTransactionOrderByName(transactionId.value()), TagServiceImpl::toTag);
    }

    @Transactional
    @Override
    public void addTagToTransaction(TransactionId transactionId, TagName tagName) {
        LOG.info("Add tag name={} to transaction id={}", tagName, transactionId);

        final Iterable<TagEntity> currentTags = repository.findAllByTransactionOrderByName(transactionId.value());
        final boolean assigned = asStream(currentTags).anyMatch(tag -> tag.getName().equals(tagName.value()));
        if (assigned) {
            LOG.info("Tag {} is already assigned to transaction id={}", tagName, transactionId);
            return;
        }

        final Long tagId = repository.findByName(tagName.value())
            .map(TagEntity::getId)
            .orElseGet(() -> createNewTag(new NewTag(tagName)).id().value());

        repository.saveTransactionTag(transactionId.value(), tagId);
    }

    @Transactional
    @Override
    public void updateTagsOfTransaction(TransactionId transactionId, Collection<TagName> tagNames) {
        LOG.info("Update tags of transaction id={}", transactionId);

        if (tagNames.isEmpty()) {
            LOG.info("No tags incoming. Delete all tags of transaction id={}", transactionId.value());
            repository.deleteAllTransactionTags(transactionId.value());
            return;
        }

        final List<TagEntity> currentTransactionTags = asList(repository.findAllByTransactionOrderByName(transactionId.value()));
        final List<String> allExistingTagNames = map(repository.findAll(), TagEntity::getName);

        final List<NewTag> newTagsToCreate = tagNames.stream()
            .filter(not(tagName -> allExistingTagNames.stream().anyMatch(name -> tagName.value().equals(name))))
            .map(NewTag::new)
            .toList();
        if (!newTagsToCreate.isEmpty()) {
            createNewTag(newTagsToCreate);
        }

        final List<Long> tagIdsToDelete = currentTransactionTags.stream()
            .filter(not(e -> tagNames.stream().anyMatch(tagName -> tagName.value().equals(e.getName()))))
            .map(TagEntity::getId)
            .toList();
        if (!tagIdsToDelete.isEmpty()) {
            LOG.info("Delete tags {} of transaction id={}", tagIdsToDelete, transactionId.value());
            repository.deleteAllTransactionTagsByTagIdIsIn(transactionId.value(), tagIdsToDelete);
        }

        final List<String> tagNamesToAdd = tagNames.stream()
            .map(TagName::value)
            .filter(not(name -> currentTransactionTags.stream().anyMatch(entity -> entity.getName().equals(name))))
            .toList();
        if (!tagNamesToAdd.isEmpty()) {
            LOG.info("Add new tags {} to transaction id={}", tagNamesToAdd, transactionId.value());
            repository.saveAllTransactionTags(transactionId.value(), tagNamesToAdd);
        }
    }

    private static Tag toTag(TagEntity entity) {

        final Budget budget;
        if (entity.getBudget() == null) {
            budget = null;
        } else {
            budget = new Budget(Money.ofEUR(entity.getBudget()), entity.getBudgetType());
        }

        final Tag.Id id = new Tag.Id(entity.getId());
        final TagName name = new TagName(entity.getName());
        final TagDescription description = new TagDescription(entity.getDescription());

        return new Tag(id, name, description, entity.getColor(), budget);
    }

    private static TagEntity toEntity(NewTag newTag) {
        final TagEntity entity = new TagEntity();
        entity.setName(newTag.name().value());
        entity.setDescription(newTag.description().value());
        entity.setColor(newTag.color());
        entity.setBudget(newTag.budget().map(Budget::amount).map(Money::amount).orElse(null));
        entity.setBudgetType(newTag.budget().map(Budget::type).orElse(null));
        return entity;
    }

    private static <T> Stream<T> asStream(Iterable<T> iterable) {
        return stream(iterable.spliterator(), false);
    }

    private static <T> List<T> asList(Iterable<T> iterable) {
        return asStream(iterable).toList();
    }

    private static <T, R> List<R> map(Iterable<T> iterable, Function<T, R> mapper) {
        return asStream(iterable).map(mapper).toList();
    }
}

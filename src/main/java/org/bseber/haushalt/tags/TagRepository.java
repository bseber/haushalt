package org.bseber.haushalt.tags;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

interface TagRepository extends CrudRepository<TagEntity, Long> {

    Optional<TagEntity> findByName(String name);

    Iterable<TagEntity> findAllByNameIsInOrderByName(Collection<String> names);

    @Query("""
        select * from tag
          inner join transaction_tag tt on tag.id = tt.tag_id
          where tt.transaction_id = :transactionId
        order by tag.name
        """)
    Iterable<TagEntity> findAllByTransactionOrderByName(@Param("transactionId") Long transactionId);

    @Modifying
    @Query("""
        insert into transaction_tag (transaction_id, tag_id)
          values (:transactionId, :tagId)
        """)
    void saveTransactionTag(@Param("transactionId") Long transactionId, @Param("tagId") Long tagId);

    @Modifying
    @Query("""
        insert into transaction_tag (transaction_id, tag_id)
          select :transactionId, id from tag where tag.name in (:tagNames)
        """)
    void saveAllTransactionTags(@Param("transactionId") Long transactionId, @Param("tagNames") Collection<String> tagNames);

    @Modifying
    @Query("""
        delete from transaction_tag t
          where t.transaction_id = :transactionId and t.tag_id = :tagId;
        """)
    void deleteTransactionTag(@Param("transactionId") Long transactionId, @Param("tagId") Long tagId);

    @Modifying
    @Query("""
        delete from transaction_tag t
          where t.transaction_id = :transactionId and t.tag_id in (:tagIds)
        """)
    void deleteAllTransactionTagsByTagIdIsIn(@Param("transactionId") Long transactionId, @Param("tagIds") Collection<Long> tagIds);

    @Modifying
    @Query("""
        delete from transaction_tag t where t.transaction_id = :transactionId
        """)
    void deleteAllTransactionTags(@Param("transactionId") Long transactionId);
}

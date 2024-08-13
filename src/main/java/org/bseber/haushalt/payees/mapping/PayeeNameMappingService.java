package org.bseber.haushalt.payees.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

@Service
class PayeeNameMappingService {

    private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

    private final PayeeNameMappingRepository repository;

    PayeeNameMappingService(PayeeNameMappingRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns {@link PayeeNameMapping}s for every existing Payee.
     *
     * @return list of mappings sorted by original name
     */
    List<PayeeNameMapping> findAllPayeeMappings() {

        final Map<String, PayeeNameMapping> mappingsByOriginalName = stream(repository.findAll().spliterator(), false)
            .map(PayeeNameMappingService::toPayeeNameMapping)
            .collect(toMap(PayeeNameMapping::originalName, identity()));

        repository.findAllPayeeNamesDistinctFromTransactions().forEach(originalName -> {
            mappingsByOriginalName.putIfAbsent(originalName, new PayeeNameMapping(originalName, ""));
        });

        return mappingsByOriginalName.values()
            .stream()
            .sorted(comparing(PayeeNameMapping::originalName))
            .toList();
    }

    public void updateNameMappings(List<PayeeNameMapping> mappings) {

        final Map<String, PayeeNameMappingEntity> entitiesByOriginalName = stream(repository.findAll().spliterator(), false)
            .collect(toMap(PayeeNameMappingEntity::getOriginalName, identity()));

        final List<PayeeNameMappingEntity> toSave = mappings.stream()
            .map(mapping -> {
                final String originalName = mapping.originalName();
                final PayeeNameMappingEntity entity = entitiesByOriginalName.getOrDefault(originalName, new PayeeNameMappingEntity());
                if (entity.getId() == null) {
                    // don't know why to guard this with id check. maybe just because.
                    entity.setOriginalName(mapping.originalName());
                }
                entity.setMappedName(mapping.mappedName());
                return entity;
            })
            .toList();

        repository.saveAll(toSave);
    }

    private static PayeeNameMapping toPayeeNameMapping(PayeeNameMappingEntity entity) {
        return new PayeeNameMapping(entity.getOriginalName(), entity.getMappedName());
    }
}

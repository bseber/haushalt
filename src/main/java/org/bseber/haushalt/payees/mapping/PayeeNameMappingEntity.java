package org.bseber.haushalt.payees.mapping;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("payee_name_mapping")
class PayeeNameMappingEntity {

    @Id
    private Long id;

    private String originalName;

    private String mappedName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMappedName() {
        return mappedName;
    }

    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayeeNameMappingEntity that = (PayeeNameMappingEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PayeeNameMappingEntity{" +
            "id=" + id +
            ", originalName='" + originalName + '\'' +
            ", mappedName='" + mappedName + '\'' +
            '}';
    }
}

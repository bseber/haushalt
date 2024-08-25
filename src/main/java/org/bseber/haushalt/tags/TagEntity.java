package org.bseber.haushalt.tags;

import org.bseber.haushalt.core.BudgetType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Table("tag")
class TagEntity {

    @Id
    private Long id;

    private String name;
    private String description;
    private BigDecimal budget;
    private BudgetType budgetType;
    private TagColor color;

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public TagColor getColor() {
        return color;
    }

    void setColor(TagColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagEntity tagEntity = (TagEntity) o;
        return Objects.equals(id, tagEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TagEntity{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", budget=" + budget +
            ", budgetType=" + budgetType +
            ", color=" + color +
            '}';
    }
}

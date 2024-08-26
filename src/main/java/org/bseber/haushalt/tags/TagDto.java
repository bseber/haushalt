package org.bseber.haushalt.tags;

import jakarta.validation.constraints.NotNull;
import org.bseber.haushalt.core.BudgetType;

import java.math.BigDecimal;
import java.util.Objects;

class TagDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal budget;
    private BudgetType budgetType;
    private TagColor color;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public TagColor getColor() {
        return color;
    }

    public void setColor(@NotNull TagColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDto tagDto = (TagDto) o;
        return Objects.equals(id, tagDto.id)
            && Objects.equals(name, tagDto.name)
            && Objects.equals(description, tagDto.description)
            && Objects.equals(budget, tagDto.budget)
            && budgetType == tagDto.budgetType
            && color == tagDto.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, budget, budgetType, color);
    }

    @Override
    public String toString() {
        return "TagDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", budget=" + budget +
            ", budgetType=" + budgetType +
            ", color=" + color +
            '}';
    }
}

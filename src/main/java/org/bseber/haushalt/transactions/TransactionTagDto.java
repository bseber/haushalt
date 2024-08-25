package org.bseber.haushalt.transactions;

import org.bseber.haushalt.tags.TagColor;

import java.util.Objects;

class TransactionTagDto {

    private String name;
    private TagColor color;

    public TransactionTagDto(String name, TagColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagColor getColor() {
        return color;
    }

    public void setColor(TagColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTagDto that = (TransactionTagDto) o;
        return Objects.equals(name, that.name) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    @Override
    public String toString() {
        return "TransactionTagDto{" +
            "name='" + name + '\'' +
            ", color=" + color +
            '}';
    }
}

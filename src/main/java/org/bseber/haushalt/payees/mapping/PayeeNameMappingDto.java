package org.bseber.haushalt.payees.mapping;

import java.util.ArrayList;
import java.util.List;

public class PayeeNameMappingDto {

    private List<Mapping> mappings;

    public PayeeNameMappingDto() {
        this(new ArrayList<>());
    }

    public PayeeNameMappingDto(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public static class Mapping {

        private String originalName;
        private String mappedName;

        public Mapping() {
        }

        public Mapping(String originalName, String mappedName) {
            this.originalName = originalName;
            this.mappedName = mappedName;
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
    }
}

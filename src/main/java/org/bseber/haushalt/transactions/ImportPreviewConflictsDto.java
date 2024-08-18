package org.bseber.haushalt.transactions;

import java.util.List;

class ImportPreviewConflictsDto {

    private List<TransactionConflictDto> conflicts;

    ImportPreviewConflictsDto() {
    }

    ImportPreviewConflictsDto(List<TransactionConflictDto> conflicts) {
        this.conflicts = conflicts;
    }

    public List<TransactionConflictDto> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<TransactionConflictDto> conflicts) {
        this.conflicts = conflicts;
    }
}

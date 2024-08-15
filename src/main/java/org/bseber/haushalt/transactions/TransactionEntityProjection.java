package org.bseber.haushalt.transactions;

class TransactionEntityProjection extends TransactionEntity {

    private String mappedPayee;

    public String getMappedPayee() {
        return mappedPayee;
    }

    public void setMappedPayee(String mappedPayee) {
        this.mappedPayee = mappedPayee;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

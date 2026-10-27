package warehouse.interfaces;

import warehouse.model.StockMovement;

/** Writes one immutable audit event. Audit records are append-only. */
public interface Auditable {
    void logMovement(StockMovement movementRecord);
}

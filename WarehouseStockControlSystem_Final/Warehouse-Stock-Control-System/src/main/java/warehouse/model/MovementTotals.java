package warehouse.model;

/** Report-only projection of all audited movement directions for one product. */
public record MovementTotals(String productName, int unitsIn, int unitsOut) { }

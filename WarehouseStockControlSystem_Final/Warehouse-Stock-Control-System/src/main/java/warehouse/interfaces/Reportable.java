package warehouse.interfaces;

import warehouse.model.LowStockAlert;
import warehouse.model.StockMovement;
import warehouse.model.StockSummary;

import java.time.LocalDate;
import java.util.List;

public interface Reportable {
    List<LowStockAlert> getLowStockAlerts();
    List<StockMovement> getStockMovementReport(LocalDate startDate, LocalDate endDate);
    List<StockSummary> getStockSummary();
}

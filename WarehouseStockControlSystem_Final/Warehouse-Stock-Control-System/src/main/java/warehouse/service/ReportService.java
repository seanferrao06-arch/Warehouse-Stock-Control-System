package warehouse.service;

import org.springframework.stereotype.Service;
import warehouse.dao.ReportDAO;
import warehouse.model.*;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final ReportDAO reports;
    public ReportService(ReportDAO reports) { this.reports = reports; }
    public List<LowStockAlert> lowStock() { return reports.getLowStockAlerts(); }
    public List<StockMovement> movements(LocalDate start, LocalDate end) { return reports.getStockMovementReport(start, end); }
    public List<StockSummary> stockSummary() { return reports.getStockSummary(); }
    public List<MovementTotals> movementTotals() { return reports.getMovementTotals(); }
}

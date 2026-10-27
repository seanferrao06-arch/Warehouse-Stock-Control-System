package warehouse.service;

import org.springframework.stereotype.Service;
import warehouse.model.DashboardStats;

import java.time.LocalDate;

@Service
public class DashboardService {
    private final CatalogService catalog;
    private final ReportService reports;
    public DashboardService(CatalogService catalog, ReportService reports) { this.catalog = catalog; this.reports = reports; }
    public DashboardStats stats() {
        return new DashboardStats(catalog.products().size(), reports.lowStock().size(), reports.movements(LocalDate.now(), LocalDate.now()).size());
    }
}

package warehouse.web;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import warehouse.service.ReportService;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final ReportService reports;
    public ReportController(ReportService reports) { this.reports = reports; }
    @GetMapping
    public String reports(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate start, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate end, Model model) {
        LocalDate resolvedEnd = end == null ? LocalDate.now() : end;
        LocalDate resolvedStart = start == null ? resolvedEnd.minusDays(30) : start;
        model.addAttribute("start", resolvedStart); model.addAttribute("end", resolvedEnd);
        model.addAttribute("alerts", reports.lowStock()); model.addAttribute("movements", reports.movements(resolvedStart, resolvedEnd));
        model.addAttribute("stockSummary", reports.stockSummary()); model.addAttribute("movementTotals", reports.movementTotals());
        return "reports";
    }
}

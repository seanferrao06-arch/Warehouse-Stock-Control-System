package warehouse.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import warehouse.service.DashboardService;
import warehouse.service.ReportService;

@Controller
public class HomeController {
    private final DashboardService dashboard;
    private final ReportService reports;
    public HomeController(DashboardService dashboard, ReportService reports) { this.dashboard = dashboard; this.reports = reports; }
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("stats", dashboard.stats());
        model.addAttribute("alerts", reports.lowStock());
        model.addAttribute("username", user.getUsername());
        return "dashboard";
    }
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/access-denied") public String denied() { return "access-denied"; }
}

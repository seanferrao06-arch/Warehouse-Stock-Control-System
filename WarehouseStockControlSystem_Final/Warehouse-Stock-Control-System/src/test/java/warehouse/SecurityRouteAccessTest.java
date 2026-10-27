package warehouse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import warehouse.service.*;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRouteAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private UserService userService;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private SupplierService supplierService;

    @MockBean
    private WarehouseService warehouseService;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void testUnauthenticatedUserRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "staff_user", roles = {"STAFF"})
    void testStaffUserBlockedFromAdminRoutes() throws Exception {
        // Direct URL access to /reports must be blocked with 403 Forbidden
        mockMvc.perform(get("/reports"))
                .andExpect(status().isForbidden());

        // Direct URL access to /users must be blocked with 403 Forbidden
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());

        // Direct URL access to /products must be blocked with 403 Forbidden
        mockMvc.perform(get("/products"))
                .andExpect(status().isForbidden());

        // Direct URL access to /suppliers must be blocked with 403 Forbidden
        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staff_user", roles = {"STAFF"})
    void testStaffUserAllowedAccessToStockAndDashboard() throws Exception {
        when(dashboardService.stats()).thenReturn(new warehouse.model.DashboardStats(0, 0, 0));
        when(reportService.lowStock()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/stock"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    void testAdminUserAllowedAccessToReportsAndUsers() throws Exception {
        when(reportService.lowStock()).thenReturn(Collections.emptyList());
        when(reportService.movements(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());
        when(reportService.stockSummary()).thenReturn(Collections.emptyList());
        when(reportService.movementTotals()).thenReturn(Collections.emptyList());
        when(userService.all()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }
}

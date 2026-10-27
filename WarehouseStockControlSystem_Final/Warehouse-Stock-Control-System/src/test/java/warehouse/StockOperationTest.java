package warehouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import warehouse.dao.StockDAO;
import warehouse.model.MovementType;
import warehouse.model.StockMovement;
import warehouse.util.DatabaseException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StockOperationTest {

    private JdbcTemplate jdbc;
    private StockDAO stockDAO;

    @BeforeEach
    void setUp() {
        jdbc = Mockito.mock(JdbcTemplate.class);
        stockDAO = new StockDAO(jdbc);
    }

    @Test
    void testStockOutThrowsWhenQuantityExceedsAvailableStock() {
        // Mock requireExists for product, location, user
        when(jdbc.query(eq("SELECT 1 FROM product WHERE product_id=?"), any(ResultSetExtractor.class), eq(1)))
                .thenReturn(1);
        when(jdbc.query(eq("SELECT 1 FROM warehouse_location WHERE location_id=?"), any(ResultSetExtractor.class), eq(1)))
                .thenReturn(1);
        when(jdbc.query(eq("SELECT 1 FROM `user` WHERE user_id=?"), any(ResultSetExtractor.class), eq(2)))
                .thenReturn(1);

        // Mock current available quantity = 15 (less than requested 50)
        when(jdbc.query(contains("SELECT quantity FROM current_stock"), any(RowMapper.class), eq(1), eq(1)))
                .thenReturn(List.of(15));

        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            stockDAO.stockOut(1, 1, 2, 50);
        });

        assertTrue(ex.getMessage().contains("Insufficient stock"));
        assertTrue(ex.getMessage().contains("Available quantity: 15"));

        // Verify update was never executed due to overdraft
        verify(jdbc, never()).update(contains("UPDATE current_stock SET quantity=?"), any(), any(), any());
    }

    @Test
    void testStockOutThrowsWhenNoStockRecordExists() {
        // Mock requireExists
        when(jdbc.query(anyString(), any(ResultSetExtractor.class), anyInt()))
                .thenReturn(1);

        // Mock no current_stock entry found
        when(jdbc.query(contains("SELECT quantity FROM current_stock"), any(RowMapper.class), eq(1), eq(1)))
                .thenReturn(Collections.emptyList());

        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            stockDAO.stockOut(1, 1, 2, 10);
        });

        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testStockOutThrowsOnInvalidInput() {
        assertThrows(DatabaseException.class, () -> stockDAO.stockOut(-1, 1, 1, 10));
        assertThrows(DatabaseException.class, () -> stockDAO.stockOut(1, 1, 1, 0));
        assertThrows(DatabaseException.class, () -> stockDAO.stockOut(1, 1, 1, -5));
    }
}

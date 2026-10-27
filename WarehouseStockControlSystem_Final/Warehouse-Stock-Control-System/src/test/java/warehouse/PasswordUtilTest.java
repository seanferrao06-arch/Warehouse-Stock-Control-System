package warehouse;

import org.junit.jupiter.api.Test;
import warehouse.util.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testSha256Hashing() {
        String adminHash = PasswordUtil.sha256("admin123");
        assertNotNull(adminHash);
        assertEquals(64, adminHash.length());

        // Verify SHA256 deterministic output
        assertEquals(adminHash, PasswordUtil.sha256("admin123"));

        String staffHash = PasswordUtil.sha256("staff123");
        assertNotEquals(adminHash, staffHash);
        assertEquals(64, staffHash.length());
    }
}

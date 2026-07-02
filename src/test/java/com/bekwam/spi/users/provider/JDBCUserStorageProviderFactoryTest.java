package com.bekwam.spi.users.provider;

import com.bekwam.spi.users.config.Config;
import com.bekwam.spi.users.config.ConfigMetadataFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JDBCUserStorageProviderFactoryTest {

    @Test
    void testGetId() {
        var factory = new JDBCUserStorageProviderFactory();
        assertEquals("Bekwam JDBC", factory.getId());
    }

    @Test
    void testGetConfigProperties() {
        var factory = new JDBCUserStorageProviderFactory();
        var properties = factory.getConfigProperties();
        assertNotNull(properties);
        assertFalse(properties.isEmpty(), "es sollten Konfigurationseigenschaften vorhanden sein");
    }

    @Test
    void testConfigMetadataFactory() {
        var factory = new ConfigMetadataFactory();
        var metadata = factory.create();
        assertNotNull(metadata);
        assertFalse(metadata.isEmpty());
    }

    @Test
    void testFactoryClose() {
        var factory = new JDBCUserStorageProviderFactory();
        // sollte ohne Fehler schließen (noch keine DataSources)
        assertDoesNotThrow(factory::close);
    }
}

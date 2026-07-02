package com.bekwam.spi.users.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTest {

    @Test
    void testHashFrom() {
        var hash = Hash.from("password123");
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    void testHashConsistency() {
        var hash1 = Hash.from("hello");
        var hash2 = Hash.from("hello");
        assertEquals(hash1, hash2, "gleiche Eingabe sollte gleichen Hash ergeben");
    }

    @Test
    void testHashDifferentInputs() {
        var hash1 = Hash.from("password1");
        var hash2 = Hash.from("password2");
        assertNotEquals(hash1, hash2, "unterschiedliche Eingaben sollten unterschiedliche Hashes ergeben");
    }

    @Test
    void testHashNotEmpty() {
        var hash = Hash.from("");
        assertNotNull(hash);
        assertFalse(hash.isEmpty(), "auch leerer String sollte Hash erzeugen");
    }

    @Test
    void testPasswordEncoder() {
        var encoder = new SHA256PasswordEncoder();
        var encoded = encoder.encode("mypassword");
        assertNotNull(encoded);

        var expected = Hash.from("mypassword");
        assertEquals(expected, encoded);
    }
}

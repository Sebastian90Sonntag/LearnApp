package com.graphicdesigncoding.learnapp;

import com.graphicdesigncoding.learnapp.user.User;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void compareTo_higherScoreIsGreater() {
        User user1 = new User(null, "Alice", "100");
        User user2 = new User(null, "Bob", "50");
        assertTrue(user1.compareTo(user2) > 0);
    }

    @Test
    public void compareTo_lowerScoreIsSmaller() {
        User user1 = new User(null, "Alice", "50");
        User user2 = new User(null, "Bob", "100");
        assertTrue(user1.compareTo(user2) < 0);
    }

    @Test
    public void compareTo_equalScores() {
        User user1 = new User(null, "Alice", "100");
        User user2 = new User(null, "Bob", "100");
        assertEquals(0, user1.compareTo(user2));
    }

    @Test
    public void compareTo_nullScoreReturnsZero() {
        User user1 = new User(null, "Alice", null);
        User user2 = new User(null, "Bob", "100");
        assertEquals(0, user1.compareTo(user2));
    }

    @Test
    public void gettersAndSetters() {
        User user = new User(null, "Alice", "100");
        assertEquals("Alice", user.getUserName());
        assertEquals("100", user.getScore());

        user.setUserName("Bob");
        user.setScore("200");
        assertEquals("Bob", user.getUserName());
        assertEquals("200", user.getScore());
    }
}

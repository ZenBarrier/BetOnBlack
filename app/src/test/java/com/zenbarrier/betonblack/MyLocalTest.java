package com.zenbarrier.betonblack;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Anthony on 3/17/2017.
 * This file is the fragment that holds all the preferences
 */

public class MyLocalTest {
    @Test
    public void testFibonacci() throws Exception {
        assertEquals(2, GameActivity.fibonacci(1, 3));
        assertEquals(20, GameActivity.fibonacci(10, 3));
        assertEquals(3, GameActivity.fibonacci(1, 4));
        assertEquals(5, GameActivity.fibonacci(1, 5));
        assertEquals(8, GameActivity.fibonacci(1, 6));
        assertEquals(34, GameActivity.fibonacci(1, 9));
        assertEquals(340, GameActivity.fibonacci(10, 9));
    }
}

package org.jboss.gwt.flux.sample.calculator;

import java.util.Random;

import org.junit.Test;

public class CalculatorTest {

    @Test
    public void run() {
        int numberOfActions = 5 + new Random().nextInt(5);
        new Calculator(numberOfActions).run();
    }
}
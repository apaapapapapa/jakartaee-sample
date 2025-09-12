package com.example.sample;

import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public final class SampleApplication {
    
    private static final Logger LOGGER = Logger.getLogger(SampleApplication.class.getName());

    private SampleApplication() {
    }

    public static void main(final String[] args) {
        LOGGER.info("Hello World!");
    }
}

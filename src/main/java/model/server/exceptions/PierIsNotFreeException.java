package model.server.exceptions;

import org.apache.log4j.Logger;

/**
 * @author Ilya Ivanov
 */
public class PierIsNotFreeException extends RuntimeException {
    public PierIsNotFreeException(String message) {
        super(message);
    }
}

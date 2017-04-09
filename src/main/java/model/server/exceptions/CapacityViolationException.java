package model.server.exceptions;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

/**
 * @author Ilya Ivanov
 */
public class CapacityViolationException extends RemoteException {
    /**
     * {@inheritDoc}
     */
    public CapacityViolationException(String s) {
        super(s);
    }
}

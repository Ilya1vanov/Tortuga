package model.server.exceptions;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

/**
 * @author Ilya Ivanov
 */
public class NotInServiceException extends RemoteException {
    public NotInServiceException(String s) {
        super(s);
    }
}

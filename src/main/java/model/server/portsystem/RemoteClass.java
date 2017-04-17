package model.server.portsystem;

import com.google.common.collect.ImmutableCollection;
import gson.GSONExclude;
import org.apache.log4j.Logger;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Ilya Ivanov
 */
public abstract class RemoteClass implements Remote {
    @GSONExclude
    Collection<? extends RemoteClass> remotes = Collections.EMPTY_LIST;

    public RemoteClass() {}

    public RemoteClass(Collection<? extends RemoteClass> remotes) {
        this.remotes = remotes;
    }

    public void setRemotes(Collection<? extends RemoteClass> remotes) {
        this.remotes = remotes;
    }

    public final Remote exportRemote() throws RemoteException {
        final Remote remote1 = UnicastRemoteObject.exportObject(this, 0);
        for (RemoteClass remote : remotes)
            remote.exportRemote();
        return remote1;
    }

    public final void unexportRemote() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
        for (RemoteClass remote : remotes)
            remote.unexportRemote();
    }
}

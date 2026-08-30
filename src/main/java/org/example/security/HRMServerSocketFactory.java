package org.example.security;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

/**
 * HRMServerSocketFactory — wraps an SSLServerSocketFactory so that
 * the RMI runtime creates TLS-encrypted server sockets instead of
 * plain TCP sockets.
 *
 * Passed to UnicastRemoteObject.exportObject() on the server side.
 */
public class HRMServerSocketFactory implements RMIServerSocketFactory, Serializable {

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        try {
            SSLServerSocketFactory factory = SSLConfig.getServerSSLContext().getServerSocketFactory();
            SSLServerSocket socket = (SSLServerSocket) factory.createServerSocket(port);
            // Only allow strong TLS protocol versions
            socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
            return socket;
        } catch (Exception e) {
            throw new IOException("Failed to create SSL server socket: " + e.getMessage(), e);
        }
    }
}

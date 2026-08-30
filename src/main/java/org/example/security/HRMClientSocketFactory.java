package org.example.security;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

/**
 * HRMClientSocketFactory — wraps an SSLSocketFactory so that
 * the RMI runtime creates TLS-encrypted client sockets instead of
 * plain TCP sockets.
 *
 * Passed to UnicastRemoteObject.exportObject() on the server side
 * and also used by the client when looking up the registry.
 */
public class HRMClientSocketFactory implements RMIClientSocketFactory, Serializable {

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        try {
            SSLSocketFactory factory = SSLConfig.getClientSSLContext().getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
            // Only allow strong TLS protocol versions
            socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
            return socket;
        } catch (Exception e) {
            throw new IOException("Failed to create SSL client socket: " + e.getMessage(), e);
        }
    }
}

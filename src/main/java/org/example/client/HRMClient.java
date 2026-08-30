package org.example.client;

import org.example.remote.HRMService;
import org.example.security.HRMClientSocketFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HRMClient {

    private static HRMService service;

    public static HRMService getService() {
        try {
            if (service == null) {
                // Connect to the SSL-secured RMI registry
                Registry registry = LocateRegistry.getRegistry(
                        "localhost",
                        1099,
                        new HRMClientSocketFactory()  // use SSL to reach the registry
                );
                service = (HRMService) registry.lookup("HRMService");
                System.out.println("[SSL]  Connected to HRMService over TLS.");
            }
            return service;
        } catch (Exception e) {
            System.out.println("Failed to connect to RMI server (SSL).");
            e.printStackTrace();
            return null;
        }
    }
}

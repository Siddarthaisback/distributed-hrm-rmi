package org.example.server;

import org.example.security.HRMClientSocketFactory;
import org.example.security.HRMServerSocketFactory;
import org.example.utils.DatabaseInitializer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * HRM RMI Server entry point — now secured with TLS/SSL.
 * All communication between client and server is encrypted.
 */
public class HRMServer {

    public static void main(String[] args) {
        try {
            System.out.println("==============================================");
            System.out.println("  HRM RMI Server  |  Starting (SSL mode)...");
            System.out.println("==============================================");

            // Step 1 — Initialise DB schema
            System.out.println("[INIT]  Invoking DatabaseInitializer.initializeDatabase()...");
            DatabaseInitializer.initializeDatabase();
            System.out.println("[OK]    Database schema verified — hrm_system ready.");

            // Step 2 — Create SSL-secured RMI registry on port 1099
            System.out.println("[INIT]  Creating SSL RMI registry on port 1099...");
            Registry registry = LocateRegistry.createRegistry(
                    1099,
                    new HRMClientSocketFactory(),   // clients connect via SSL
                    new HRMServerSocketFactory()    // server listens via SSL
            );
            System.out.println("[OK]    SSL RMI Registry created on port 1099");

            // Step 3 — Instantiate service implementation (exports itself over SSL)
            System.out.println("[INIT]  Instantiating HRMServiceImpl with SSL socket factories...");
            HRMServiceImpl impl = new HRMServiceImpl();

            // Step 4 — Bind to registry
            registry.rebind("HRMService", impl);
            System.out.println("[OK]    HRMServiceImpl bound → registry.rebind(\"HRMService\", impl)");
            System.out.println("[OK]    HRM RMI Server is running with TLS encryption.");
            System.out.println("[INFO]  Listening at: rmi://localhost:1099/HRMService (SSL)");
            System.out.println("==============================================");

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to start HRM RMI Server:");
            e.printStackTrace();
        }
    }
}


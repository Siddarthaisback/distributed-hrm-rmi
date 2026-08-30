package org.example.server;

import org.example.remote.HRMService;

/**
 * ServerManager — holds the single live HRMServiceImpl instance so that
 * any server-side UI panel can call it directly without going through RMI.
 *
 * Set once in HRMServer after the service is created:
 *   ServerManager.setService(service);
 *
 * Then access anywhere in the server package:
 *   HRMService svc = ServerManager.getService();
 */
public class ServerManager {

    private static HRMService service;

    public static void setService(HRMService svc) {
        service = svc;
    }

    public static HRMService getService() {
        return service;
    }
}

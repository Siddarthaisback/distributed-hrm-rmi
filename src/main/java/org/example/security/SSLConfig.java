package org.example.security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class SSLConfig {

    private static final char[] PASSWORD = "hrmpassword".toCharArray();

    private static InputStream openResource(String fileName) throws Exception {

        // 1. Classpath (works after mvn compile copies resources to target/classes)
        InputStream is = SSLConfig.class.getResourceAsStream("/ssl/" + fileName);
        if (is != null) return is;

        // 2. target/classes/ssl/ — IntelliJ output folder
        String base = SSLConfig.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
        // base is something like: .../target/classes/
        // so we look inside it for ssl/
        File f1 = new File(base + "ssl/" + fileName);
        if (f1.exists()) return new FileInputStream(f1);

        // 3. Walk up from target/classes to project root, then into src/main/resources/ssl
        File classesDir = new File(base);
        File projectRoot = classesDir.getParentFile() // target/
                .getParentFile(); // project root
        File f2 = new File(projectRoot, "src/main/resources/ssl/" + fileName);
        if (f2.exists()) return new FileInputStream(f2);

        // 4. Working directory (wherever the JVM was launched from)
        File f3 = new File("src/main/resources/ssl/" + fileName);
        if (f3.exists()) return new FileInputStream(f3);

        throw new RuntimeException(
                "\n\n  *** SSL keystore not found: " + fileName + " ***\n" +
                        "  Looked in:\n" +
                        "    - classpath: /ssl/" + fileName + "\n" +
                        "    - " + f1.getAbsolutePath() + "\n" +
                        "    - " + f2.getAbsolutePath() + "\n" +
                        "    - " + f3.getAbsolutePath() + "\n\n" +
                        "  FIX: In IntelliJ go to Build → Rebuild Project, then run the server again.\n"
        );
    }

    public static SSLContext getServerSSLContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream is = openResource("hrmserver.keystore")) {
            keyStore.load(is, PASSWORD);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, PASSWORD);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), null, null);
        return ctx;
    }

    public static SSLContext getClientSSLContext() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream is = openResource("hrmclient.truststore")) {
            trustStore.load(is, PASSWORD);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);
        return ctx;
    }
}
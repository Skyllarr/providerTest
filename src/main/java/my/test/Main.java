package my.test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class Main {

    private static final String CONFIG_FILE = "/home/skylar/work/projects/providerTest/src/main/resources/META-INF/wildfly-config.xml";

    public static void main(String[] args) {

//        Security.insertProviderAt(new ElytronClientDefaultSSLContextProvider(CONFIG_FILE), 1);
//        Security.insertProviderAt(new ElytronClientDefaultSSLContextProvider(), 1);
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostName, SSLSession session) {
                return true;
            }
        };

        if (Security.getProvider("ElytronClientDefaultSSLContextProvider") == null) {
            throw new RuntimeException("ElytronClientDefaultSSLContextProvider is not registered");
        }

        try {
            SSLContext defaultSSLContext = SSLContext.getDefault();
            ClientBuilder builder = ClientBuilder.newBuilder();
            Client client = builder.sslContext(SSLContext.getDefault()).hostnameVerifier(hostnameVerifier).build();

            Response response = client.target("https://localhost:8443").request().get();

            if(!Integer.toString(200).equals(Integer.toString(response.getStatus()))) {
                throw new RuntimeException("Request was not successful");
            }

            if(!SSLContext.getDefault().getProvider().getName().equals(Security.getProvider("ElytronClientDefaultSSLContextProvider").getName())) {
                throw new RuntimeException("SSLContext's provider is not ElytronClientDefaultSSLContextProvider provider");
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getCause());
            throw new RuntimeException("SSLContext.getDefault threw NoSuchAlgorithmException");
        }
    }
}

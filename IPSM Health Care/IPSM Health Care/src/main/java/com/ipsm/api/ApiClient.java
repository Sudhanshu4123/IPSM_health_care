package com.ipsm.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipsm.UserSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    // Change this to your Cloud/Server URL when deploying in Production
    // E.g. "http://34.123.45.67:8080"
    // Base URL loaded from config or default
    private static String BASE_URL = "http://72.61.253.79:8080";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static String cachedAuthHeader = null;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        java.io.File configFile = new java.io.File("config.properties");
        if (configFile.exists()) {
            try (java.io.InputStream is = new java.io.FileInputStream(configFile)) {
                java.util.Properties props = new java.util.Properties();
                props.load(is);
                String url = props.getProperty("server.url");
                if (url != null && !url.isEmpty()) {
                    // Remove trailing slash if present for consistency
                    if (url.endsWith("/"))
                        url = url.substring(0, url.length() - 1);
                    BASE_URL = url;
                    System.out.println("Loaded Server URL from config: " + BASE_URL);
                }
            } catch (Exception e) {
                System.err.println("Failed to load config.properties: " + e.getMessage());
            }
        } else {
            System.out.println("No config.properties found. Using Default URL: " + BASE_URL);
            createDefaultConfig();
        }
    }

    private static void createDefaultConfig() {
        try {
            java.io.File configFile = new java.io.File("config.properties");
            if (!configFile.exists()) {
                // Don't overwrite if exists (though we are in else block)
                try (java.io.FileWriter fw = new java.io.FileWriter(configFile)) {
                    fw.write("# IPSM Health Care Client Configuration\n");
                    fw.write("server.url=http://localhost:8080\n");
                }
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    public static UserSession login(String username, String password) throws IOException, InterruptedException {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);

        String json = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Cache credentials for subsequent requests
            String authData = username + ":" + password;
            String encodedAuth = java.util.Base64.getEncoder()
                    .encodeToString(authData.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            cachedAuthHeader = "Basic " + encodedAuth;

            // Map the JSON response directly to UserSession
            return mapper.readValue(response.body(), UserSession.class);
        } else {
            // Throw exception with detailed server error
            throw new IOException("Server Error (" + response.statusCode() + "): " + response.body());
        }
    }

    public static boolean changePassword(String username, String oldPassword, String newPassword)
            throws IOException, InterruptedException {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("oldPassword", oldPassword);
        payload.put("newPassword", newPassword);

        String json = mapper.writeValueAsString(payload);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/change-password"))
                .header("Content-Type", "application/json");

        if (cachedAuthHeader != null)
            builder.header("Authorization", cachedAuthHeader);

        HttpRequest request = builder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Update cached credentials if admin changed their own password?
            // Usually change-password needs re-login for safety or update cache.
            // If self-service, update cache.
            // For now, assuming admin changing others or self. If self, strictly should
            // re-login.
            return true;
        } else {
            return false;
        }
    }

    // --- User Management API ---

    public static java.util.List<com.ipsm.model.User> getAllUsers() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users"));

        if (cachedAuthHeader != null)
            builder.header("Authorization", cachedAuthHeader);

        HttpRequest request = builder.GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(),
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.ipsm.model.User>>() {
                    });
        } else {
            throw new IOException("Failed to fetch users: " + response.statusCode());
        }
    }

    public static boolean addUser(com.ipsm.model.User user) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(user);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users/register"))
                .header("Content-Type", "application/json");

        if (cachedAuthHeader != null)
            builder.header("Authorization", cachedAuthHeader);

        HttpRequest request = builder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public static boolean updateUser(com.ipsm.model.User user) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(user);

        HttpRequest.Builder builder = HttpRequest.newBuilder() // Use PUT with username in path
                .uri(URI.create(BASE_URL + "/api/users/" + user.getUsername()))
                .header("Content-Type", "application/json");

        if (cachedAuthHeader != null)
            builder.header("Authorization", cachedAuthHeader);

        HttpRequest request = builder
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public static boolean deleteUser(String username) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users/" + username));

        if (cachedAuthHeader != null)
            builder.header("Authorization", cachedAuthHeader);

        HttpRequest request = builder.DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    public static boolean checkServerHealth() {
        try {
            // Simple ping to backend
            // Ideally create a /health endpoint in Spring Boot
            // For now, we just check if we can reach the base URL or login endpoint without
            // crashing network
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/auth/login")) // Using login as ping for now (405 or 400 is fine
                                                                   // implies reachability)
                    .method("OPTIONS", HttpRequest.BodyPublishers.noBody()) // or simple GET if allowed
                    .build();

            // Just checking connectivity, status doesn't matter much as long as we get a
            // response
            client.send(request, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

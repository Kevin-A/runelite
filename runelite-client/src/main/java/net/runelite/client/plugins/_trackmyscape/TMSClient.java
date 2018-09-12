package net.runelite.client.plugins._trackmyscape;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

@Slf4j
public class TMSClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String HOSTNAME = "track-my-scape-staging.herokuapp.com/runelite";

    private UUID uid;

    public TMSClient() {}

    public TMSClient(UUID uid) {
        this.uid = uid;
    }

    public boolean isSessionActive() {
        Request request = new Request.Builder()
                .header("tms_auth", uid.toString())
                .url("https://" + HOSTNAME + "/check-session")
                .build();

        try (Response response = RuneLiteAPI.CLIENT.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            log.debug("Unable to verify the session for uid {}", uid, e);
            return true;
        }
    }

    public LoginResponse login(String email, String password) throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        RequestBody body = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url("https://" + HOSTNAME + "/login")
                .method("POST", body)
                .build();

        try (Response response = RuneLiteAPI.CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("Login was not successful");
            }

            InputStream in = response.body().byteStream();
            return RuneLiteAPI.GSON.fromJson(new InputStreamReader(in), LoginResponse.class);
        } catch (Exception e) {
            log.error("Could not log in as: {}", email);
            throw e;
        }
    }

    void logout() throws IOException {
        if (uid == null) {
            log.warn("tried logging out without session uid");
        }
        Request request = new Request.Builder()
                .header("tms_auth", uid.toString())
                .url("https://track-my-scape-staging.herokuapp.com/runelite/logout")
                .build();

        try (Response response = RuneLiteAPI.CLIENT.newCall(request).execute()) {
            log.debug("Sent logout request");
        } catch(IOException e) {
            log.error("Could not log out", e);
            throw e;
        }
    }
}
package hu.blog.megosztanam.filter;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import hu.blog.megosztanam.model.shared.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleVerifier(@Value("${google.oauth.client.id}") String clientId) {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public Optional<User> parseToken(String token) {
        if(token == null || token.isEmpty()) {
            System.out.println("Token must not be empty");
            return Optional.empty();
        }
        try {
            GoogleIdToken idToken;
            idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                User user = new User();
                System.out.println("GoogleId:" + payload.getSubject());
                user.setGoogleId(payload.getSubject());
                return Optional.of(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();


    }
}

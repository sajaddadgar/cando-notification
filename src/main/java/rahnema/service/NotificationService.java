package rahnema.service;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import rahnema.config.StompPrincipal;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class NotificationService {

    private Set<StompPrincipal> principals = new HashSet<>();

    public Optional<StompPrincipal> getUsernameFromId(String id) {
        return principals.stream().filter(principal ->
                principal.getUserId().equals(id)).findAny();
    }


    public void connectSession(SessionConnectEvent event) {
        StompPrincipal user = (StompPrincipal) event.getUser();
        StompHeaderAccessor wrap = StompHeaderAccessor.wrap(event.getMessage());
        String passcode = wrap.getPasscode();
        assert user != null;
        user.setUserId(passcode);
        principals.forEach(principal -> {
            if (principal.getUserId().equals(passcode))
                principals.remove(principal);
        });
        principals.add(user);
    }
}
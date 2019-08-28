package rahnema.config;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private String username;
    private String userId;

    public StompPrincipal(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return username;
    }
}

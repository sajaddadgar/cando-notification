package rahnema.config;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private String username;
    private String email;

    public StompPrincipal(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getName() {
        return username;
    }
}

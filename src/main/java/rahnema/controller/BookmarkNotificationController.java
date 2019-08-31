package rahnema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import rahnema.domain.BookmarkDomain;
import rahnema.model.BookmarkNotificaion;
import rahnema.service.NotificationService;

@RestController
@RequestMapping("/notification")
public class BookmarkNotificationController {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    private NotificationService service;

    @Value("${user.token}")
    private String token;

    @PostMapping("/bookmark")
    public void setNotification(@RequestBody BookmarkDomain bookmarkDomain) {
        new BookmarkNotificaion(bookmarkDomain, String.valueOf(bookmarkDomain.getEmail()), template, service, service.getScheduler()).setTokenString(token).set();
    }

    @PostMapping("/cancel")
    public void cancelNotification(@RequestBody BookmarkDomain bookmarkDomain) {
        service.cancelJob(String.valueOf(bookmarkDomain.getEmail()), bookmarkDomain);
    }

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        service.connectSession(event);
    }
}

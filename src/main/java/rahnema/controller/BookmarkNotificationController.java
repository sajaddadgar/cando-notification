package rahnema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import rahnema.config.StompPrincipal;
import rahnema.domain.BookmarkDomain;
import rahnema.model.BookmarkNotificaion;
import rahnema.service.NotificationService;

import java.util.Date;

@RestController
@RequestMapping("/notification")
public class BookmarkNotificationController {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    private NotificationService service;

    @Value("${user.token}")
    private String token;


    @MessageMapping("/bookmark")
    public void setNotification(@RequestBody BookmarkDomain bookmarkDomain, StompPrincipal principal) {
        bookmarkDomain = new BookmarkDomain().setAuctionId(1).setDueDate(new Date().getTime() + 10000);
        new BookmarkNotificaion(bookmarkDomain, principal.getUserId(), template, service, service.getScheduler()).setTokenString(token).set();
    }

    @MessageMapping("/cancel")
    public void cancelNotification(@RequestBody BookmarkDomain bookmarkDomain, StompPrincipal principal) {
        bookmarkDomain = new BookmarkDomain().setAuctionId(1).setDueDate(new Date().getTime() + 10000);
        service.cancelJob(principal.getUserId(), bookmarkDomain);
    }

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        service.connectSession(event);
    }
}

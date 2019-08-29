package rahnema.model;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.client.RestTemplate;
import rahnema.config.StompPrincipal;
import rahnema.domain.BookmarkDomain;
import rahnema.service.NotificationService;
import rahnema.util.SendNotificationJob;

import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class BookmarkNotificaion implements INotification {

    private SimpMessagingTemplate messagingTemplate;
    private NotificationService notificationService;
    private BookmarkDomain bookmarkDomain;
    private String userId;
    private String tokenString;


    public BookmarkNotificaion(BookmarkDomain bookmarkDomain,
                               String userId,
                               SimpMessagingTemplate messagingTemplate,
                               NotificationService notificationService) {
        this.bookmarkDomain = bookmarkDomain;
        this.userId = userId;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    public BookmarkNotificaion setTokenString(String tokenString) {
        this.tokenString = tokenString;
        return this;
    }

    @Override
    public void send() {
        StompPrincipal stompPrincipal = notificationService.getUsernameFromId(userId).orElseThrow(IllegalArgumentException::new);
        String url = "http://localhost:8080/auction/notification/" + bookmarkDomain.getAuctionId();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tokenString);
        HttpEntity<String> request = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        messagingTemplate.convertAndSendToUser(stompPrincipal.getName(), "/notification", response.getBody());
    }

    @Override
    public void set() {
        JobDetail jobDetail = getJobDetail();
        Trigger trigger = getTrigger();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private Trigger getTrigger() {
        return TriggerBuilder.
                newTrigger().withIdentity("trigger")
                .startAt(new Date(bookmarkDomain.getDueDate()))
                .withSchedule(simpleSchedule().withIntervalInSeconds(1))
                .build();
    }

    private JobDetail getJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("user", userId);
        jobDataMap.put("domain", bookmarkDomain);
        jobDataMap.put("service", notificationService);
        jobDataMap.put("template", messagingTemplate);
        jobDataMap.put("token", tokenString);

        return JobBuilder.
                newJob(SendNotificationJob.class)
                .withIdentity("notification")
                .usingJobData(jobDataMap).build();
    }

}

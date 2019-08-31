package rahnema.model;

import org.quartz.*;
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
    private String email;
    private String tokenString;
    private Scheduler jobScheduler;


    public BookmarkNotificaion(BookmarkDomain bookmarkDomain,
                               String email,
                               SimpMessagingTemplate messagingTemplate,
                               NotificationService notificationService,
                               Scheduler jobScheduler) {
        this.bookmarkDomain = bookmarkDomain;
        this.email = email;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.jobScheduler = jobScheduler;
    }

    public BookmarkNotificaion setTokenString(String tokenString) {
        this.tokenString = tokenString;
        return this;
    }

    @Override
    public void send() {
        System.out.println("sending ...");
        StompPrincipal stompPrincipal = notificationService.getUsernameFromEmail(email).orElseThrow(IllegalArgumentException::new);
        String url = "http://localhost:8080/auction/notification/" + bookmarkDomain.getAuctionId();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + tokenString);
        HttpEntity<String> request = new HttpEntity<String>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        messagingTemplate.convertAndSendToUser(stompPrincipal.getName(), "/notification", response.getBody());
        System.out.println("sent to " + stompPrincipal.getEmail() + "as " + stompPrincipal.getName());
    }

    @Override
    public void set() {
        JobDetail jobDetail = getJobDetail();
        System.out.println("new job received");
        Trigger trigger = getTrigger();
        try {
            notificationService.addJob(jobDetail);
            jobScheduler.scheduleJob(jobDetail, trigger);
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
        jobDataMap.put("user", email);
        jobDataMap.put("domain", bookmarkDomain);
        jobDataMap.put("service", notificationService);
        jobDataMap.put("template", messagingTemplate);
        jobDataMap.put("token", tokenString);
        jobDataMap.put("scheduler", jobScheduler);

        return JobBuilder.
                newJob(SendNotificationJob.class)
                .withIdentity("notification")
                .usingJobData(jobDataMap).build();
    }

}

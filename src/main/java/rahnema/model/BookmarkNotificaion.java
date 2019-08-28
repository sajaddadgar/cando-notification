package rahnema.model;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rahnema.config.StompPrincipal;
import rahnema.domain.BookmarkDomain;
import rahnema.service.NotificationService;
import rahnema.util.SendNotifJob;

import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class BookmarkNotificaion implements INotification {

    SimpMessagingTemplate messagingTemplate;
    NotificationService notificationService;
    BookmarkDomain bookmarkDomain;
    String userId;


    public BookmarkNotificaion(BookmarkDomain bookmarkDomain, String userId, SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.bookmarkDomain = bookmarkDomain;
        this.userId = userId;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Override
    public void send() {
        StompPrincipal stompPrincipal = notificationService.getUsernameFromId(userId).orElseThrow(IllegalArgumentException::new);
        messagingTemplate.convertAndSendToUser(stompPrincipal.getName(), "/notification", "salam");
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

        return JobBuilder.
                newJob(SendNotifJob.class)
                .withIdentity("notification")
                .usingJobData(jobDataMap).build();
    }

}

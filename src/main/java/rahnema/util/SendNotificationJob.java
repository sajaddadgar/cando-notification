package rahnema.util;

import org.quartz.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rahnema.domain.BookmarkDomain;
import rahnema.model.BookmarkNotificaion;
import rahnema.service.NotificationService;

public class SendNotificationJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String userId = (String) jobDataMap.get("user");
        BookmarkDomain bookmarkDomain = (BookmarkDomain) jobDataMap.get("domain");
        NotificationService notificationService = (NotificationService) jobDataMap.get("service");
        SimpMessagingTemplate template = (SimpMessagingTemplate) jobDataMap.get("template");
        String token = (String) jobDataMap.get("token");
        Scheduler scheduler = (Scheduler) jobDataMap.get("scheduler");
        new BookmarkNotificaion(bookmarkDomain, userId, template, notificationService, scheduler).setTokenString(token).send();
    }
}

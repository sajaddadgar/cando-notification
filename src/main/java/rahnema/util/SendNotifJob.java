package rahnema.util;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rahnema.domain.BookmarkDomain;
import rahnema.model.BookmarkNotificaion;
import rahnema.service.NotificationService;

public class SendNotifJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String userId= (String) jobDataMap.get("user");
        BookmarkDomain bookmarkDomain = (BookmarkDomain) jobDataMap.get("domain");
        NotificationService notificationService = (NotificationService) jobDataMap.get("service");
        SimpMessagingTemplate template = (SimpMessagingTemplate) jobDataMap.get("template");
        new BookmarkNotificaion(bookmarkDomain, userId,template,notificationService).send();
    }
}

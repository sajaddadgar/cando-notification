package rahnema.service;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import rahnema.config.StompPrincipal;
import rahnema.domain.BookmarkDomain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class NotificationService {

    private Set<StompPrincipal> principals = new HashSet<>();
    private Set<JobDetail> jobDetails = new HashSet<>();
    private Scheduler scheduler;

    {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Optional<StompPrincipal> getUsernameFromId(String id) {
        return principals.stream().filter(principal ->
                principal.getUserId().equals(id)).findAny();
    }

    public void addJob(JobDetail jobDetail) {
        jobDetails.add(jobDetail);
    }

    public void cancelJob(String userId, BookmarkDomain domain) {
        jobDetails.forEach(jobDetail -> {
            String filteredUserId = (String) jobDetail.getJobDataMap().get("user");
            BookmarkDomain filteredDomain = (BookmarkDomain) jobDetail.getJobDataMap().get("domain");
            if (filteredDomain.getAuctionId() == domain.getAuctionId() && userId.equals(filteredUserId)) {
                try {
                    scheduler.deleteJob(jobDetail.getKey());
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        });
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
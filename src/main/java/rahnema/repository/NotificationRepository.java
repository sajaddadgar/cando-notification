package rahnema.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rahnema.model.INotification;

@Repository
public interface NotificationRepository extends CrudRepository<INotification, Long> {
}

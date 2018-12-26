package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.AnonymousAnswer;

public interface AnonymousAnswerRepository extends CrudRepository<AnonymousAnswer, Long> {
}

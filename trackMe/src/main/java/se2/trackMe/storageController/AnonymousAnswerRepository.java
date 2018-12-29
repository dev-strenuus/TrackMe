package se2.trackMe.storageController;

import org.springframework.data.repository.CrudRepository;
import se2.trackMe.model.AnonymousAnswer;
import se2.trackMe.model.AnonymousRequest;

import java.util.Date;
import java.util.List;

public interface AnonymousAnswerRepository extends CrudRepository<AnonymousAnswer, Long> {
    List<AnonymousAnswer> findAllByAnonymousRequest(AnonymousRequest anonymousRequest);
}

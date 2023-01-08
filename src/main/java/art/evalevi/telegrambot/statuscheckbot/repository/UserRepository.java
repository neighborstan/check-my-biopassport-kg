package art.evalevi.telegrambot.statuscheckbot.repository;

import art.evalevi.telegrambot.statuscheckbot.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}

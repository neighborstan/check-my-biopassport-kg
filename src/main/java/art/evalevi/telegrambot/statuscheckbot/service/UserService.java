package art.evalevi.telegrambot.statuscheckbot.service;


import art.evalevi.telegrambot.statuscheckbot.entity.User;

import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> findByChatId(Long chatId);
}

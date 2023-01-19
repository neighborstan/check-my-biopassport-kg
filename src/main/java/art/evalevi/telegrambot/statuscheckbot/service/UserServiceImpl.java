package art.evalevi.telegrambot.statuscheckbot.service;

import art.evalevi.telegrambot.statuscheckbot.entity.User;
import art.evalevi.telegrambot.statuscheckbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return userRepository.findById(chatId);
    }
}

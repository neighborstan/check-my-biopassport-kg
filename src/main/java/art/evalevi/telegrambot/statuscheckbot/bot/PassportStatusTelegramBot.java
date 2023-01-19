package art.evalevi.telegrambot.statuscheckbot.bot;

import art.evalevi.telegrambot.statuscheckbot.command.CommandIdentifier;
import art.evalevi.telegrambot.statuscheckbot.entity.User;
import art.evalevi.telegrambot.statuscheckbot.service.PassportService;
import art.evalevi.telegrambot.statuscheckbot.service.SendMessageServiceImpl;
import art.evalevi.telegrambot.statuscheckbot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.*;

@Slf4j
@Component
public class PassportStatusTelegramBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final CommandIdentifier commandIdentifier;

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Autowired
    public PassportStatusTelegramBot(PassportService passportService, UserService userService) {
        this.userService = userService;
        commandIdentifier = new CommandIdentifier(new SendMessageServiceImpl(this, new Keyboard()), passportService);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = getChatId(update);
            String textRequest = getMessage(update);
            String firstName = getFirstName(update);
            String lastName = getLastName(update);
            String username = getUserName(update);
            String nameToSendUser = firstName.isEmpty() ? username : firstName;

            commandIdentifier.findCommand(textRequest).execute(update);
            String selectedCity = commandIdentifier.getSelectedCity();

            userService.findByChatId(chatId).ifPresentOrElse(
                    user -> {
                        Integer requestsCount = user.getRequestsCount();
                        user.setRequestsCount(++requestsCount);
                        user.setLastRequest(textRequest);
                        user.setSelectedCity(selectedCity);
                        userService.save(user);
                        log.info(String.format("User %s updated", nameToSendUser));
                    },
                    () -> {
                        User user = new User();
                        user.setChatId(chatId);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setUserName(username);
                        user.setRequestsCount(1);
                        user.setLastRequest(textRequest);
                        user.setSelectedCity(selectedCity);
                        userService.save(user);
                        log.info(String.format("User %s created", nameToSendUser));
                    });
        }
    }
}

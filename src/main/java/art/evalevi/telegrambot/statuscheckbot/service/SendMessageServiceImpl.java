package art.evalevi.telegrambot.statuscheckbot.service;

import art.evalevi.telegrambot.statuscheckbot.bot.Keyboard;
import art.evalevi.telegrambot.statuscheckbot.bot.PassportStatusTelegramBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Getter
@Service
public class SendMessageServiceImpl implements SendMessageService {

    private final PassportStatusTelegramBot telegramBot;
    private final Keyboard keyboard;

    @Autowired
    public SendMessageServiceImpl(PassportStatusTelegramBot telegramBot, Keyboard keyboard) {
        this.telegramBot = telegramBot;
        this.keyboard = keyboard;
    }

    @Override
    public void sendMessage(Long chatId, String message) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);

        sendMessage.setReplyMarkup(keyboard.getKeyboardMarkup());
        sendMessage.enableHtml(true);

        try {
            telegramBot.execute(sendMessage);
            log.info("A message is sent");
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e);
            e.printStackTrace();
        }
    }

}

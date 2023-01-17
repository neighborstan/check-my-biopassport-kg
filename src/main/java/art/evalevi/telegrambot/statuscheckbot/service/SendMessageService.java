package art.evalevi.telegrambot.statuscheckbot.service;

public interface SendMessageService {

    void sendMessage(Long chatId, String message);
}

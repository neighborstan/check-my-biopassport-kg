package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.getChatId;

public class UnknownCommand implements Command {

    private static final String NOT_FOUND_WARNING_TEXT = "<code>Номер не найден </code>" + "\uD83D\uDE15";

    private final SendMessageService sendMessageService;

    public UnknownCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(getChatId(update), NOT_FOUND_WARNING_TEXT);
    }
}

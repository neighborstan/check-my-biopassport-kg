package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.getChatId;

public class ContactCommand implements Command {

    private static final String CONTACT_TEXT = "<code>Для связи пишите в лс</code> @neighbor";

    private final SendMessageService sendMessageService;

    public ContactCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(getChatId(update), CONTACT_TEXT);
    }
}

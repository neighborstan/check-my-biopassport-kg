package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.getChatId;

public class HelpCommand implements Command {

    private static final String HELP_TEXT = """
            ◾ <a href="https://bishkek.kdmid.ru/queue/Default.aspx">Запись на прием (Бишкек)</a>
            ◾ <a href="https://osh.kdmid.ru/queue/Default.aspx">Запись на прием (Ош)</a>
            """;

    private final SendMessageService sendMessageService;

    public HelpCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(getChatId(update), HELP_TEXT);
    }
}

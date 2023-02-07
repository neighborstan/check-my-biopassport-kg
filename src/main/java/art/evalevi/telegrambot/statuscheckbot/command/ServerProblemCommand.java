package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.getChatId;

public class ServerProblemCommand implements Command {
    private static final String SERVER_PROBLEM_WARNING_TEXT =
            """
            <code>Не могу получить ответ от сервера с данными статусов, похоже с ним что-то случилось </code> \uD83D\uDE15\s

            <code>Повторите попытку чуть позже либо попробуйте сделать запрос напрямую через сайт </code><a href='https://info.midpass.ru'>info.midpass.ru</a>
            """;

    private final SendMessageService sendMessageService;

    public ServerProblemCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(getChatId(update), SERVER_PROBLEM_WARNING_TEXT);
    }
}

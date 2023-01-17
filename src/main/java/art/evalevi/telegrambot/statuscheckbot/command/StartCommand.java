package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.*;

public class StartCommand implements Command {

    private static final String START_TEXT = """
            <code><b>%s</b>, саламатсызбы, как говорится!

            Чтобы получить информацию о текущем статусе биометрического загранпаспорта отправьте короткий номер заявления, не более 8 цифр или полный идентификатор заявления - 25 цифр.
            
            Например, следующие запросы относятся к одному заявлению:
            2000996012018071700012345
            00012345
            12345
           
            Запрос выполняется через сервис info.midpass.ru

            Выберите город, где было подано заявление</code>""";

    private final SendMessageService sendMessageService;

    public StartCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        String firstName = getFirstName(update);
        String username = getUserName(update);
        String nameToSendUser = firstName.isEmpty() ? username : firstName;

        sendMessageService.sendMessage(getChatId(update), String.format(START_TEXT, nameToSendUser));
    }
}

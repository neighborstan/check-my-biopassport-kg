package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.bot.City;
import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.getChatId;

public class OshCityCommand implements Command {

    private static final String SELECTED_CITY_INFO = "<code>Документы поданы в г.%s\n\nДалее отправьте номер заявления...</code>";

    private final SendMessageService sendMessageService;

    public OshCityCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(Update update) {
        sendMessageService.sendMessage(getChatId(update), String.format(SELECTED_CITY_INFO, City.OSH.name));
    }
}

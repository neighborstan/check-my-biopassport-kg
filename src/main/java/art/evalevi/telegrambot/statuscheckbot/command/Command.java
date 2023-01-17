package art.evalevi.telegrambot.statuscheckbot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

    void execute(Update update);
}

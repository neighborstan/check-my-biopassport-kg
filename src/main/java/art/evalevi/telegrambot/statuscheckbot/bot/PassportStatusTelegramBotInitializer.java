package art.evalevi.telegrambot.statuscheckbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class PassportStatusTelegramBotInitializer {

    private final PassportStatusTelegramBot bot;

    @Autowired
    public PassportStatusTelegramBotInitializer(PassportStatusTelegramBot bot) {
        this.bot = bot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);

            bot.execute(new SetMyCommands(getBotMenus(Menu.values()), new BotCommandScopeDefault(), null));

        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private List<BotCommand> getBotMenus(Menu[] menus){
        List<BotCommand> botMenus = new ArrayList<>();
        Arrays.stream(menus).forEach(menu -> botMenus.add(new BotCommand(menu.getName(), menu.getDescription())));
        return botMenus;
    }
}

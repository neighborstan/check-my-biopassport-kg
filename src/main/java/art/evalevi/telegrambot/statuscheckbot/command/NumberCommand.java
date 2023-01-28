package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.dto.Passport;
import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static art.evalevi.telegrambot.statuscheckbot.command.CommandUtil.getChatId;

public class NumberCommand implements Command {

    private static final String PASSPORT_STATUS_TEXT = """
            <code>Дата запроса::%s (МСК)
                        
            UID::%s
            Дата подачи::%s
            Основной статус::%s
            Внутренний статус::%s
            Готовность::%s&#37;</code>
            """;
    private final SendMessageService sendMessageService;
    private final Passport passport;

    public NumberCommand(SendMessageService sendMessageService, Passport passport) {
        this.sendMessageService = sendMessageService;
        this.passport = passport;
    }

    @Override
    public void execute(Update update) {
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zonedUTC = now.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedMoscow = zonedUTC.withZoneSameInstant(ZoneId.of("Europe/Moscow"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        String sendText = String.format(PASSPORT_STATUS_TEXT,
                zonedMoscow.format(formatter),
                passport.getUid(),
                passport.getReceptionDate(),
                passport.getPassportStatus().getName(),
                passport.getInternalStatus().getName(),
                passport.getInternalStatus().getPercent());

        sendMessageService.sendMessage(getChatId(update), sendText);
    }
}

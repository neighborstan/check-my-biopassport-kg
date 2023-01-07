package art.evalevi.telegrambot.statuscheckbot.bot;

import art.evalevi.telegrambot.statuscheckbot.dto.Passport;
import art.evalevi.telegrambot.statuscheckbot.service.PassportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PassportStatusTelegramBot extends TelegramLongPollingBot {

    private static final String PASSPORT_STATUS_TEXT = """
            <code>Дата запроса::%s (МСК)
                        
            UID заявления::%s
            Дата подачи заявления::%s
            Основной статус::%s
            Внутренний статус::%s
            Готовность::%s&#37;</code>
            """;
    private static final String NOT_FOUND_WARNING_TEXT = "<code>Номер не найден </code>" + "\uD83D\uDE15";

    private final PassportService passportService;


    private String cityCode;

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Autowired
    public PassportStatusTelegramBot(PassportService passportService) {
        this.passportService = passportService;
        this.cityCode = passportService.getBishkekCode();
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            Long chatId = update.getMessage().getChatId();
            String textRequest = update.getMessage().getText().trim();

            Optional<Passport> passport = receivePassportData(textRequest, cityCode);

            if (passport.isPresent()) {
                sendPassportStatusMessage(chatId, passport.get());
            } else {
                sendWarningMessage(chatId);
            }
        }

    }

    private Optional<Passport> receivePassportData(String text, String cityCode) {
        Optional<Passport> passport = Optional.empty();

        if (isID(text)) {
            Passport[] passports = passportService.getPassportsByIdSync(text, cityCode);
            log.info(String.format("Reply to request by ID: %s", text));
            passport = passports.length > 0 ? Optional.ofNullable(passports[0]) : Optional.empty();
        } else if (isUID(text)) {
            passport = Optional.ofNullable(passportService.getPassportByUIDSync(text));
            log.info(String.format("Reply to request by UID: %s", text));
        }
        return passport;
    }

    private void sendPassportStatusMessage(Long chatId, Passport passport) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        SendMessage message = new SendMessage();
        String sendText = String.format(PASSPORT_STATUS_TEXT,
                now.format(formatter),
                passport.getUid(),
                passport.getReceptionDate(),
                passport.getPassportStatus().getName(),
                passport.getInternalStatus().getName(),
                passport.getInternalStatus().getPercent());

        message.setChatId(chatId);
        message.setText(sendText);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        message.enableHtml(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void sendWarningMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(NOT_FOUND_WARNING_TEXT);
        executeMessage(message);
    }

    private Boolean isUID(String id) {
        String uidRegex = "^\\d{25}$";
        Pattern pattern = Pattern.compile(uidRegex);
        return pattern.matcher(id).matches();
    }

    private Boolean isID(String id) {
        String idRegex = "^\\d{1,8}$";
        Pattern pattern = Pattern.compile(idRegex);
        return pattern.matcher(id).matches();
    }
}

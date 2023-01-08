package art.evalevi.telegrambot.statuscheckbot.bot;

import art.evalevi.telegrambot.statuscheckbot.dto.Passport;
import art.evalevi.telegrambot.statuscheckbot.entity.User;
import art.evalevi.telegrambot.statuscheckbot.service.PassportService;
import art.evalevi.telegrambot.statuscheckbot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PassportStatusTelegramBot extends TelegramLongPollingBot {

    private static final String START_COMMAND = "/start";
    private static final String BISHKEK_BUTTON = "Бишкек";
    private static final String OSH_BUTTON = "Ош";
    private static final String SELECTED_CITY_INFO = "<code>Документы поданы в г.%s\n\nДалее отправьте номер заявления...</code>";
    private static final String NOT_FOUND_WARNING_TEXT = "<code>Номер не найден </code>" + "\uD83D\uDE15";
    private static final String START_TEXT = "<code><b>%s</b>, саламатсызбы, как говорится!" +
            "\n\nЧтобы получить информацию о текущем статусе биометрического загранпаспорта РФ отправьте короткий номер " +
            "заявления, не более 8 цифр или полный идентификатор заявления - 25 цифр\n\nВыберите город, где было" +
            " подано заявление</code>";
    private static final String PASSPORT_STATUS_TEXT = """
            <code>Дата запроса::%s (МСК)
                        
            UID заявления::%s
            Дата подачи заявления::%s
            Основной статус::%s
            Внутренний статус::%s
            Готовность::%s&#37;</code>
            """;

    private final PassportService passportService;
    private final UserService userService;

    private ReplyKeyboardMarkup keyboardMarkup;

    private String cityCode;
    private String selectedCity;

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Autowired
    public PassportStatusTelegramBot(PassportService passportService, UserService userService) {
        this.passportService = passportService;
        this.userService = userService;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String textRequest = update.getMessage().getText().trim();
            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();
            String username = update.getMessage().getFrom().getUserName();
            String nameToSendUser = firstName.isEmpty() ? username : firstName;

            Optional<Passport> passport = receivePassportData(textRequest, cityCode);

            initKeyboard();

            if (START_COMMAND.equals(textRequest)) {
                sendStartMessage(chatId, nameToSendUser);
                log.info("A start message is sent");
            } else if (BISHKEK_BUTTON.equals(textRequest)) {
                sendSelectedCityMessage(chatId, BISHKEK_BUTTON);
                cityCode = passportService.getBishkekCode();
                selectedCity = BISHKEK_BUTTON;
                log.info("Selected city message is sent " + BISHKEK_BUTTON);
            } else if (OSH_BUTTON.equals(textRequest)) {
                sendSelectedCityMessage(chatId, OSH_BUTTON);
                cityCode = passportService.getOshCode();
                selectedCity = OSH_BUTTON;
                log.info("Selected city message is sent " + OSH_BUTTON);
            } else if (passport.isPresent()) {
                sendPassportStatusMessage(chatId, passport.get());
                log.info("Passport status message is sent");
            } else {
                sendWarningMessage(chatId);
                log.info("Warning message is sent");
            }

            userService.findByChatId(chatId).ifPresentOrElse(
                    user -> {
                        Integer requestsCount = user.getRequestsCount();
                        user.setRequestsCount(++requestsCount);
                        user.setLastRequest(textRequest);
                        user.setSelectedCity(selectedCity);
                        userService.save(user);
                        log.info(String.format("User %s updated", nameToSendUser));
                    },
                    () -> {
                        User user = new User();
                        user.setChatId(chatId);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setUserName(username);
                        user.setRequestsCount(1);
                        user.setLastRequest(textRequest);
                        user.setSelectedCity(selectedCity);
                        userService.save(user);
                        log.info(String.format("User %s created", nameToSendUser));
                    });
        }
    }

    private void initKeyboard() {
        keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add(new KeyboardButton(BISHKEK_BUTTON));
        row.add(new KeyboardButton(OSH_BUTTON));

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
    }

    /**
     * Output example: input 25 digit number
     * var.1 [{"uid":"2000996012022112300018600","sourceUid":null,"receptionDate":"2022-11-23","passportStatus":{"id":1,"name":"Документы приняты","description":null,"color":"blue","subscription":true},"internalStatus":{"name":"на согласовании","percent":30},"clones":[]}]
     * var.2 []
     * Output example: input <=8 digit number
     * {"uid":"2000996012022112300018600","sourceUid":null,"receptionDate":"2022-11-23","passportStatus":{"id":1,"name":"Документы приняты","description":null,"color":"blue","subscription":true},"internalStatus":{"name":"на согласовании","percent":30},"clones":[]}
     */
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

    private void sendWarningMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(NOT_FOUND_WARNING_TEXT);
        executeMessage(message);
    }

    private void sendSelectedCityMessage(Long chatId, String city) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(String.format(SELECTED_CITY_INFO, city));
        executeMessage(message);
    }

    private void sendStartMessage(Long chatId, String name) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String sendText = String.format(START_TEXT, name);
        message.setText(sendText);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        message.setReplyMarkup(keyboardMarkup);
        message.enableHtml(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
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

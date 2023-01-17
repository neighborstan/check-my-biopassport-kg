package art.evalevi.telegrambot.statuscheckbot.command;

import art.evalevi.telegrambot.statuscheckbot.bot.City;
import art.evalevi.telegrambot.statuscheckbot.dto.Passport;
import art.evalevi.telegrambot.statuscheckbot.service.PassportService;
import art.evalevi.telegrambot.statuscheckbot.service.SendMessageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
public class CommandIdentifier {

    private final SendMessageService sendMessageService;
    private final PassportService passportService;

    @Getter
    private String selectedCity;
    private String cityCode;

    public CommandIdentifier(SendMessageService sendMessageService, PassportService passportService) {
        this.sendMessageService = sendMessageService;
        this.passportService = passportService;
        cityCode = City.BISHKEK.code;
    }

    public Command findCommand(String textRequest) {
        Command command = new UnknownCommand(sendMessageService);

        Optional<Passport> passport = receivePassportData(textRequest, cityCode);

        if ("/start".equals(textRequest)) {
            command = new StartCommand(sendMessageService);
        } else if (City.BISHKEK.name.equals(textRequest)) {
            cityCode = City.BISHKEK.code;
            selectedCity = City.BISHKEK.name;
            command = new BishkekCityCommand(sendMessageService);
        } else if (City.OSH.name.equals(textRequest)) {
            cityCode = City.OSH.code;
            selectedCity = City.OSH.name;
            command = new OshCityCommand(sendMessageService);
        } else if (passport.isPresent()) {
            command = new NumberCommand(sendMessageService, passport.get());
        }

        return command;
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

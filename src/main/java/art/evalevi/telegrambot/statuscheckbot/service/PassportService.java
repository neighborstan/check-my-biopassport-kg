package art.evalevi.telegrambot.statuscheckbot.service;

import art.evalevi.telegrambot.statuscheckbot.dto.Passport;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
@Slf4j
public class PassportService {

    @Value("${url.target.base}")
    private String baseUrlTemplate;

    @Value("${url.target.id}")
    private String idUrlTemplate;

    @Value("${url.target.uid}")
    private String uidUrlTemplate;


    public Passport[] getPassportsByIdSync(final String id, String cityCode) {
        return Unirest.get(String.format(baseUrlTemplate + idUrlTemplate, cityCode, id))
                .asObject(new GenericType<Passport[]>() {
                })
                .getBody();
    }

    public Passport getPassportByUIDSync(final String id) {
        return Unirest.get(String.format(baseUrlTemplate + uidUrlTemplate, id))
                .asObject(new GenericType<Passport>() {
                })
                .getBody();
    }
}

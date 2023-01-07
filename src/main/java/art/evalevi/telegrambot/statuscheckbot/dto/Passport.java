package art.evalevi.telegrambot.statuscheckbot.dto;

import lombok.Data;

@Data
public class Passport {
    private String uid;
    private String sourceUid;
    private String receptionDate;
    private PassportStatus passportStatus;
    private InternalStatus internalStatus;
    private String[] clones;
}

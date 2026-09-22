package schwartz.spring.app.domain.demand;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum DemandStatus {
    CREATED(0),
    ACTIVE(1),
    STOPPED(2),
    FINISHED(3),
    CANCELED(4),
    REOPENED(5);

    private final int code;

    DemandStatus(int code) {
        this.code = code;
    }

    private static final Map<Integer, DemandStatus> BY_CODE = new HashMap<>();

    static {
        for (DemandStatus s : values()) {
            BY_CODE.put(s.code, s);
        }
    }

    public static DemandStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        DemandStatus status = BY_CODE.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Unknown DemandStatus code: " + code);
        }
        return status;
    }
}
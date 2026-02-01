package api.basic.com.projects.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class FieldException extends RuntimeException {
    private String field;
    private HttpStatus status;
    public FieldException(String message, String field, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
    }
}

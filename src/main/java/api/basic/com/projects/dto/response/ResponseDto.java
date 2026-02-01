package api.basic.com.projects.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDto<T> {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private T data;
    private String path;

    public static <T>ResponseEntity<ResponseDto<T>> response(T data, int status) {
        String currentPath = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRequestURI();
        return ResponseEntity.status(HttpStatusCode.valueOf(status)).body(
                ResponseDto.<T>builder()
                        .timestamp(LocalDateTime.now())
                        .status(status)
                        .data(data)
                        .path(currentPath)
                        .build()
        );
    }
}

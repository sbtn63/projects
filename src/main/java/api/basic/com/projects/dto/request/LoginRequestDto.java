package api.basic.com.projects.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "El username es obligatorio y no puede ser blanco ")
    private String username;
    @NotBlank(message = "El password es obligatorio y no puede ser blanco ")
    private String password;
}

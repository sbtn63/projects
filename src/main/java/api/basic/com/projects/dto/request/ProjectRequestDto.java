package api.basic.com.projects.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestDto {
    @NotBlank(message = "El nombre es obligatorio y no puede estar vacio")
    private String name;

    private String description;
    private String emblemUrl;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    private LocalDate endDate;
}
package api.basic.com.projects.controller;

import api.basic.com.projects.dto.request.ProjectRequestDto;
import api.basic.com.projects.dto.response.ProjectResponseDto;
import api.basic.com.projects.dto.response.ResponseDto;
import api.basic.com.projects.entity.User;
import api.basic.com.projects.exception.NotFoundException;
import api.basic.com.projects.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<?> findAll(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ProjectResponseDto> projects = projectService.findAll(userDetails);
        if(projects.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseDto.response(projects, HttpStatus.OK.value());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        ProjectResponseDto project = projectService.findById(id, userDetails).orElseThrow(
                () -> new NotFoundException("ID " + id + " no encontrado")
        );
        return ResponseDto.response(project, HttpStatus.OK.value());
    }

    @PostMapping
    public ResponseEntity<?> save(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProjectRequestDto projectRequestDto
    ) {
        System.out.println(userDetails.getUsername());
        ProjectResponseDto savedProject = projectService.save(projectRequestDto, userDetails);
        return ResponseDto.response(savedProject, HttpStatus.CREATED.value());
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProjectRequestDto projectRequestDto,
            @PathVariable Long id
    ) {
        ProjectResponseDto project = projectService.update(id, projectRequestDto, userDetails).orElseThrow(
                () -> new NotFoundException("ID " + id + " no encontrado")
        );
        return ResponseDto.response(project, HttpStatus.OK.value());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        if(!projectService.deleteById(id, userDetails)) throw new NotFoundException("ID " + id + " no encontrado");
        Map<String, String> data = new HashMap<>();
        data.put("message", "Projecto eliminado correctamente");
        return ResponseDto.response(data, HttpStatus.OK.value());
    }
}

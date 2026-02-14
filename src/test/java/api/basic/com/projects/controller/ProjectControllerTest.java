package api.basic.com.projects.controller;

import api.basic.com.projects.dto.request.ProjectRequestDto;
import api.basic.com.projects.dto.response.ProjectResponseDto;
import api.basic.com.projects.service.CustomUserDetailService;
import api.basic.com.projects.service.ProjectService;
import api.basic.com.projects.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private  ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldFindAllProjectsWhenSuccessfully() throws Exception {
        List<ProjectResponseDto> projects = getProjects();
        Mockito.when(projectService.findAll(any())).thenReturn(projects);

        mockMvc.perform(get("/projects")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Project"));
    }

    @Test
    void shouldFindAllProjectsWhenNotContent() throws Exception {
        Mockito.when(projectService.findAll(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/projects")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFindProjectWhenSuccessfully() throws Exception {
        ProjectResponseDto project = getProjects().get(0);
        Mockito.when(projectService.findById(any(), any())).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/1")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    void shouldFindProjectWhenNotFound() throws Exception {
        Long id = 99L;
        Mockito.when(projectService.findById(eq(id), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/99")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    void shouldSaveProjectWhenSuccessfully() throws Exception {
        ProjectResponseDto response = getProjects().get(0);
        Mockito.when(projectService.save(any(ProjectRequestDto.class), any())).thenReturn(response);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    void shouldSaveProjectWhenBadRequest() throws Exception {
        ProjectRequestDto invalidRequest = ProjectRequestDto.builder().build();

        mockMvc.perform(post("/projects")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateProjectWhenSuccessfully() throws Exception {
        Long id = 1L;
        ProjectResponseDto project = getProjects().get(0);
        Mockito.when(projectService.update(eq(id), any(ProjectRequestDto.class), any()))
                .thenReturn(Optional.of(project));

        mockMvc.perform(put("/projects/1")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateProjectWhenNotFound() throws Exception {
        Long id = 99L;
        ProjectResponseDto project = getProjects().get(0);
        Mockito.when(projectService.update(eq(id), any(ProjectRequestDto.class), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/projects/99")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateProjectWhenBadRequest() throws Exception {
        ProjectRequestDto project = ProjectRequestDto.builder().build();

        mockMvc.perform(put("/projects/1")
                        .with(user("test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteProjectWhenSuccessfully() throws Exception {
        Long id = 1L;
        Mockito.when(projectService.deleteById(eq(id), any())).thenReturn(true);

        mockMvc.perform(delete("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message")
                        .value("Projecto eliminado correctamente"));
    }

    @Test
    void shouldDeleteProjectWhenNotFound() throws Exception {
        Long id = 1L;
        Mockito.when(projectService.deleteById(eq(id), any())).thenReturn(false);

        mockMvc.perform(delete("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.message")
                        .value("ID 1 no encontrado"));
    }

    List<ProjectResponseDto> getProjects() {
        List<ProjectResponseDto> projects = new ArrayList<>();
        projects.add(ProjectResponseDto.builder()
                        .id(1L)
                        .name("Test Project")
                        .description("Test Description Project")
                        .emblemUrl("https://urlemblem.com")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now())
                        .build());

        return projects;
    }
}

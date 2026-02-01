package api.basic.com.projects.service;

import api.basic.com.projects.dto.request.ProjectRequestDto;
import api.basic.com.projects.dto.response.ProjectResponseDto;
import api.basic.com.projects.entity.Project;
import api.basic.com.projects.entity.User;
import api.basic.com.projects.exception.FieldException;
import api.basic.com.projects.repository.IProjectRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private IProjectRepository iProjectRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    public List<ProjectResponseDto> findAll(UserDetails userDetails) {
        User user = userService.getAuthenticatedUser(userDetails);
        return user.getProjects().stream().map(
                project -> modelMapper.map(project, ProjectResponseDto.class)
        ).toList();
    }

    public Optional<ProjectResponseDto> findById(Long id, UserDetails userDetails) {
        User user = userService.getAuthenticatedUser(userDetails);
        return iProjectRepository.findById(id)
                .filter(project -> project.getUser().getUsername().equals(user.getUsername()))
                .map(project -> modelMapper.map(project, ProjectResponseDto.class));
    }

    @Transactional
    public ProjectResponseDto save(ProjectRequestDto projectRequestDto, UserDetails userDetails) {
        validateDates(projectRequestDto);
        Project project = modelMapper.map(projectRequestDto, Project.class);
        project.setUser(userService.getAuthenticatedUser(userDetails));
        Project saveProject = iProjectRepository.save(project);
        return modelMapper.map(saveProject, ProjectResponseDto.class);
    }

    @Transactional
    public Optional<ProjectResponseDto> update(Long id, ProjectRequestDto projectRequestDto, UserDetails userDetails) {
        validateDates(projectRequestDto);
        User user = userService.getAuthenticatedUser(userDetails);
        return iProjectRepository.findById(id)
                .filter(project -> project.getUser().getUsername().equals(user.getUsername()))
                .map(existingProject -> {
                    modelMapper.typeMap(ProjectRequestDto.class, Project.class).addMappings(
                            mapper -> {
                                mapper.skip(Project::setId);
                                mapper.skip(Project::setUser);
                            }
                    );
                    modelMapper.map(projectRequestDto, existingProject);
                    return modelMapper.map(existingProject, ProjectResponseDto.class);
                });
    }

    @Transactional
    public Boolean deleteById(Long id, UserDetails userDetails) {
        User user = userService.getAuthenticatedUser(userDetails);
        return iProjectRepository.findById(id)
                .filter(project -> project.getUser().getUsername().equals(user.getUsername()))
                .map(
                project -> {
                    iProjectRepository.delete(project);
                    return true;
                }
        ).orElse(false);
    }

    private void validateDates(ProjectRequestDto dto) {
        if (!isDateRangeValid(dto.getStartDate(), dto.getEndDate())) {
            throw new FieldException(
                    "La fecha de inicio no puede ser posterior a la de fin",
                    "startDate",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private boolean isDateRangeValid(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return true;
        return !startDate.isAfter(endDate);
    }
}

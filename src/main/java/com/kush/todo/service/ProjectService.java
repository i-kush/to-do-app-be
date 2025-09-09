package com.kush.todo.service;

import com.kush.todo.constant.CommonErrorMessages;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.ProjectRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ProjectResponseDto;
import com.kush.todo.entity.Project;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.ProjectMapper;
import com.kush.todo.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final CurrentUser currentUser;

    @Transactional
    public ProjectResponseDto create(ProjectRequestDto request) {
        Project project = projectMapper.toProject(currentUser.getTenantId(), request);
        return projectMapper.toProjectResponseDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto findByIdRequired(UUID id) {
        return projectMapper.toProjectResponseDto(projectRepository.findByIdAndTenantIdRequired(id, currentUser.getTenantId()));
    }

    @Transactional
    public ProjectResponseDto update(UUID id, ProjectRequestDto request) {
        Project project = projectMapper.toProject(projectRepository.findByIdAndTenantIdRequired(id, currentUser.getTenantId()), request);
        return projectMapper.toProjectResponseDto(projectRepository.save(project));
    }

    @Transactional
    public void delete(UUID id) {
        verifyExists(id);
        projectRepository.deleteByIdAndTenantId(id, currentUser.getTenantId());
    }

    @Transactional(readOnly = true)
    public CustomPage<ProjectResponseDto> findAll(int page, int size) {
        Page<ProjectResponseDto> pages = projectRepository.findAllByTenantId(PageRequest.of(page - 1, size), currentUser.getTenantId())
                                                          .map(projectMapper::toProjectResponseDto);
        return projectMapper.toCustomPage(pages);
    }

    @Transactional(readOnly = true)
    public CustomPage<ProjectResponseDto> findAll(int page, int size, String key) {
        Page<ProjectResponseDto> pages = projectRepository
                .findAllLike(PageRequest.of(page - 1, size), currentUser.getTenantId(), key)
                .map(projectMapper::toProjectResponseDto);
        return projectMapper.toCustomPage(pages);
    }

    @Transactional(readOnly = true)
    public void verifyExists(UUID id) {
        if (!projectRepository.existsByIdAndTenantId(id, currentUser.getTenantId())) {
            throw new NotFoundException(String.format(CommonErrorMessages.PATTERN_NOT_FOUND, id));
        }
    }
}
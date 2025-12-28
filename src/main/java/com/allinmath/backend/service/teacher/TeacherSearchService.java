package com.allinmath.backend.service.teacher;

import com.allinmath.backend.dto.teacher.SearchTeachersDTO;
import com.allinmath.backend.model.account.TeacherProfile;
import com.allinmath.backend.repository.TeachersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TeacherSearchService {

    private final TeachersRepository teachersRepository;

    public TeacherSearchService(TeachersRepository teachersRepository) {
        this.teachersRepository = teachersRepository;
    }

    public List<TeacherProfile> search(SearchTeachersDTO dto) {
        try {
            return teachersRepository.searchTeachers(dto);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to search teachers", e);
        }
    }
}

package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.api.UserDataAccessService;
import com.innowise.userservice.service.api.UserQueryService;
import com.innowise.userservice.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepo;
    private final UserDataAccessService userDataAccessService;
    private final UserMapper mapper;


    @Override
    public UserResponseDto findDtoById(long id) {
        return mapper.toDto(userDataAccessService.findById(id));
    }

    @Override
    public UserResponseDto findDtoByEmail(String email) {
        return mapper.toDto(userDataAccessService.findByEmail(email));
    }

    @Override
    public Page<UserResponseDto> findAll(String name,
                                         String surname,
                                         LocalDate birthDate,
                                         Boolean active,
                                         Pageable pageable) {
        Specification<User> spec = configureSpecification(name, surname, birthDate, active);

        Page<User> users = userRepo.findAll(spec, pageable);

        return mapper.toDto(users);
    }

    private Specification<User> configureSpecification(String name,
                                                       String surname,
                                                       LocalDate birthDate,
                                                       Boolean active) {
        Specification<User> spec = Specification.unrestricted();

        if (name != null) {
            spec = spec.and(UserSpecification.hasName(name));
        }

        if (surname != null) {
            spec = spec.and(UserSpecification.hasSurname(surname));
        }

        if (birthDate != null) {
            spec = spec.and(UserSpecification.hasBirthDate(birthDate));
        }

        if (active != null) {
            spec = spec.and(UserSpecification.isActive(active));
        }

        return spec;

    }
}

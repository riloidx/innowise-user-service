package com.innowise.userservice.service;

import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.api.UserDataAccessService;
import com.innowise.userservice.service.impl.UserQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserDataAccessService userDataAccessService;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserQueryServiceImpl service;

    @Mock
    private Pageable pageable;

    @Test
    void findAll_shouldReturnEmptyPage_whenAllParametersNull() {
        Page<User> usersPage = new PageImpl<>(List.of());
        Page<UserResponseDto> dtoPage = new PageImpl<>(List.of());

        when(userRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(usersPage);
        when(mapper.toDto(usersPage)).thenReturn(dtoPage);

        Page<UserResponseDto> result = service.findAll(null, null, null, null, pageable);

        assertEquals(dtoPage, result);
        verify(userRepo).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(usersPage);
    }

    @Test
    void findAll_shouldBuildSpecification_whenSomeParametersProvided() {
        String name = "Alice";
        Boolean active = true;
        String email = "alice@test.com";

        User user = User.builder()
                .id(1L)
                .name(name)
                .surname("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email(email)
                .active(active)
                .build();

        UserResponseDto userDto = UserResponseDto.builder()
                .id(1L)
                .name(name)
                .surname("Smith")
                .build();

        Page<User> usersPage = new PageImpl<>(List.of(user));
        Page<UserResponseDto> dtoPage = new PageImpl<>(List.of(userDto));

        when(userRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(usersPage);
        when(mapper.toDto(usersPage)).thenReturn(dtoPage);

        Page<UserResponseDto> result = service.findAll(name, null, null, active, pageable);

        assertEquals(dtoPage, result);
        verify(userRepo).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(usersPage);
    }

    @Test
    void findAll_shouldBuildSpecification_whenAllParametersProvided() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String name = "Test";
        String surname = "Test";
        String email = "test@test.com";
        Boolean active = true;

        User user = User.builder()
                .id(1L)
                .name(name)
                .surname(surname)
                .birthDate(birthDate)
                .email(email)
                .active(active)
                .build();

        UserResponseDto userDto = UserResponseDto.builder()
                .id(1L)
                .name(name)
                .surname(surname)
                .birthDate(birthDate)
                .email(email)
                .active(active)
                .build();

        Page<User> usersPage = new PageImpl<>(List.of(user));
        Page<UserResponseDto> dtoPage = new PageImpl<>(List.of(userDto));

        when(userRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(usersPage);
        when(mapper.toDto(usersPage)).thenReturn(dtoPage);

        Page<UserResponseDto> result = service.findAll(name, surname, birthDate, active, pageable);

        assertEquals(dtoPage, result);
        verify(userRepo).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(usersPage);
    }
}

package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.UserCreateDto;
import com.innowise.userservice.dto.request.UserUpdateDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.BadRequestException;
import com.innowise.userservice.exception.UserAlreadyExistsException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.api.UserDataAccessService;
import com.innowise.userservice.service.impl.UserCommandServiceImpl;
import com.innowise.userservice.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserDataAccessService userDataAccessService;

    @Mock
    private UserMapper mapper;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private UserCommandServiceImpl service;


    @Test
    void create_shouldSaveAndReturnDto_whenEmailNotTaken() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("test@test.com");
        createDto.setName("Test");
        createDto.setSurname("Test");
        createDto.setBirthDate(LocalDate.of(1990, 1, 1));

        User userEntity = User.builder()
                .id(1L)
                .name("Test")
                .surname("Test")
                .email("test@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .build();

        UserResponseDto userDto = UserResponseDto.builder()
                .id(1L)
                .name("Test")
                .surname("Test")
                .email("test@example.com")
                .birthDate(LocalDate.of(2000, 1, 1))
                .active(true)
                .build();

        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(mapper.toEntity(createDto)).thenReturn(userEntity);
        when(userRepo.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDto(userEntity)).thenReturn(userDto);

        UserResponseDto result = service.create(createDto);

        assertEquals(userDto, result);
        verify(userRepo).findByEmail("test@test.com");
        verify(userRepo).save(userEntity);
        verify(mapper).toEntity(createDto);
        verify(mapper).toDto(userEntity);
    }

    @Test
    void create_shouldThrow_whenEmailTaken() {
        UserCreateDto createDto = new UserCreateDto();
        String email = "test@test.com";
        createDto.setEmail(email);

        User existingUser = User.builder().id(1L).email(email).build();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> service.create(createDto));
        verify(userRepo).findByEmail(email);
        verifyNoMoreInteractions(userRepo, mapper);
    }

    @Test
    void update_shouldSaveAndReturnDto_whenValid() {
        long userId = 1L;

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setId(userId);
        updateDto.setEmail("new@test.com");

        User currentUser = User.builder()
                .id(userId)
                .email("old@test.com")
                .active(true)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .email("new@test.com")
                .active(true)
                .build();

        UserResponseDto userDto = UserResponseDto.builder().id(userId).email("new@test.com").build();

        doNothing().when(validationUtil).validateMatchingIds(userId, updateDto.getId());
        when(userDataAccessService.findById(userId)).thenReturn(currentUser);
        when(userRepo.findByEmail("new@test.com")).thenReturn(Optional.empty());
        doNothing().when(mapper).updateEntityFromDto(updateDto, currentUser);
        when(userRepo.save(currentUser)).thenReturn(updatedUser);
        when(mapper.toDto(updatedUser)).thenReturn(userDto);

        UserResponseDto result = service.update(userId, updateDto);

        assertEquals(userDto, result);
        verify(validationUtil).validateMatchingIds(userId, updateDto.getId());
        verify(userDataAccessService).findById(userId);
        verify(userRepo).save(currentUser);
        verify(mapper).updateEntityFromDto(updateDto, currentUser);
        verify(mapper).toDto(updatedUser);
    }

    @Test
    void delete_shouldCallRepo_whenUserExists() {
        long userId = 1L;
        User user = User.builder().id(userId).build();
        when(userDataAccessService.findById(userId)).thenReturn(user);

        service.delete(userId);

        verify(userDataAccessService).findById(userId);
        verify(userRepo).deleteById(userId);
    }

    @Test
    void changeStatus_shouldUpdateActive_whenDifferent() {
        long userId = 1L;
        User user = User.builder().id(userId).active(false).build();
        User updatedUser = User.builder().id(userId).active(true).build();
        UserResponseDto dto = UserResponseDto.builder().id(userId).active(true).build();

        when(userDataAccessService.findById(userId)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(updatedUser);
        when(mapper.toDto(updatedUser)).thenReturn(dto);

        UserResponseDto result = service.changeStatus(userId, true);

        assertEquals(dto, result);
        verify(userDataAccessService).findById(userId);
        verify(userRepo).save(user);
        verify(mapper).toDto(updatedUser);
    }

    @Test
    void changeStatus_shouldThrow_whenStatusAlreadySet() {
        long userId = 1L;
        User user = User.builder().id(userId).active(true).build();

        when(userDataAccessService.findById(userId)).thenReturn(user);

        assertThrows(BadRequestException.class, () -> service.changeStatus(userId, true));
        verify(userDataAccessService).findById(userId);
        verifyNoMoreInteractions(userRepo, mapper);
    }
}

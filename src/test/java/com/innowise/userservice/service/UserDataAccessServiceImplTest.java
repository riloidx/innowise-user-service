package com.innowise.userservice.service;

import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.PaymentCardNotFoundException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.PaymentCardDataAccessServiceImpl;
import com.innowise.userservice.service.impl.UserDataAccessServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDataAccessServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserDataAccessServiceImpl service;

    @Test
    void findById_returnUser_whenUserExists() {
        long id = 1L;

        User user = User.builder().
                id(id).
                name("Test").
                build();

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        User result = service.findById(id);

        assertEquals(user, result);
        verify(userRepo).findById(id);
    }

    @Test
    void findById_ThrowException_whenUserDoesNotExist() {
        long id = 1L;
        when(userRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findById(id));
        verify(userRepo).findById(id);
    }

    @Test
    void findByEmail_returnUser_whenUserExist() {
        String email = "test@test.com";

        User user = User.builder().
                id(1L).
                email(email).
                build();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        User result = service.findByEmail(email);

        assertEquals(user, result);
        verify(userRepo).findByEmail(email);
    }

    @Test
    void findByEmail_ThrowException_whenUserDoesNotExist() {
        String email = "test@test.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findByEmail(email));
        verify(userRepo).findByEmail(email);
    }

}
package com.innowise.userservice.controller;

import com.innowise.userservice.dto.request.UserCreateDto;
import com.innowise.userservice.dto.request.UserUpdateDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.service.api.UserCommandService;
import com.innowise.userservice.service.api.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService commandService;
    private final UserQueryService queryService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        UserResponseDto dto = queryService.findDtoById(id);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> findAll(@RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String surname,
                                                         @RequestParam(required = false) LocalDate birthDate,
                                                         @RequestParam(required = false) Boolean active,
                                                         Pageable pageable) {
        Page<UserResponseDto> page = queryService.findAll(name, surname, birthDate, active, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getByEmail(@PathVariable String email) {
        UserResponseDto dto = queryService.findDtoByEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserCreateDto dto) {
        UserResponseDto created = commandService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id,
                                                  @RequestBody @Valid UserUpdateDto dto) {
        UserResponseDto updated = commandService.update(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponseDto> activate(@PathVariable Long id) {
        UserResponseDto updated = commandService.changeStatus(id, true);

        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDto> deactivate(@PathVariable Long id) {
        UserResponseDto updated = commandService.changeStatus(id, false);

        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
}

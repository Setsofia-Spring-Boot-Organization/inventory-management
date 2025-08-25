package com.backend.inventory_management.core.user;

import com.backend.inventory_management.common.constants.Constants;
import com.backend.inventory_management.common.dto.BaseResponse;
import com.backend.inventory_management.core.user.mapper.UserMapper;
import com.backend.inventory_management.core.user.requests.NewUserDto;
import com.backend.inventory_management.core.user.requests.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX + "/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserEntity>> createUser(@Valid @RequestBody NewUserDto user) {
        try {
            UserEntity createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.success(createdUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(BaseResponse.success(UserMapper.toDto(user))))
                .orElse(ResponseEntity.notFound().build());
    }



    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<Page<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserEntity> users = search != null && !search.isEmpty() ?
                userService.searchUsers(search, pageable) :
                userService.findAllWithPagination(pageable);

        Page<UserResponseDto> dtoPage = users.map(UserMapper::toDto);

        return ResponseEntity.ok(BaseResponse.success(dtoPage));
    }




    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserEntity>> updateUser(
            @PathVariable Long id, @Valid @RequestBody UserEntity user) {
        try {
            UserEntity updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(BaseResponse.success(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> toggleUserStatus(@PathVariable Long id) {
        try {
            userService.toggleUserStatus(id);
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            userService.changePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BaseResponse<List<UserResponseDto>>> getUsersByStore(@PathVariable Long storeId) {
        List<UserResponseDto> dtoList = userService.findByStore(storeId)
                .stream()
                .map(UserMapper::toDto)
                .toList();

        return ResponseEntity.ok(BaseResponse.success(dtoList));
    }
}
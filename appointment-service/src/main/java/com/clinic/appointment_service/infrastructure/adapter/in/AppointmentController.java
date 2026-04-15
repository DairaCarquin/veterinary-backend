package com.clinic.appointment_service.infrastructure.adapter.in;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.appointment_service.application.dto.request.RescheduleRequest;
import com.clinic.appointment_service.application.dto.request.UpdateAppointmentStatusRequest;
import com.clinic.appointment_service.application.service.AppointmentService;
import com.clinic.appointment_service.domain.model.Appointment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','VETERINARY')")
    @PostMapping
    public Mono<Appointment> create(@Valid @RequestBody Appointment appointment, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        Long userId = Long.valueOf(authentication.getName());
        return service.create(appointment, role, userId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','VETERINARY')")
    public Mono<Map<String, Object>> findAll(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long veterinarianId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        Long userId = Long.valueOf(authentication.getName());

        return service.findAll(role, userId, petId, veterinarianId, status, date, page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','VETERINARY')")
    public Mono<Appointment> findById(@PathVariable Long id, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        Long userId = Long.valueOf(authentication.getName());
        return service.findById(id, role, userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    @PutMapping("/{id}/status")
    public Mono<Appointment> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request,
            Authentication authentication) {

        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        Long userId = Long.valueOf(authentication.getName());

        return service.updateStatus(id, request.getStatus(), role, userId);
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','VETERINARY')")
    public Mono<Appointment> reschedule(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest request,
            Authentication authentication) {

        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        Long userId = Long.valueOf(authentication.getName());

        return service.reschedule(id, request.getNewDate(), request.getNewVeterinarianId(), role, userId);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> toggle(@PathVariable Long id, @RequestParam boolean enabled) {
        return service.toggleEnabled(id, enabled);
    }
}

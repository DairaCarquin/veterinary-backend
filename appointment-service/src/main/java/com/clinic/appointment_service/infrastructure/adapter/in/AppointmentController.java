package com.clinic.appointment_service.infrastructure.adapter.in;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.appointment_service.application.dto.request.UpdateAppointmentStatusRequest;
import com.clinic.appointment_service.application.service.AppointmentService;
import com.clinic.appointment_service.domain.model.Appointment;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','VETERINARY')")
    @PostMapping
    public Mono<Appointment> create(
            @RequestBody Appointment appointment,
            Authentication authentication) {

        String role = authentication.getAuthorities()
                .iterator().next().getAuthority()
                .replace("ROLE_", "");

        Long userId = Long.valueOf(authentication.getName());

        return service.create(appointment, role, userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','VETERINARY')")
    @GetMapping
    public Flux<Appointment> findAll(Authentication authentication) {

        String role = authentication.getAuthorities()
                .iterator().next().getAuthority()
                .replace("ROLE_", "");

        Long userId = Long.valueOf(authentication.getName());

        return service.findAll(role, userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARY')")
    @PutMapping("/{id}/status")
    public Mono<Appointment> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentStatusRequest request,
            Authentication authentication) {

        String role = authentication.getAuthorities()
                .iterator().next()
                .getAuthority()
                .replace("ROLE_", "");

        Long userId = Long.valueOf(authentication.getName());

        return service.updateStatus(id, request.getStatus(), role, userId);
    }
}
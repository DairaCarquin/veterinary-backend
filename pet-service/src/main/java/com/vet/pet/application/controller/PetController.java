package com.vet.pet.application.controller;

import com.vet.pet.application.dto.request.CreatePetRequest;
import com.vet.pet.application.dto.request.UpdatePetRequest;
import com.vet.pet.application.dto.response.PetResponse;
import com.vet.pet.application.service.PetUseCaseImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetUseCaseImpl petUseCaseImpl;

    @PostMapping
    public ResponseEntity<PetResponse> createPet(
            @RequestBody CreatePetRequest request,
            Authentication authentication) {
        String role = "ADMIN"; // Default role for development
        if (authentication != null) {
            role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .orElse("ADMIN");
        }

        PetResponse response = petUseCaseImpl.createPet(request, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id) {
        PetResponse response = petUseCaseImpl.getPetById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getAllPets() {
        List<PetResponse> response = petUseCaseImpl.getAllPets();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> updatePet(
            @PathVariable Long id,
            @RequestBody UpdatePetRequest request) {
        PetResponse response = petUseCaseImpl.updatePet(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PetResponse> deletePet(@PathVariable Long id) {
        PetResponse response = petUseCaseImpl.deletePet(id);
        return ResponseEntity.ok(response);
    }

}

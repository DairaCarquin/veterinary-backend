package com.vet.gateway.infrastructure.audit;

import org.springframework.stereotype.Component;

import com.vet.gateway.application.service.AuditService;

@Component
public class ConsoleAuditAdapter implements AuditService {

    @Override
    public void audit(String message) {
        System.out.println("[AUDIT] " + message);
    }
}
package com.vet.security.infrastructure.client;

import com.vet.security.application.dto.request.UpdateDniRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente HTTP para comunicación con client-service
 */
@Component
public class ClientServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String clientServiceUrl;

    public ClientServiceClient(
            RestTemplate restTemplate,
            @Value("${client-service.url:http://localhost:8081}") String clientServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.clientServiceUrl = clientServiceUrl;
    }

    /**
     * Actualiza el DNI de un cliente
     * 
     * @param userId ID del usuario
     * @param dni Nuevo DNI
     */
    public void updateClientDni(Long userId, String dni) {
        try {
            String url = clientServiceUrl + "/clients/by-user/" + userId + "/dni";
            
            UpdateDniRequest request = new UpdateDniRequest(dni);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<UpdateDniRequest> entity = new HttpEntity<>(request, headers);
            
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            
            logger.info("DNI updated successfully in client-service for user: {}", userId);
            
        } catch (Exception e) {
            logger.error("Error updating DNI in client-service for user: {}", userId, e);
            // No lanzamos excepción para no fallar la actualización de password
            // El DNI se actualizará manualmente o en un reintento
        }
    }
}

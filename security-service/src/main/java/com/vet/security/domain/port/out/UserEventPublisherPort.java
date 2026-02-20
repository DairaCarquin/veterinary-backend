package com.vet.security.domain.port.out;
import com.vet.security.domain.exception.model.User;

public interface UserEventPublisherPort {
    void publishUserCreated(User user);
    void publishUserUpdated(User user);
    void publishUserDisabled(User user);
    void publishRoleAssigned(User user);

}

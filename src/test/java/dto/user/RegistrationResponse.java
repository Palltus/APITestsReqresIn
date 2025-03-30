package dto.user;

import lombok.Data;

@Data
public class RegistrationResponse {
    private Integer id;
    private String token;
}
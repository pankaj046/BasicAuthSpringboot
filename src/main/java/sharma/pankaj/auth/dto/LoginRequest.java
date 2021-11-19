package sharma.pankaj.auth.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class LoginRequest {
    private String email;
    private String password;
}


package sharma.pankaj.auth.comman;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HttpResponse {

    private int code;
    private HttpStatus status;
    private String message;
    private String devMessage;
    private Object data;

}

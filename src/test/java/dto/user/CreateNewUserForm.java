package dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateNewUserForm {
    @JsonProperty("name")
    private final String NAME;
    @JsonProperty("job")
    private final String JOB;

}

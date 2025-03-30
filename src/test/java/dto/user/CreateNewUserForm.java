package dto.user;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CreateNewUserForm {
    @SerializedName("name")
    private final String NAME;
    @SerializedName("job")
    private final String JOB;

}

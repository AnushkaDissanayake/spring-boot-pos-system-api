package lk.anushka.pointofsales.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegisterDTO {
    @Size(min = 3, message = "Username should contain at least three characters.")
    private String username;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",message = "Invalid Password format.\n" +"       Password must contain at least one digit [0-9].\n" +
            "       Password must contain at least one lowercase Latin character [a-z].\n" +
            "       Password must contain at least one uppercase Latin character [A-Z].\n" +
            "       Password must contain at least one special character like ! @ # & ( ).\n" +
            "       Password must contain a length of at least 8 characters and a maximum of 20 characters.")
    private String password;
    @Email(message = "Invalid Email Address.")
    private String email;
}

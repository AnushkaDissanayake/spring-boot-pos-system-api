package lk.anushka.pointofsales.util;

import lk.anushka.pointofsales.email.GMailer;
import lk.anushka.pointofsales.entity.UserEntity;
import lombok.NoArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class EmailUtil {

    public void sendVerificationEmail(UserEntity user, String serverURL) throws Exception {
        String toAddress = user.getEmail();
        String subject = "Please verify your registration";
        String content = "Dear [[name]],\n"
                + "Please click the link below to verify your registration:\n"
                + "[[URL]]\n\n\n"
                + "Thank you,\n"
                + "Anushka Dissanayake.";

        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);

        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = serverURL+"/verify?username="+user.getUsername()+"&code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        new GMailer().sendMail(subject,content,toAddress);
    }
}

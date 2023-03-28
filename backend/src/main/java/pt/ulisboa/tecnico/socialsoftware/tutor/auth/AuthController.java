package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthPasswordDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.validation.Valid;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_LOGIN_CREDENTIALS;

@RestController
public class AuthController {
    @Autowired
    private AuthUserService authUserService;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${oauth.consumer.key}")
    private String oauthConsumerKey;

    @Value("${oauth.consumer.secret}")
    private String oauthConsumerSecret;

    @Value("${callback.url}")
    private String callbackUrl;

    @GetMapping("/auth/fenix")
    public AuthDto fenixAuth(@RequestParam String code) {
        FenixEduInterface fenix = new FenixEduInterface(baseUrl, oauthConsumerKey, oauthConsumerSecret, callbackUrl);
        fenix.authenticate(code);
        return this.authUserService.fenixAuth(fenix);
    }

    @PostMapping("/auth/external")
    public AuthDto externalUserAuth(@Valid @RequestBody AuthPasswordDto authUsernameDto) {
        try {
            return authUserService.externalUserAuth(authUsernameDto.getUsername(), authUsernameDto.getPassword());
        } catch (TutorException e) {
            throw new TutorException(INVALID_LOGIN_CREDENTIALS);
        }
    }

    @GetMapping("/auth/demo/student")
    public AuthDto demoStudentAuth(@RequestParam Boolean createNew) {
        return this.authUserService.demoStudentAuth(createNew);
    }

    @GetMapping("/auth/demo/teacher")
    public AuthDto demoTeacherAuth() {
        return this.authUserService.demoTeacherAuth();
    }

    @GetMapping("/auth/demo/admin")
    public AuthDto demoAdminAuth() {
        return this.authUserService.demoAdminAuth();
    }

}
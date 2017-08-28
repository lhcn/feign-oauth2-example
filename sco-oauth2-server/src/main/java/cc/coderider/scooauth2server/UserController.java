package cc.coderider.scooauth2server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by henry on 27/08/2017.
 */
@RestController
public class UserController {

    /**
     * feignClient需要用到
     * @param user
     * @return
     */
    @GetMapping("/me")
    public Principal me(Principal user){
        return user;
    }
}

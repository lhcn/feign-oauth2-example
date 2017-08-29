package cc.coderider.scooauth2server;

import cc.coderider.scooauth2server.innerconsumer.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

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

    @Autowired
    ConsumerService consumerService;
    @GetMapping("/")
    public Map index(){
        return consumerService.complexCalc();
    }
}

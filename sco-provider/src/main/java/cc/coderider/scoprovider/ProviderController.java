package cc.coderider.scoprovider;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

/**
 * Created by henry on 27/08/2017.
 */
@RestController
public class ProviderController {

    @PreAuthorize("#oauth2.hasScope('service') or hasAuthority('system')")
    @GetMapping("/add")
    public Map add(
            @RequestParam(required = false, defaultValue = "0") int a,
            @RequestParam(required = false, defaultValue = "0") int b, Principal user) {
        System.out.print(user);
        return convertResult(a+b);
    }

    @PreAuthorize("#oauth2.hasScope('service')")
    @GetMapping("/minus")
    public Map minus(
            @RequestParam(required = false, defaultValue = "0") int a,
            @RequestParam(required = false, defaultValue = "0") int b) {
        return convertResult(a-b);
    }

    @PreAuthorize("hasAuthority('provider.times')")
    @GetMapping("/times")
    public Map times(
            @RequestParam(required = false, defaultValue = "0") int a,
            @RequestParam(required = false, defaultValue = "0") int b) {
        return convertResult(a*b);
    }

    //@PreAuthorize("permitAll()")
    @GetMapping("/division")
    public Map division(
            @RequestParam(required = false, defaultValue = "0") int a,
            @RequestParam(required = false, defaultValue = "1") int b) {
        assert b!=0;
        return convertResult(a/b);
    }

    private Map convertResult(int result){
        return Collections.singletonMap("result", result);
    }
}

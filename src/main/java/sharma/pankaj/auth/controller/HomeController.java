package sharma.pankaj.auth.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class HomeController {

    @GetMapping("/home")
    public String getAPI(){
        return "check for getapi";
    }
}

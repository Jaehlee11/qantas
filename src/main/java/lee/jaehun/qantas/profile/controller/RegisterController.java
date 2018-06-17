package lee.jaehun.qantas.profile.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lee.jaehun.qantas.profile.model.Profile;
import lee.jaehun.qantas.profile.model.Register;
import lee.jaehun.qantas.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/register")
@Api(value = "/register", description = "Operations to register users and profiles")
public class RegisterController {

    @Autowired
    private ProfileService profileService;

    @ApiOperation(value = "Create User and Profile",
            notes = "It will create a user and a profile from request body which is Register.")
    @RequestMapping(method=POST)
    public ResponseEntity<?> createCustomerProfile(@RequestBody Register register) {

        Profile profile = profileService.createProfile(register);
        if (profile == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(profile, HttpStatus.CREATED);
        }
    }
}

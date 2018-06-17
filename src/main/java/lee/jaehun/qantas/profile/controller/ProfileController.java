package lee.jaehun.qantas.profile.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lee.jaehun.qantas.profile.model.Profile;
import lee.jaehun.qantas.profile.model.User;
import lee.jaehun.qantas.profile.service.ProfileService;
import lee.jaehun.qantas.profile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/profile")
@Api(value = "/profile", description = "Operations about profiles")
public class ProfileController {

    @Autowired
    @Qualifier("userDetailsService")
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @ApiOperation(value = "Get Profile",
            notes = "It will show the profile related to a given id.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "-H Authorization: Bearer [TOKEN]",
                    dataType = "string",
                    required = true,
                    paramType = "header") })
    @RequestMapping(value="/{id}", method=GET)
    public ResponseEntity<?> getCustomerProfile(@PathVariable("id") Long id, HttpServletResponse response) {

        Optional<Profile> result = profileService.getProfile(id);
        if (result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Update Profile",
            notes = "It will update the profile of a customer with request body.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "-H Authorization: Bearer [TOKEN]",
                    dataType = "string",
                    required = true,
                    paramType = "header") })
    @RequestMapping(method=PUT)
    public Profile updateCustomerProfile(@RequestBody Profile profile, Principal principal, HttpServletResponse response) {

        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser.getId() == profile.getId()) {
            Profile result = profileService.updateProfile(profile);
            response.setStatus(HttpServletResponse.SC_RESET_CONTENT);
            return result;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return profile;
        }
    }

    @ApiOperation(value = "Delete User and Profile",
            notes = "It will remove the user and the profile related to a given id.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization",
                    value = "-H Authorization: Bearer [TOKEN]",
                    dataType = "string",
                    required = true,
                    paramType = "header") })
    @RequestMapping(value="/{id}", method=DELETE)
    public ResponseEntity<Void> deleteCustomerProfile(@PathVariable("id") Long id, Principal principal, HttpServletResponse response) {

        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser.getId() == id) {
            profileService.deleteProfile(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        }
    }
}

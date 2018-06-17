package lee.jaehun.qantas.profile.service.impl;

import lee.jaehun.qantas.profile.model.Profile;
import lee.jaehun.qantas.profile.model.Register;
import lee.jaehun.qantas.profile.model.User;
import lee.jaehun.qantas.profile.repository.ProfileRepository;
import lee.jaehun.qantas.profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements lee.jaehun.qantas.profile.service.ProfileService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Optional<Profile> getProfile(Long id) {

        return profileRepository.findById(id);
    }

    @Override
    public Profile createProfile(Register register) {

        User user = userRepository.findOneByUsername(register.getUsername());
        if (user == null) {
            user = userRepository.save(register.getUser(passwordEncoder));

            Profile profile = register.getProfile();
            profile.setId(user.getId());
            return profileRepository.save(profile);
        } else {
            return null;
        }
    }

    @Override
    public Profile updateProfile(Profile profile) {

        Optional<Profile> result = profileRepository.findById(profile.getId());
        if (result.isPresent()) {
            return profileRepository.save(profile);
        } else {
            return null;
        }
    }

    @Override
    public RESULT_TYPE deleteProfile(Long id) {

        Optional<Profile> profile = profileRepository.findById(id);
        if (profile.isPresent()) {
            profileRepository.delete(profile.get());

            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userRepository.delete(user.get());
            }
            return RESULT_TYPE.DELETE_SUCCESS;
        } else {
            return RESULT_TYPE.NOT_FOUNT;
        }
    }
}

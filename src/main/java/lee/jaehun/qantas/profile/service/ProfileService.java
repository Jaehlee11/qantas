package lee.jaehun.qantas.profile.service;

import lee.jaehun.qantas.profile.model.Profile;
import lee.jaehun.qantas.profile.model.Register;

import java.util.Optional;

public interface ProfileService {

    enum RESULT_TYPE {
        NOT_FOUNT, DELETE_SUCCESS, DELETE_FAIL;
    }

    Optional<Profile> getProfile(Long id);

    Profile createProfile(Register register);

    Profile updateProfile(Profile profile);

    RESULT_TYPE deleteProfile(Long id);
}

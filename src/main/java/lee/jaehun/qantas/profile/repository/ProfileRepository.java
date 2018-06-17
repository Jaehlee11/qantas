package lee.jaehun.qantas.profile.repository;

import lee.jaehun.qantas.profile.model.Profile;
import org.springframework.data.repository.CrudRepository;

/**
 * Access to the profile data. CrudRepository grants us convenient access methods here.
 */
public interface ProfileRepository extends CrudRepository<Profile, Long> {

}

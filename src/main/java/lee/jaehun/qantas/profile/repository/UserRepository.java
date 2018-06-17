package lee.jaehun.qantas.profile.repository;

import lee.jaehun.qantas.profile.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Access to the user data. CrudRepository grants us convenient access methods here.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Find a user by username
     *
     * @param username the user's username
     * @return user which contains the user with the given username or null.
     */
    User findOneByUsername(String username);
}

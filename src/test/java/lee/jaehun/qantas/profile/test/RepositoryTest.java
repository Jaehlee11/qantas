package lee.jaehun.qantas.profile.test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import lee.jaehun.qantas.profile.model.Address;
import lee.jaehun.qantas.profile.model.AddressType;
import lee.jaehun.qantas.profile.model.Profile;
import lee.jaehun.qantas.profile.model.User;
import lee.jaehun.qantas.profile.repository.ProfileRepository;
import lee.jaehun.qantas.profile.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testProfile() {
        List<Address> addresses = new ArrayList<>();
        Profile profileJaehun = new Profile("Jaehun", "Lee", null, addresses);
        profileJaehun.setId(1L);

        Profile profile = profileRepository.save(profileJaehun);

        Optional<Profile> result = profileRepository.findById(1L);
        assertThat(result, is(not((Optional.empty()))));
        addresses = result.get().getAddress();
        assertThat(addresses.size(), is(0));

        addresses = new ArrayList<>();
        addresses.add(new Address(AddressType.HOME,"4 Mills Ave"));
        addresses.add(new Address(AddressType.EMAIL,"jaehlee11@gmail.com"));
        profileJaehun.setAddress(addresses);

        profileRepository.save(profileJaehun);

        result = profileRepository.findById(1L);
        assertThat(result, is(not((Optional.empty()))));
        addresses = result.get().getAddress();
        assertThat(addresses.size(), is(2));

        profileRepository.delete(profileJaehun);
        result = profileRepository.findById(1L);
        assertThat(result, is((Optional.empty())));

    }

    @Test
    public void test() {
        String username = "jaehlee@gmail.com";
        User userJaehun = new User(username, "password", true);

        User user = userRepository.save(userJaehun);

        Optional<User> result = userRepository.findById(user.getId());
        assertThat(result, is(not((Optional.empty()))));
        assertThat(user.getUsername(), is(username));

        user = userRepository.findOneByUsername(username);
        assertThat(user, notNullValue());
    }
}

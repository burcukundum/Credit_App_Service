package com.patika.kredinbizdeservice.service;

import com.patika.kredinbizdeservice.exceptions.KredinbizdeException;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.producer.NotificationProducer;
import com.patika.kredinbizdeservice.producer.dto.NotificationDTO;
import com.patika.kredinbizdeservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationProducer notificationProducer;

    @Mock
    private CacheManager cacheManager;

    @Test
    public void should_create_user_successfully() {

        //given
        //Mockito.when(userRepository.save(praperaUser())).thenReturn(praperaUser()); //neden hata veriyor?

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(preparaUser()); //neden hata vermiyor
        System.out.println(preparaUser().hashCode());
        //when
        User userResponse = userService.save(preparaUser());

        //then --assertion -- doğrulama

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getName()).isEqualTo("test name");
        assertThat(userResponse.getSurname()).isEqualTo(preparaUser().getSurname());
        assertThat(userResponse.getEmail()).isEqualTo(preparaUser().getEmail());
        assertThat(userResponse.getPassword()).isEqualTo(preparaUser().getPassword());
        assertThat(userResponse.getIsActive()).isTrue();

        verify(userRepository, times(1)).save(Mockito.any(User.class));
        verify(notificationProducer, times(1)).sendNotification(Mockito.any(NotificationDTO.class));
    }

    @Test
    public void should_return_user_by_email_successfully() {

        //given
        Mockito.when(userRepository.findByEmail(preparaUser().getEmail())).thenReturn(Optional.of(preparaUser()));

        //when
        User userResponse = userService.getByEmail("test@gmail.com");

        //then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getName()).isEqualTo("test name");
        assertThat(userResponse.getSurname()).isEqualTo(preparaUser().getSurname());
        assertThat(userResponse.getEmail()).isEqualTo(preparaUser().getEmail());
        assertThat(userResponse.getPassword()).isEqualTo(preparaUser().getPassword());
        assertThat(userResponse.getIsActive()).isTrue();

        verify(userRepository, times(1)).findByEmail("test@gmail.com");
    }


    @Test
    public void should_throw_kredinbizdeException_whenUserNotFound() {
        //given

        //when
        Throwable throwable = catchThrowable(() -> userService.getByEmail("test@gmail.com"));

        //then
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("user bulunamadı!");
    }

    @Test
    public void should_throw_kredinbizdeException_whenUserNotFound2() {
        //given

        //when
        Assertions.assertThrows(KredinbizdeException.class, () -> userService.getByEmail("test"), "user bulunamadı!");

    }

    @Test
    public void should_get_all_users_successfully() {
        // given
        List<User> mockUsers = Arrays.asList(
                new User("John", "Doe"),
                new User("Jane", "Smith")
        );
        Mockito.when(userRepository.findAll()).thenReturn(mockUsers);

        // when
        List<User> retrievedUsers = userService.getAll();

        // then
        assertThat(retrievedUsers).isNotNull();
        //assertThat(retrievedUsers).hasSize(2);
        //assertThat(retrievedUsers.get(0).getId()).isEqualTo(1L);
        assertThat(retrievedUsers.get(0).getName()).isEqualTo("John");
        //assertThat(retrievedUsers.get(1).getId()).isEqualTo(2L);
        assertThat(retrievedUsers.get(1).getName()).isEqualTo("Jane");

        verify(userRepository, times(1)).findAll();

    }


    @Test
    public void should_update_user_and_put_in_cache() {

        String email = "test@example.com";
        User existingUser = new User(email,"John", "Doe");
        User updatedUser = new User(email,"Jane", "Smith");
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // When (call the update method)
        User returnedUser = userService.update(email, updatedUser);

        // Then (assertions)
        // Verify user update
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getEmail()).isEqualTo(email);
        assertThat(returnedUser.getName()).isEqualTo("Jane");
        assertThat(returnedUser.getSurname()).isEqualTo("Smith");

        // Verify cache update
        // Assuming a cache named "users" and key based on email
        Cache cache = Mockito.verify(cacheManager, times(1)).getCache("users");
        Mockito.verify(cache, times(1)).put(email, returnedUser);

        // Verify interaction with userRepository
        Mockito.verify(userRepository, times(1)).findByEmail(email);
        Mockito.verify(userRepository, times(1)).save(returnedUser); // Updated user
        // Note: userRepository.delete(user) is not called because we update the existing object
        }
    @Test
    public void should_get_user_by_id_successfully() {
        // Given
        Long userId = 1L;
        User expectedUser = new User(userId, "John", "Doe");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // When
        User returnedUser = userService.getById(userId);

        // Then
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getId()).isEqualTo(userId);
        assertThat(returnedUser.getName()).isEqualTo("John");
        assertThat(returnedUser.getSurname()).isEqualTo("Doe");

        // Verify interaction with userRepository
        Mockito.verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void should_throw_kredinbizdeException_whenUserNotFoundById() {
        // Given
        Long userId = 10L; // An ID for which no user exists
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When and Then
        Assertions.assertThrows(KredinbizdeException.class, () -> userService.getById(userId), "User bulunamadı!");

        // Verify interaction with userRepository
        Mockito.verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void should_throw_kredinbizdeException_whenUserNotFoundById2() {
        // Given
        Long userId = 20L; // Another ID for which no user exists
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Throwable throwable = catchThrowable(() -> userService.getById(userId));

        // Then
        assertThat(throwable).isInstanceOf(KredinbizdeException.class);
        assertThat(throwable.getMessage()).isEqualTo("User bulunamadı!");

        // Verify interaction with userRepository
        Mockito.verify(userRepository, times(1)).findById(userId);
    }


    private User preparaUser() {
        User user = new User();

        user.setName("test name");
        user.setSurname("test surname");
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        user.setIsActive(true);
        return user;
    }



}

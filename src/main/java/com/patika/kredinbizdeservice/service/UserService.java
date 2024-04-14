package com.patika.kredinbizdeservice.service;

import com.patika.kredinbizdeservice.exceptions.ExceptionMessages;
import com.patika.kredinbizdeservice.exceptions.KredinbizdeException;
import com.patika.kredinbizdeservice.model.Address;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.producer.NotificationProducer;
import com.patika.kredinbizdeservice.producer.dto.NotificationDTO;
import com.patika.kredinbizdeservice.producer.enums.NotificationType;
import com.patika.kredinbizdeservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Scope(value = "singleton")
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final NotificationProducer notificationProducer;

    //@CacheEvict(value = "users", allEntries = true)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW, rollbackOn = KredinbizdeException.class)
    public User save(User user) {
        System.out.println(user.hashCode());
        user.setAddress(getAddress());
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        notificationProducer.sendNotification(prepareNotificationDTO(NotificationType.EMAIL, user.getEmail()));

        return savedUser;
    }

    private Address getAddress() {
        Address address = new Address();
        address.setAddressTitle("Ev");
        address.setAddressDescription("istanbul, istanbul istanbul");
        address.setProvince("istanbul");
        return address;
    }

    private NotificationDTO prepareNotificationDTO(NotificationType notificationType, String email) {
        return NotificationDTO.builder()
                .message("user kaydedildi.")
                .notificationType(notificationType)
                .email(email)
                .build();
    }

    //@Cacheable(value = "users")
    public List<User> getAll() {
        System.out.println("userRepository: " + userRepository.hashCode());
        return userRepository.findAll();
    }

    @Cacheable(value = "users", key = "#email")
    public User getByEmail(String email) {

        log.info("user db'den alındı.");

        Optional<User> foundUser = userRepository.findByEmail(email);

        User user = foundUser.orElseThrow(() -> new KredinbizdeException(ExceptionMessages.USER_NOT_FOUND));

        if (foundUser.isPresent()) {
            user = foundUser.get();
        }

        return user;
    }

    @CachePut(value = "users", key = "#email")
    public User update(String email, User user) {

        Optional<User> foundUser = userRepository.findByEmail(email);

        foundUser.get().setName(user.getName());

        foundUser.get().setSurname(user.getSurname());

        userRepository.delete(user);

        userRepository.save(foundUser.get());

        return foundUser.get();
    }

    public User getById(Long userId) {
        return userRepository.findById(userId).get();
    }
}

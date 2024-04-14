package com.patika.kredinbizdeservice.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.patika.kredinbizdeservice.client.AkbankServiceClient;
import com.patika.kredinbizdeservice.client.dto.response.ApplicationResponse;
import com.patika.kredinbizdeservice.converter.ApplicationConverter;
import com.patika.kredinbizdeservice.dto.request.ApplicationRequest;
import com.patika.kredinbizdeservice.enums.ApplicationStatus;
import com.patika.kredinbizdeservice.model.Application;
import com.patika.kredinbizdeservice.model.User;
import com.patika.kredinbizdeservice.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationConverter applicationConverter;

    @Mock
    private UserService userService;

    @Mock
    private AkbankServiceClient akbankServiceClient;

    @InjectMocks
    private ApplicationService service;

    @Test
    public void should_application_isCreated_successfully () {

        User mockUser = Mockito.mock(User.class);
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getName()).thenReturn("John Doe");

        ApplicationRequest request = new ApplicationRequest();
        request.setEmail(mockUser.getEmail());

        Application mockApplication = new Application();
        when(userService.getByEmail(request.getEmail())).thenReturn(mockUser);
        when(applicationConverter.toApplication(request, mockUser)).thenReturn(mockApplication);
        when(applicationRepository.save(mockApplication)).thenReturn(mockApplication);
        when(akbankServiceClient.createApplication(any())).thenReturn(new ApplicationResponse()); // Assuming a void response


        Application createdApplication = service.createApplication(request);


        assertEquals(mockApplication, createdApplication);

        verify(applicationRepository).save(mockApplication);
        verify(akbankServiceClient).createApplication(any());
    }

    @Test
    public void should_create_application_with_INITIAL_status() {

        User mockUser = new User();
        ApplicationRequest request = new ApplicationRequest();
        request.setEmail(mockUser.getEmail());


        Application mockApplication = new Application();
        mockApplication.setStatus(ApplicationStatus.INITIAL);

        when(userService.getByEmail(request.getEmail())).thenReturn(mockUser);
        when(applicationConverter.toApplication(request, mockUser)).thenReturn(mockApplication);
        when(applicationRepository.save(mockApplication)).thenReturn(mockApplication);


        when(akbankServiceClient.createApplication(any())).thenReturn(new ApplicationResponse());


        Application createdApplication = service.createApplication(request);


        assertEquals(ApplicationStatus.INITIAL, createdApplication.getStatus());
    }

    @Test
     public void should_create_application_setsStatusToInProgress() {
        User mockUser = new User();
        ApplicationRequest request = new ApplicationRequest();
        request.setEmail(mockUser.getEmail());


        Application mockApplication = new Application();
        mockApplication.setStatus(ApplicationStatus.INITIAL); // Example

        when(userService.getByEmail(request.getEmail())).thenReturn(mockUser);
        when(applicationConverter.toApplication(request, mockUser)).thenReturn(mockApplication);
        when(applicationRepository.save(mockApplication)).thenReturn(mockApplication);


        ApplicationResponse akbankResponse = new ApplicationResponse();
        akbankResponse.setApplicationId(123L); // Set a sample application ID
        when(akbankServiceClient.createApplication(any())).thenReturn(akbankResponse);


        Application createdApplication = service.createApplication(request);


        assertEquals(ApplicationStatus.IN_PROGRESS, createdApplication.getStatus());

    }

    @Test
    public void should_create_application_setsStatusToDone() {

        User mockUser = new User();
        ApplicationRequest request = new ApplicationRequest();
        request.setEmail(mockUser.getEmail());


        Application mockApplication = new Application();
        mockApplication.setStatus(ApplicationStatus.INITIAL);

        when(userService.getByEmail(request.getEmail())).thenReturn(mockUser);
        when(applicationConverter.toApplication(request, mockUser)).thenReturn(mockApplication);
        when(applicationRepository.save(mockApplication)).thenReturn(mockApplication);


        when(akbankServiceClient.createApplication(any())).thenReturn(new ApplicationResponse());


        Application createdApplication = service.createApplication(request);


        assertEquals(ApplicationStatus.DONE, createdApplication.getStatus());

    }

}


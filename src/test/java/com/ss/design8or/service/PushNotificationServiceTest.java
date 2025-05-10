package com.ss.design8or.service;

import com.ss.design8or.controller.request.SubscriptionKey;
import com.ss.design8or.controller.request.SubscriptionRequest;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.Subscription;
import com.ss.design8or.repository.SubscriptionRepository;
import com.ss.design8or.service.communication.PushNotificationService;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.security.Security;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PushNotificationServiceTest {

    @MockitoBean
    private PushService pushService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private PushNotificationService pushNotificationService;

    @BeforeEach
    public void init() {
        pushNotificationService = new PushNotificationService(pushService, subscriptionRepository);
    }

    @Test
    public void createSubscription() {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .endpoint("https://example.com")
                .keys(SubscriptionKey.builder().auth("authKey").p256dh("p256dhKey").build())
                .build();
        Subscription subscription = pushNotificationService.createSubscription(request);
        assertThat(subscription.getEndpoint()).isEqualTo("https://example.com");
        assertThat(subscription.getAuth()).isEqualTo("authKey");
        assertThat(subscription.getP256dh()).isEqualTo("p256dhKey");
        assertThat(subscriptionRepository.findByEndpoint("https://example.com")).isPresent();
        assertThat(subscription.getId()).isNotNull();
    }

    @Test
    public void getSubscriptions() {
        Page<Subscription> subscriptionsPage = pushNotificationService.getSubscriptions(
                PageRequest.of(0, 1));
        assertThat(subscriptionsPage.getTotalElements()).isEqualTo(1);
        assertThat(subscriptionsPage.getContent().getFirst().getId()).isEqualTo(1);
    }

    @Test
    public void sendAssignmentNotification() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        pushNotificationService.sendAssignmentNotification("some@test.com");
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(1L);
        assertThat(subscriptionOpt.isPresent()).isTrue();
        Subscription subscription = subscriptionOpt.get();
        ArgumentCaptor<Notification> dataCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(pushService, times(1)).sendAsync(dataCaptor.capture());
        Notification notification = dataCaptor.getValue();
        assertThat(notification.getEndpoint()).isEqualTo(subscription.getEndpoint());
        assertThat(new String(notification.getPayload())).isEqualTo("some@test.com is the new lead");
    }

    @Test
    public void sendNotificationMissingSubscription() {
        assertThrows(ResourceNotFoundException.class, () -> {
            pushNotificationService.sendNotification(0L, "");
        });
    }

    @Test
    public void sendNotification() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        when(pushService.send(any())).thenReturn(mock(HttpResponse.class));
        pushNotificationService.sendNotification(1L, "some@test.com is the new lead");
        verify(pushService, times(1)).send(any());
    }

}
/* eslint-disable no-restricted-globals */

// Service Worker for handling push notifications
self.addEventListener('push', (event) => {
    console.log('[Service Worker] Push notification received:', event);

    let notificationData = {
        title: 'Desig8or Notification',
        body: 'You have a new notification',
        icon: '/favicon.ico',
        badge: '/favicon.ico'
    };

    if (event.data) {
        try {
            console.log('[Service Worker] Raw push data:', event.data.text());
            const data = event.data.json();
            console.log('[Service Worker] Parsed push data:', data);

            notificationData = {
                title: data.title || notificationData.title,
                body: data.body || data.message || notificationData.body,
                icon: data.icon || notificationData.icon,
                badge: data.badge || notificationData.badge,
                data: data.url ? { url: data.url } : undefined
            };
        } catch (e) {
            console.error('[Service Worker] Error parsing push notification data:', e);
            notificationData.body = event.data.text();
        }
    } else {
        console.log('[Service Worker] Push event has no data');
    }

    console.log('[Service Worker] Showing notification:', notificationData);

    const promiseChain = self.registration.showNotification(
        notificationData.title,
        {
            body: notificationData.body,
            icon: notificationData.icon,
            badge: notificationData.badge,
            data: notificationData.data,
            requireInteraction: false,
            tag: 'desig8or-notification'
        }
    );

    event.waitUntil(promiseChain);
});

self.addEventListener('notificationclick', (event) => {
    console.log('Notification clicked:', event);
    event.notification.close();

    if (event.notification.data && event.notification.data.url) {
        event.waitUntil(
            clients.openWindow(event.notification.data.url)
        );
    } else {
        event.waitUntil(
            clients.openWindow('/')
        );
    }
});

self.addEventListener('install', (event) => {
    console.log('Service Worker installing.');
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    console.log('Service Worker activating.');
    event.waitUntil(clients.claim());
});

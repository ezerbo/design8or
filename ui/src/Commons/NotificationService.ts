import axios from 'axios';
import { API_BASE_URL } from './Paths';

interface SubscriptionRequest {
    endpoint: string;
    keys: {
        auth: string;
        p256dh: string;
    };
}

export class NotificationService {
    private static publicKey: string | null = null;

    /**
     * Get the VAPID public key from the server
     */
    static async getPublicKey(): Promise<string> {
        if (this.publicKey) {
            return this.publicKey;
        }

        try {
            const response = await axios.get(
                `${API_BASE_URL}/configurations/key/browser-push-notification.public-key`
            );
            const publicKey = response.data.value;
            if (!publicKey) {
                throw new Error('VAPID public key not found in response');
            }
            this.publicKey = publicKey;
            return publicKey;
        } catch (error) {
            console.error('Error fetching VAPID public key:', error);
            throw error;
        }
    }

    /**
     * Convert a base64 string to Uint8Array (needed for VAPID key)
     */
    static urlBase64ToUint8Array(base64String: string): Uint8Array {
        const padding = '='.repeat((4 - base64String.length % 4) % 4);
        const base64 = (base64String + padding)
            .replace(/-/g, '+')
            .replace(/_/g, '/');

        const rawData = window.atob(base64);
        const outputArray = new Uint8Array(rawData.length);

        for (let i = 0; i < rawData.length; ++i) {
            outputArray[i] = rawData.charCodeAt(i);
        }
        return outputArray;
    }

    /**
     * Register service worker
     */
    static async registerServiceWorker(): Promise<ServiceWorkerRegistration | null> {
        if ('serviceWorker' in navigator) {
            try {
                const registration = await navigator.serviceWorker.register('/service-worker.js');
                console.log('Service Worker registered:', registration);
                return registration;
            } catch (error) {
                console.error('Service Worker registration failed:', error);
                return null;
            }
        }
        console.warn('Service Workers are not supported in this browser');
        return null;
    }

    /**
     * Request notification permission from user
     */
    static async requestPermission(): Promise<NotificationPermission> {
        if (!('Notification' in window)) {
            console.warn('Notifications are not supported in this browser');
            return 'denied';
        }

        if (Notification.permission === 'granted') {
            return 'granted';
        }

        if (Notification.permission !== 'denied') {
            const permission = await Notification.requestPermission();
            return permission;
        }

        return Notification.permission;
    }

    /**
     * Subscribe to push notifications
     */
    static async subscribe(): Promise<boolean> {
        try {
            // Request permission
            const permission = await this.requestPermission();
            if (permission !== 'granted') {
                console.log('Notification permission denied');
                return false;
            }

            // Register service worker
            const registration = await this.registerServiceWorker();
            if (!registration) {
                console.error('Could not register service worker');
                return false;
            }

            // Get existing subscription
            let subscription = await registration.pushManager.getSubscription();

            // If already subscribed, send to server and return
            if (subscription) {
                console.log('Already subscribed to push notifications');
                await this.sendSubscriptionToServer(subscription);
                return true;
            }

            // Get VAPID public key
            const publicKey = await this.getPublicKey();
            const applicationServerKey = this.urlBase64ToUint8Array(publicKey);

            // Subscribe to push manager
            subscription = await registration.pushManager.subscribe({
                userVisibleOnly: true,
                applicationServerKey: applicationServerKey
            });

            console.log('Subscribed to push notifications:', subscription);

            // Send subscription to server
            await this.sendSubscriptionToServer(subscription);

            return true;
        } catch (error) {
            console.error('Error subscribing to push notifications:', error);
            return false;
        }
    }

    /**
     * Send subscription to server
     */
    static async sendSubscriptionToServer(subscription: PushSubscription): Promise<void> {
        try {
            const subscriptionJson = subscription.toJSON();
            
            const request: SubscriptionRequest = {
                endpoint: subscriptionJson.endpoint || '',
                keys: {
                    auth: subscriptionJson.keys?.auth || '',
                    p256dh: subscriptionJson.keys?.p256dh || ''
                }
            };

            await axios.post(`${API_BASE_URL}/notifications`, request);
            console.log('Subscription sent to server successfully');
        } catch (error) {
            // Check if it's a duplicate subscription error (400 with "already exists" message)
            if (axios.isAxiosError(error) &&
                (error.response?.status === 400 || error.response?.status === 409) &&
                error.response?.data?.message?.includes('already exists')) {
                console.log('Subscription already exists on server');
            } else {
                console.error('Error sending subscription to server:', error);
                throw error;
            }
        }
    }

    /**
     * Unsubscribe from push notifications
     */
    static async unsubscribe(): Promise<boolean> {
        try {
            const registration = await navigator.serviceWorker.getRegistration();
            if (!registration) {
                return false;
            }

            const subscription = await registration.pushManager.getSubscription();
            if (!subscription) {
                return false;
            }

            const successful = await subscription.unsubscribe();
            console.log('Unsubscribed from push notifications');
            return successful;
        } catch (error) {
            console.error('Error unsubscribing from push notifications:', error);
            return false;
        }
    }

    /**
     * Check if user is subscribed
     */
    static async isSubscribed(): Promise<boolean> {
        try {
            const registration = await navigator.serviceWorker.getRegistration();
            if (!registration) {
                return false;
            }

            const subscription = await registration.pushManager.getSubscription();
            return subscription !== null;
        } catch (error) {
            console.error('Error checking subscription status:', error);
            return false;
        }
    }
}

const PUBLIC_VAPID_KEY = "BOsT9tYV7LuR21EAs5AZ6qCwb9zsD8RRApInBOwVJwD5EOzYfoyPfbP0M0CYXI14lscch43JSiPO45b-B_n-dJo";

async function registerPush() {
    if (!("serviceWorker" in navigator)) return;
    const registration = await navigator.serviceWorker.register("/sw.js");

    const subscription = await registration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: urlBase64ToUint8Array(PUBLIC_VAPID_KEY),
    });

    console.log(JSON.stringify(subscription));
}

function urlBase64ToUint8Array(base64String) {
    const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding).replace(/\-/g, "+").replace(/_/g, "/");

    const rawData = atob(base64);
    return Uint8Array.from([...rawData].map(char => char.charCodeAt(0)));
}

async function handleSubscribe() {
    const permission = await Notification.requestPermission();
    if (permission === "granted") {
        await registerPush();
        alert("Subscribed to push notifications!");
    }
}
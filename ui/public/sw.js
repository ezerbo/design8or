/* eslint-disable no-restricted-globals */
self.addEventListener("push", (event) => {
    const data = event.data
    console.log(JSON.stringify(data));
    self.registration.showNotification(data.title, {
        body: data.body
    }).catch((error) => {
        console.error("Error showing notification:", error);
    })

});
/* eslint-enable no-restricted-globals */
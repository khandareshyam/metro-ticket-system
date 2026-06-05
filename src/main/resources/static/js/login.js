const API = "/api";

let confirmationResult;

/* SEND OTP */
async function sendOtp() {
firebase.auth().settings.appVerificationDisabledForTesting = true;
    const mobile =
        document.getElementById("mobile")
        .value
        .trim();

    if (!mobile) {
        alert("Enter mobile number");
        return;
    }

    try {

        if (!window.recaptchaVerifier) {

   if (!window.recaptchaVerifier) {

    window.recaptchaVerifier =
        new firebase.auth.RecaptchaVerifier(
            "recaptcha-container",
            {
                size: "normal"
            }
        );

    await window.recaptchaVerifier.render();
}
}

        const phoneNumber =
            "+91" + mobile;

        confirmationResult =
            await firebase.auth()
                .signInWithPhoneNumber(
                    phoneNumber,
                    window.recaptchaVerifier
                );

        document
            .getElementById("otpBox")
            .classList
            .remove("hidden");

        alert("OTP sent");

    } catch (e) {

        console.error(e);

        alert("Failed to send OTP");
    }
}

/* VERIFY OTP */
async function verifyOtp() {

    const mobile =
        document.getElementById("mobile")
        .value
        .trim();

    const otp =
        document.getElementById("otp")
        .value
        .trim();

    if (!otp) {
        alert("Enter OTP");
        return;
    }

    try {

        await confirmationResult.confirm(otp);

        const res =
            await fetch(
                `${API}/auth/firebase-login`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type":
                        "application/x-www-form-urlencoded"
                    },
                    body:
                    `mobile=${encodeURIComponent(mobile)}`,
                    credentials: "include"
                });

        if (!res.ok) {
            throw new Error();
        }

        window.location.href =
            "/dashboard.html";

    } catch (e) {

        console.error(e);

        alert("Invalid OTP");
    }
}
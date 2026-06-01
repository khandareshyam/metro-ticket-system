const API = "/api";

/* =====================
   SEND OTP
===================== */
function sendOtp() {
    const mobileInput = document.getElementById("mobile");
    const mobile = mobileInput.value.trim();

    if (!mobile) {
        alert("Enter mobile number");
        return;
    }

    fetch(`${API}/auth/send-otp`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `mobile=${encodeURIComponent(mobile)}`,
        credentials: "include" // 🔴 REQUIRED
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("OTP send failed");
        }
        return res.text();
    })
    .then(() => {
        document.getElementById("otpBox").classList.remove("hidden");
    })
    .catch(() => {
        alert("Failed to send OTP");
    });
}

/* =====================
   VERIFY OTP
===================== */
function verifyOtp() {
    const mobile = document.getElementById("mobile").value.trim();
    const otp = document.getElementById("otp").value.trim();

    if (!otp) {
        alert("Enter OTP");
        return;
    }

    fetch(`${API}/auth/verify-otp`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `mobile=${encodeURIComponent(mobile)}&otp=${encodeURIComponent(otp)}`,
        credentials: "include" // 🔴 REQUIRED
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Invalid OTP");
        }
        return res.text();
    })
    .then(() => {
        // session is now created
        window.location.href = "/index.html";
    })
    .catch(() => {
        alert("Invalid OTP");
    });
}
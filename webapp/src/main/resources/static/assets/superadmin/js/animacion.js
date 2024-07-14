// Selección de elementos del DOM
const sign_in_btn = document.querySelector("#sign-in-btn");
const sign_up_btn = document.querySelector("#sign-up-btn");
const container = document.querySelector(".container");
const sign_in_btn2 = document.querySelector("#sign-in-btn2");
const sign_up_btn2 = document.querySelector("#sign-up-btn2");

// Funciones para cambiar entre inicio de sesión y registro
function mostrarRegistro() {
    container.classList.add("sign-up-mode");
}

function mostrarInicioSesion() {
    container.classList.remove("sign-up-mode");
}

function mostrarRegistro2() {
    container.classList.add("sign-up-mode2");
}

function mostrarInicioSesion2() {
    container.classList.remove("sign-up-mode2");
}

// Eventos para cambiar entre inicio de sesión y registro
sign_up_btn.addEventListener("click", mostrarRegistro);
sign_in_btn.addEventListener("click", mostrarInicioSesion);
sign_up_btn2.addEventListener("click", mostrarRegistro2);
sign_in_btn2.addEventListener("click", mostrarInicioSesion2);

document.addEventListener("DOMContentLoaded", function () {
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("login-password");
    const emailError = document.getElementById("email-error");
    const passwordError = document.getElementById("password-error");
    const loginButton = document.getElementById("login-button");

    emailInput.addEventListener("input", function () {
        const emailValue = emailInput.value.trim();
        const regexEmail = /^[a-zA-Z0-9_À-ÿ.-]+@[^@\s]+\.com$/;
 // Permitir caracteres latinos y otros especiales en el email antes de @pucp.edu.pe

        if (emailValue.length === 0) {
            emailError.textContent = "Campo vacío";
            emailError.classList.add("error-message");
        } else if (!regexEmail.test(emailValue)) {
            emailError.textContent = "Correo no válido";
            emailError.classList.add("error-message");
        } else {
            emailError.textContent = ""; // Limpiar el mensaje de error si no hay problema con el correo
            emailError.classList.remove("error-message");
        }
    });

    passwordInput.addEventListener("input", function () {
        const passwordValue = passwordInput.value.trim();
        const regexPassword = /^[a-zA-Z0-9ÑñÁáÉéÍíÓóÚú@]+$/; // Se agregan los caracteres latinos y el símbolo @

        if (passwordValue.length === 0) {
            passwordError.textContent = "Campo vacío";
            passwordError.classList.add("error-message");   
        } else if (passwordValue.length < 8) {
            passwordError.textContent = "Min 8 caracteres";
            passwordError.classList.add("error-message");
        } else {
            passwordError.textContent = ""; // Limpiar el mensaje de error si no hay problema con la contraseña
            passwordError.classList.remove("error-message");
        }
    });



    loginButton.addEventListener("click", function (e) {
        const emailValue = emailInput.value.trim();
        const passwordValue = passwordInput.value.trim();

        const regexEmail = /^[a-zA-Z0-9_À-ÿ.-]+@[^@\s]+\.com$/;// Permite caracteres alfanuméricos, guiones, puntos y guiones bajos antes de @pucp.edu.pe
        if (!emailValue) {
            e.preventDefault();
            emailError.textContent = "Campo vacío";
            emailError.classList.add("error-message");
        } else if (!regexEmail.test(emailValue)) {
            e.preventDefault();
            emailError.textContent = "Correo no válido";
            emailError.classList.add("error-message");
        } else {
            emailError.textContent = ""; // Limpiar el mensaje de error si no hay problema con el correo
            emailError.classList.remove("error-message");
        }

        const regexPassword = /^[a-zA-Z0-9ÑñÁáÉéÍíÓóÚú@]+$/; // Se agregan los caracteres latinos y el símbolo @
        if (!passwordValue.trim()) {
            e.preventDefault();
            passwordError.textContent = "Campo vacío";
            passwordError.classList.add("error-message");
        } else if (passwordValue.length < 8) {
            e.preventDefault();
            passwordError.textContent = "Min 8 caracteres";
            passwordError.classList.add("error-message");
        } else {
            passwordError.textContent = ""; // Limpiar el mensaje de error si no hay problema con la contraseña
            passwordError.classList.remove("error-message");
        }
    });





    document.addEventListener("DOMContentLoaded", function () {
        // ...

        const loginForm = document.querySelector("#login-form");
        loginForm.addEventListener("submit", function (e) {
            e.preventDefault(); // Evita el envío del formulario

            // Validar los campos de correo y contraseña
            const emailInput = document.querySelector("#email");
            const passwordInput = document.querySelector("#login-password");

            // Aquí puedes agregar tu lógica de validación personalizada
            // Por ejemplo, verificar si los campos están vacíos o si cumplen ciertos criterios

            // Si los campos son válidos, envía el formulario
            loginForm.submit();
        });

        // ...
    });

});



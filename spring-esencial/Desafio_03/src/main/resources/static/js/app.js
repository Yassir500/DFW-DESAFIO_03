// URL base de tu API
const API_URL = "http://localhost:8080/api";

// Almacenamos el token en localStorage para persistencia
let token = localStorage.getItem("token") || "";

// Al cargar la página, verificamos si ya hay un token
document.addEventListener("DOMContentLoaded", () => {
    if (token) {
        mostrarApp();
    }
});

// --- AUTENTICACIÓN ---

async function login() {
    const user = document.getElementById("user").value;
    const pass = document.getElementById("pass").value;
    const errorMsg = document.getElementById("login-error");

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: user, password: pass })
        });

        const data = await response.json();

        if (response.ok) {
            token = data.token;
            localStorage.setItem("token", token);
            alert("Login exitoso");
            mostrarApp();
        } else {
            errorMsg.innerText = data.error || "Credenciales inválidas";
        }
    } catch (err) {
        errorMsg.innerText = "Error de conexión con el servidor";
    }
}

function mostrarApp() {
    document.getElementById("login-section").style.display = "none";
    document.getElementById("main-content").style.display = "block";
    getEvents();
    getMyBookings();
}

function logout() {
    localStorage.removeItem("token");
    location.reload();
}

// --- GESTIÓN DE EVENTOS ---

async function getEvents() {
    try {
        const res = await fetch(`${API_URL}/events`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (res.status === 401 || res.status === 403) {
            alert("Sesión expirada");
            logout();
            return;
        }

        const data = await res.json();
        const container = document.getElementById("eventos-list");
        container.innerHTML = "";

        data.forEach(e => {
            // 🔥 SOLUCIÓN AL ERROR NULL: Detecta el ID sin importar el formato del JSON
            const idActual = e.idEvent || e.id_event;
            const precio = e.pricePerTicket || e.price_per_ticket;

            container.innerHTML += `
                <div class="card">
                    <h3>${e.title}</h3>
                    <p>${e.description}</p>
                    <p><b>Precio:</b> $${precio} | <b>Lugar:</b> ${e.venue}</p>
                    <p><b>Cupos disponibles:</b> ${e.capacity}</p>
                    <input type="number" id="qty-${idActual}" value="1" min="1" style="width:60px">
                    <button onclick="bookEvent(${idActual})">Reservar Entradas</button>
                </div>
            `;
        });
    } catch (err) {
        console.error("Error al cargar eventos:", err);
    }
}

// --- GESTIÓN DE RESERVAS (BOOKINGS) ---

async function bookEvent(eventId) {
    // Verificación de seguridad en el cliente
    if (!eventId) {
        alert("Error: El ID del evento no se cargó correctamente.");
        return;
    }

    const qtyInput = document.getElementById(`qty-${eventId}`);
    const qty = qtyInput ? qtyInput.value : 1;

    try {
        const res = await fetch(`${API_URL}/bookings`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                event_id: parseInt(eventId), // Se envía como event_id para el @JsonProperty
                quantity: parseInt(qty)
            })
        });

        const data = await res.json();

        if (res.ok) {
            // El backend devuelve el total calculado automáticamente
            alert(`Reserva confirmada. Total: $${data.total_amount}`);
            getMyBookings();
            getEvents();
        } else {
            // Captura el mensaje de error del backend (ej: "No hay cupos")
            alert("No se pudo realizar la reserva: " + (data.message || data.error || "Error interno"));
        }
    } catch (err) {
        alert("Error al procesar la reserva. Revisa la consola.");
        console.error(err);
    }
}

async function getMyBookings() {
    try {
        const res = await fetch(`${API_URL}/bookings/my`, {
            headers: { "Authorization": `Bearer ${token}` }
        });
        const data = await res.json();
        const container = document.getElementById("mis-reservas");
        container.innerHTML = "";

        data.forEach(b => {
            const isConfirmed = b.status === 'CONFIRMED';
            // Manejo de IDs para las reservas
            const idReserva = b.idBooking || b.id_booking;
            const total = b.totalAmount || b.total_amount;

            container.innerHTML += `
                <div class="card" style="border-left: 5px solid ${isConfirmed ? 'green' : 'red'}">
                    <p><b>Reserva #${idReserva}</b> - Estado: ${b.status}</p>
                    <p>Cantidad: ${b.quantity} | Total Pagado: $${total}</p>
                    ${isConfirmed ? `<button onclick="cancelBooking(${idReserva})" style="background:red; color:white;">Cancelar Reserva</button>` : ''}
                </div>
            `;
        });
    } catch (err) {
        console.error("Error al cargar mis reservas:", err);
    }
}

async function cancelBooking(id) {
    if (!confirm("¿Seguro que deseas cancelar esta reserva?")) return;

    try {
        const res = await fetch(`${API_URL}/bookings/${id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (res.ok) {
            alert("Reserva cancelada correctamente");
            getMyBookings();
            getEvents();
        }
    } catch (err) {
        alert("Error al cancelar");
    }
}
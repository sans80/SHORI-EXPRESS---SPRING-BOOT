// Validaciones y efectos para formularios de autenticación
document.addEventListener('DOMContentLoaded', function() {
    
    // Elementos del formulario
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    
    // Validación en tiempo real para el formulario de registro
    if (registerForm) {
        const correoInput = document.getElementById('correoUsuario');
        const usuarioInput = document.getElementById('nombreUsuario');
        const documentoInput = document.getElementById('documentoUsuario');
        const contraseñaInput = document.getElementById('contraseñaUsuario');
        
        // Validar correo en tiempo real
        if (correoInput) {
            correoInput.addEventListener('blur', function() {
                const correo = this.value.trim();
                if (correo && isValidEmail(correo)) {
                    verificarDisponibilidad('/auth/verificar-correo', 'correo', correo, this);
                }
            });
        }
        
        // Validar nombre de usuario en tiempo real
        if (usuarioInput) {
            usuarioInput.addEventListener('blur', function() {
                const usuario = this.value.trim();
                if (usuario && usuario.length >= 3) {
                    verificarDisponibilidad('/auth/verificar-usuario', 'nombreUsuario', usuario, this);
                }
            });
        }
        
        // Validar documento en tiempo real
        if (documentoInput) {
            documentoInput.addEventListener('blur', function() {
                const documento = this.value.trim();
                if (documento) {
                    verificarDisponibilidad('/auth/verificar-documento', 'documento', documento, this);
                }
            });
        }
        
        // Validar fortaleza de contraseña
        if (contraseñaInput) {
            contraseñaInput.addEventListener('input', function() {
                validarContraseña(this.value, this);
            });
        }
    }
    
    // Efectos de carga para botones
    if (loginForm && loginBtn) {
        loginForm.addEventListener('submit', function() {
            mostrarCarga(loginBtn, 'Iniciando sesión...');
        });
    }
    
    if (registerForm && registerBtn) {
        registerForm.addEventListener('submit', function(e) {
            if (!validarFormularioRegistro()) {
                e.preventDefault();
                return;
            }
            mostrarCarga(registerBtn, 'Creando cuenta...');
        });
    }
    
    // Animaciones de entrada
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';
        
        setTimeout(() => {
            card.style.transition = 'all 0.6s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 200);
    });
});

// Función para validar email
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Función para verificar disponibilidad
async function verificarDisponibilidad(url, param, valor, input) {
    try {
        const response = await fetch(`${url}?${param}=${encodeURIComponent(valor)}`);
        const disponible = await response.json();
        
        const existingMessage = input.parentNode.querySelector('.validation-message');
        if (existingMessage) {
            existingMessage.remove();
        }
        
        const message = document.createElement('span');
        message.className = 'validation-message';
        message.style.fontSize = '14px';
        message.style.marginTop = '5px';
        message.style.display = 'block';
        
        if (disponible) {
            message.style.color = '#28a745';
            message.textContent = '✓ Disponible';
            input.classList.remove('error');
        } else {
            message.style.color = '#dc3545';
            message.textContent = '✗ Ya está en uso';
            input.classList.add('error');
        }
        
        input.parentNode.appendChild(message);
        
    } catch (error) {
        console.error('Error al verificar disponibilidad:', error);
    }
}

// Función para validar fortaleza de contraseña
function validarContraseña(contraseña, input) {
    const existingMessage = input.parentNode.querySelector('.password-strength');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    if (contraseña.length === 0) return;
    
    const message = document.createElement('div');
    message.className = 'password-strength';
    message.style.fontSize = '12px';
    message.style.marginTop = '5px';
    
    let strength = 0;
    let feedback = [];
    
    if (contraseña.length >= 6) strength++;
    else feedback.push('Al menos 6 caracteres');
    
    if (/[A-Z]/.test(contraseña)) strength++;
    else feedback.push('Una mayúscula');
    
    if (/[a-z]/.test(contraseña)) strength++;
    else feedback.push('Una minúscula');
    
    if (/[0-9]/.test(contraseña)) strength++;
    else feedback.push('Un número');
    
    if (/[^A-Za-z0-9]/.test(contraseña)) strength++;
    else feedback.push('Un carácter especial');
    
    const colors = ['#dc3545', '#fd7e14', '#ffc107', '#28a745', '#198754'];
    const labels = ['Muy débil', 'Débil', 'Regular', 'Fuerte', 'Muy fuerte'];
    
    message.style.color = colors[strength - 1] || colors[0];
    message.innerHTML = `
        <div>Fortaleza: ${labels[strength - 1] || labels[0]}</div>
        ${feedback.length > 0 ? `<div style="font-size: 11px; opacity: 0.8;">Falta: ${feedback.join(', ')}</div>` : ''}
    `;
    
    input.parentNode.appendChild(message);
}

// Función para mostrar estado de carga
function mostrarCarga(button, texto) {
    const originalText = button.querySelector('.btn-text').textContent;
    button.disabled = true;
    button.classList.add('loading');
    button.querySelector('.btn-text').innerHTML = `
        <span class="spinner"></span>
        ${texto}
    `;
    
    // Restaurar después de 10 segundos si no hay redirección
    setTimeout(() => {
        button.disabled = false;
        button.classList.remove('loading');
        button.querySelector('.btn-text').textContent = originalText;
    }, 10000);
}

// Función para validar formulario de registro completo
function validarFormularioRegistro() {
    const form = document.getElementById('registerForm');
    const inputs = form.querySelectorAll('input[required], select[required]');
    let valido = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('error');
            valido = false;
        } else {
            input.classList.remove('error');
        }
    });
    
    // Validar email
    const emailInput = document.getElementById('correoUsuario');
    if (emailInput && !isValidEmail(emailInput.value)) {
        emailInput.classList.add('error');
        valido = false;
    }
    
    // Validar contraseña
    const passwordInput = document.getElementById('contraseñaUsuario');
    if (passwordInput && passwordInput.value.length < 6) {
        passwordInput.classList.add('error');
        valido = false;
    }
    
    if (!valido) {
        // Mostrar mensaje de error general
        let errorDiv = document.querySelector('.validation-error');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'alert alert-danger validation-error';
            errorDiv.textContent = 'Por favor, completa todos los campos requeridos correctamente.';
            form.insertBefore(errorDiv, form.firstChild);
        }
        
        // Scroll al primer error
        const firstError = form.querySelector('.error');
        if (firstError) {
            firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    } else {
        // Remover mensaje de error si existe
        const errorDiv = document.querySelector('.validation-error');
        if (errorDiv) {
            errorDiv.remove();
        }
    }
    
    return valido;
}

// Función para auto-ocultar alertas
setTimeout(() => {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        alert.style.transition = 'all 0.5s ease';
        alert.style.opacity = '0';
        alert.style.transform = 'translateY(-20px)';
        
        setTimeout(() => {
            alert.remove();
        }, 500);
    });
}, 5000);

// Prevenir envío múltiple de formularios
const forms = document.querySelectorAll('form');
forms.forEach(form => {
    form.addEventListener('submit', function() {
        const submitBtn = this.querySelector('button[type="submit"]');
        if (submitBtn) {
            setTimeout(() => {
                submitBtn.disabled = true;
            }, 100);
        }
    });
});
// ✨ NUEVO: Llamar al backend ML en lugar de cálculo local
document.getElementById('alumnoForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    // Obtener datos del formulario
    const formData = new FormData(e.target);
    const studentData = {
        nombre: formData.get('nombre') || 'Estudiante ' + Date.now(),
        nota_media: parseFloat(formData.get('notaMedia')),
        asistencia: parseFloat(formData.get('asistencia')),
        nota_practicas: parseFloat(formData.get('notaPracticas')),
        ra_completados: parseInt(formData.get('raCompletados')),
        participacion: parseInt(formData.get('participacion'))
    };

    try {
        // 🎯 LLAMADA AL BACKEND ML
        const response = await fetch('/api/predict', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(studentData)
        });

        if (!response.ok) {
            throw new Error('Error en la predicción');
        }

        const result = await response.json();

        // Mostrar resultado del ML
        const probabilidad = Math.round(result.probabilidad_aprobado * 100);
        document.getElementById('resultPercentage').textContent = probabilidad + '%';

        // Badge según riesgo del ML
        const badgeClasses = {
            'Bajo': 'success',
            'Medio': 'warning',
            'Alto': 'danger',
            'Crítico': 'danger'
        };

        const badgeClass = badgeClasses[result.riesgo] || 'secondary';
        document.getElementById('resultStatus').innerHTML =
            `<span class="badge ${badgeClass}">${result.riesgo}</span>`;

        // Ocultar formulario y mostrar resultado
        document.getElementById('form').style.display = 'none';
        document.getElementById('resultSection').style.display = 'block';
        document.getElementById('resultSection').scrollIntoView({ behavior: 'smooth' });

    } catch (error) {
        console.error('Error:', error);
        alert('Error al realizar la predicción. ¿Está el modelo entrenado?');
    }
});

// Funciones auxiliares (mantener igual)
function resetForm() {
    document.getElementById('alumnoForm').reset();
}

function closeResult() {
    document.getElementById('resultSection').style.display = 'none';
    document.getElementById('form').style.display = 'block';
    document.getElementById('form').scrollIntoView({ behavior: 'smooth' });
}

function goToList() {
    document.getElementById('listado').scrollIntoView({ behavior: 'smooth' });
}

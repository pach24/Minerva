// Simulación de cálculo de predicción
document.getElementById('alumnoForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    // Obtener datos del formulario
    const formData = new FormData(e.target);
    const notaMedia = parseFloat(formData.get('notaMedia'));
    const asistencia = parseFloat(formData.get('asistencia'));
    const notaPracticas = parseFloat(formData.get('notaPracticas'));
    const raCompletados = parseInt(formData.get('raCompletados'));
    const participacion = parseInt(formData.get('participacion'));
    
    // Algoritmo simulado de predicción
    let probabilidad = 0;
    probabilidad += notaMedia * 10;
    probabilidad += asistencia * 0.3;
    probabilidad += notaPracticas * 5;
    probabilidad += raCompletados * 2;
    probabilidad += participacion * 3;
    probabilidad = Math.min(Math.round(probabilidad / 2), 99);
    
    // Determinar estado
    let estado = '';
    let badgeClass = '';
    if (probabilidad >= 75) {
        estado = 'Aprobado';
        badgeClass = 'success';
    } else if (probabilidad >= 50) {
        estado = 'Riesgo Medio';
        badgeClass = 'warning';
    } else {
        estado = 'Riesgo Alto';
        badgeClass = 'danger';
    }
    
    // Mostrar resultado
    document.getElementById('resultPercentage').textContent = probabilidad + '%';
    document.getElementById('resultStatus').innerHTML = `<span class="badge ${badgeClass}">${estado}</span>`;
    
    // Ocultar formulario y mostrar resultado
    document.getElementById('form').style.display = 'none';
    document.getElementById('resultSection').style.display = 'block';
    
    // Scroll al resultado
    document.getElementById('resultSection').scrollIntoView({ behavior: 'smooth' });
});

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

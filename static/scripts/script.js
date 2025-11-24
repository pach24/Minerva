const navbar = document.getElementById('navbar');
const dropdownArea = document.getElementById('dropdown-area');
const dropdownTriggers = document.querySelectorAll('[data-dropdown]');
let hoverTimeout;
let isDropdownOpen = false;

// Scroll effect
window.addEventListener('scroll', () => {
    if (navbar) {
        navbar.classList.toggle('scrolled', window.scrollY > 10);
    }
});

// Dropdown functionality
if (dropdownTriggers && dropdownArea) {
    dropdownTriggers.forEach(trigger => {
        trigger.addEventListener('mouseenter', (e) => {
            clearTimeout(hoverTimeout);
            const targetId = e.target.getAttribute('data-dropdown') + '-content';
            showDropdown(targetId);
        });
        
        trigger.parentElement.addEventListener('mouseleave', () => {
            hoverTimeout = setTimeout(() => {
                if(!isDropdownOpen) {
                    hideDropdown();
                }
            }, 100);
        });
    });

    dropdownArea.addEventListener('mouseenter', () => {
        clearTimeout(hoverTimeout);
        isDropdownOpen = true;
    });

    dropdownArea.addEventListener('mouseleave', () => {
        isDropdownOpen = false;
        hoverTimeout = setTimeout(hideDropdown, 100);
    });
}

function showDropdown(targetId) {
    document.querySelectorAll('.dropdown-content').forEach(content => {
        content.style.display = 'none';
    });
    
    const targetContent = document.getElementById(targetId);
    if(targetContent) {
        targetContent.style.display = 'flex';
        dropdownArea.classList.add('active');
    }
}

function hideDropdown() {
    dropdownArea.classList.remove('active');
    setTimeout(() => {
        document.querySelectorAll('.dropdown-content').forEach(content => {
            content.style.display = 'none';
        });
    }, 400);
}

// Smooth scroll
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        const href = this.getAttribute('href');
        if (href !== '#') {
            e.preventDefault();
            const target = document.querySelector(href);
            if(target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        }
    });
});
// Inicializar Lucide Icons
document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();
});

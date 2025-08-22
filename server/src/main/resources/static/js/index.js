const hamburer = document.querySelector(".hamburger");
const navList = document.querySelector(".nav-list");

if (hamburer) {
  hamburer.addEventListener("click", () => {
    navList.classList.toggle("open");
  });
}


// LISTING FORM 
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("listingForm");
  const propertyList = document.getElementById("propertyList");
  const searchInput = document.getElementById("searchInput");

  console.log(form);

  // Handle new property listing
  form.addEventListener("submit", function (e) {
    e.preventDefault();

    const title = document.getElementById("title").value;
    const price = document.getElementById("price").value;
    const location = document.getElementById("location").value;
    const type = document.getElementById("type").value;
    const description = document.getElementById("description").value;
    const imageFile = document.getElementById("image").files[0];

    if (!imageFile) return alert("Please upload an image");

    const reader = new FileReader();
    reader.onload = function (event) {
      const card = document.createElement("div");
      card.classList.add("property-card");
      card.innerHTML = `
        <img src="${event.target.result}" alt="${title}">
        <h3>${title}</h3>
        <p class="price">₱${price}</p>
        <p class="location">${location}</p>
        <p class="type">${type === "rent" ? "For Rent" : "For Sale"}</p>
        <p class="desc">${description}</p>
      `;
      propertyList.appendChild(card);
    };
    reader.readAsDataURL(imageFile);

    form.reset();
  });

  // Search filter
  searchInput.addEventListener("input", function () {
    const filter = searchInput.value.toLowerCase();
    const cards = propertyList.getElementsByClassName("property-card");

    Array.from(cards).forEach(card => {
      const title = card.querySelector("h3").textContent.toLowerCase();
      const location = card.querySelector(".location").textContent.toLowerCase();
      if (title.includes(filter) || location.includes(filter)) {
        card.style.display = "";
      } else {
        card.style.display = "none";
      }
    });
  });
});


// FORM VALIDATION
document.addEventListener('DOMContentLoaded', () => {

    function setupFormValidation(formId) {
        const form = document.getElementById(formId);
        if (!form) return;

        const inputs = form.querySelectorAll('input, select, textarea');

        function validateField(input) {
            if (!input.hasAttribute('required')) return true;

            const type = input.type;
            const tag = input.tagName.toLowerCase();

            if (type === 'file') return input.files.length > 0;
            if (type === 'number') return input.value.trim() !== '' && !isNaN(input.value) && Number(input.value) >= 0;
            if (tag === 'select') return input.value.trim() !== '';
            
            return input.value.trim() !== '';
        }

        function toggleError(input) {
            const errorMessage = input.nextElementSibling;
            if (!errorMessage) return;
            errorMessage.style.display = validateField(input) ? 'none' : 'block';
        }

        // Live validation
        inputs.forEach(input => {
            input.addEventListener('input', () => toggleError(input));
            if (input.type === 'file' || input.tagName.toLowerCase() === 'select') {
                input.addEventListener('change', () => toggleError(input));
            }
            input.addEventListener('blur', () => toggleError(input));
        });

        // Submit validation
        form.addEventListener('submit', (e) => {
            let allValid = true;

            inputs.forEach(input => {
                toggleError(input);
                if (!validateField(input)) allValid = false;
            });

            if (!allValid) {
                e.preventDefault();
                const firstError = form.querySelector('.error-message[style*="block"]');
                if (firstError) firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        });
    }

    // Initialize validation for CREATE and EDIT property
    setupFormValidation('form-edit-property');
    setupFormValidation('form-create-property');
});

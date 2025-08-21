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


// VALIDATION FOR UPDATING PROPERTIES
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('form-edit-property');
    const inputs = form.querySelectorAll('input, textarea');

    inputs.forEach(input => {
        const errorMessage = input.nextElementSibling;

        // Show error if input is empty
        input.addEventListener('blur', () => {
            if (!validateField(input)) {
                errorMessage.style.display = 'block';
            } else {
                errorMessage.style.display = 'none';
            }
        });

        // Hide error while typing / selecting file
        input.addEventListener('input', () => {
            if (validateField(input)) {
                errorMessage.style.display = 'none';
            } else {
                errorMessage.style.display = 'block';
            }
        });

        if (input.type === 'file') {
            input.addEventListener('change', () => {
                if (validateField(input)) {
                    errorMessage.style.display = 'none';
                } else {
                    errorMessage.style.display = 'block';
                }
            });
        }
    });

    // Final form submit validation
    form.addEventListener('submit', (e) => {
        let valid = true;
        inputs.forEach(input => {
            const errorMessage = input.nextElementSibling;
            if (!validateField(input)) {
                errorMessage.style.display = 'block';
                valid = false;
            } else {
                errorMessage.style.display = 'none';
            }
        });
        
        if (!valid) {
            e.preventDefault();
        }
    });

    function validateField(input) {
        if (input.hasAttribute('required')) {
            if (input.type === 'file') {
                return input.files.length > 0;
            }
            if (input.type === 'number') {
                return input.value !== '' && !isNaN(input.value) && input.value > 0;
            }
            return input.value.trim() !== '';
        }
        return true;
    }
});


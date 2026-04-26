import { api } from "/js/api.js";

const state = {
    customers: [],
    cities: [],
    countries: []
};

const elements = {
    customerForm: document.getElementById("customerForm"),
    customerId: document.getElementById("customerId"),
    customerName: document.getElementById("customerName"),
    customerDob: document.getElementById("customerDob"),
    customerNic: document.getElementById("customerNic"),
    mobileNumbers: document.getElementById("mobileNumbers"),
    familyMembers: document.getElementById("familyMembers"),
    deleteCustomer: document.getElementById("deleteCustomer"),
    resetCustomerForm: document.getElementById("resetCustomerForm"),
    addAddressRow: document.getElementById("addAddressRow"),
    addressRows: document.getElementById("addressRows"),
    addressRowTemplate: document.getElementById("addressRowTemplate"),
    customerTableBody: document.getElementById("customerTableBody"),
    nicSearch: document.getElementById("nicSearch"),
    customerCount: document.getElementById("customerCount"),
    cityCount: document.getElementById("cityCount"),
    countryCount: document.getElementById("countryCount"),
    uploadForm: document.getElementById("uploadForm"),
    uploadFile: document.getElementById("uploadFile"),
    countryForm: document.getElementById("countryForm"),
    countryName: document.getElementById("countryName"),
    countryList: document.getElementById("countryList"),
    cityForm: document.getElementById("cityForm"),
    cityName: document.getElementById("cityName"),
    cityList: document.getElementById("cityList"),
    toast: document.getElementById("toast")
};

function init() {
    bindEvents();
    addAddressRow();
    refreshAll();
}

function bindEvents() {
    elements.customerForm.addEventListener("submit", saveCustomer);
    elements.deleteCustomer.addEventListener("click", removeSelectedCustomer);
    elements.resetCustomerForm.addEventListener("click", resetCustomerForm);
    elements.addAddressRow.addEventListener("click", () => addAddressRow());
    elements.uploadForm.addEventListener("submit", uploadWorkbook);
    elements.countryForm.addEventListener("submit", saveCountry);
    elements.cityForm.addEventListener("submit", saveCity);
    elements.nicSearch.addEventListener("input", renderCustomers);
}

async function refreshAll() {
    try {
        const [customers, cities, countries] = await Promise.all([
            api.getCustomers(),
            api.getCities(),
            api.getCountries()
        ]);

        state.customers = customers;
        state.cities = cities;
        state.countries = countries;

        renderCounts();
        renderCustomers();
        renderFamilyMemberOptions();
        renderReferenceList(elements.cityList, state.cities, deleteCity);
        renderReferenceList(elements.countryList, state.countries, deleteCountry);
    } catch (error) {
        showToast(error.message, true);
    }
}

function renderCounts() {
    elements.customerCount.textContent = state.customers.length;
    elements.cityCount.textContent = state.cities.length;
    elements.countryCount.textContent = state.countries.length;
}

function renderFamilyMemberOptions() {
    const selectedIds = new Set(getSelectedFamilyMemberIds());
    elements.familyMembers.innerHTML = "";

    state.customers.forEach((customer) => {
        const option = document.createElement("option");
        option.value = String(customer.id);
        option.textContent = `${customer.name} (${customer.nic})`;
        option.selected = selectedIds.has(customer.id);
        elements.familyMembers.appendChild(option);
    });
}

function renderCustomers() {
    const filter = elements.nicSearch.value.trim().toLowerCase();
    const rows = state.customers
        .filter((customer) => !filter || (customer.nic || "").toLowerCase().includes(filter))
        .map((customer) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${escapeHtml(customer.name || "")}</td>
                <td>${escapeHtml(customer.nic || "")}</td>
                <td>${formatDate(customer.dob)}</td>
                <td>${escapeHtml((customer.mobileNumbers || []).join(", "))}</td>
                <td>${renderAddresses(customer.addresses || [])}</td>
                <td>${escapeHtml((customer.familyMembers || []).map((item) => item.name).join(", "))}</td>
                <td>
                    <div class="table-actions">
                        <button class="button button--secondary" type="button" data-action="edit" data-id="${customer.id}">Edit</button>
                        <button class="button button--ghost" type="button" data-action="delete" data-id="${customer.id}">Delete</button>
                    </div>
                </td>
            `;
            return tr;
        });

    elements.customerTableBody.innerHTML = "";
    rows.forEach((row) => elements.customerTableBody.appendChild(row));

    elements.customerTableBody.querySelectorAll("button").forEach((button) => {
        button.addEventListener("click", handleTableAction);
    });
}

function renderAddresses(addresses) {
    if (!addresses.length) {
        return "No addresses";
    }

    return addresses.map((address) => {
        const parts = [
            address.line1,
            address.line2,
            address.city?.name,
            address.country?.name
        ].filter(Boolean);
        return escapeHtml(parts.join(", "));
    }).join("<br>");
}

function renderReferenceList(container, items, onDelete) {
    container.innerHTML = "";

    items.forEach((item) => {
        const pill = document.createElement("div");
        pill.className = "pill";
        pill.innerHTML = `
            <span>${escapeHtml(item.name || "")}</span>
            <button type="button" aria-label="Delete ${escapeHtml(item.name || "")}">&times;</button>
        `;
        pill.querySelector("button").addEventListener("click", () => onDelete(item.id));
        container.appendChild(pill);
    });
}

function addAddressRow(address = {}) {
    const fragment = elements.addressRowTemplate.content.cloneNode(true);
    const row = fragment.querySelector(".address-row");

    row.querySelector('[data-field="line1"]').value = address.line1 || "";
    row.querySelector('[data-field="line2"]').value = address.line2 || "";
    row.querySelector('[data-field="city"]').value = address.city?.name || address.city || "";
    row.querySelector('[data-field="country"]').value = address.country?.name || address.country || "";

    row.querySelector(".address-row__remove").addEventListener("click", () => {
        row.remove();
        if (!elements.addressRows.children.length) {
            addAddressRow();
        }
    });

    elements.addressRows.appendChild(fragment);
}

async function saveCustomer(event) {
    event.preventDefault();

    const payload = {
        name: elements.customerName.value.trim(),
        dob: elements.customerDob.value,
        nic: elements.customerNic.value.trim(),
        mobileNumbers: splitCsv(elements.mobileNumbers.value),
        addresses: readAddressRows(),
        familyMemberIds: getSelectedFamilyMemberIds()
    };

    try {
        const id = elements.customerId.value;
        if (id) {
            await api.updateCustomer(id, payload);
            showToast("Customer updated");
        } else {
            await api.createCustomer(payload);
            showToast("Customer created");
        }

        resetCustomerForm();
        await refreshAll();
    } catch (error) {
        showToast(error.message, true);
    }
}

async function removeSelectedCustomer() {
    const id = elements.customerId.value;
    if (!id) {
        return;
    }

    try {
        await api.deleteCustomer(id);
        showToast("Customer deleted");
        resetCustomerForm();
        await refreshAll();
    } catch (error) {
        showToast(error.message, true);
    }
}

async function handleTableAction(event) {
    const button = event.currentTarget;
    const action = button.dataset.action;
    const id = Number(button.dataset.id);

    if (action === "edit") {
        const customer = state.customers.find((item) => item.id === id);
        if (customer) {
            fillCustomerForm(customer);
        }
        return;
    }

    if (action === "delete") {
        try {
            await api.deleteCustomer(id);
            showToast("Customer deleted");
            if (Number(elements.customerId.value) === id) {
                resetCustomerForm();
            }
            await refreshAll();
        } catch (error) {
            showToast(error.message, true);
        }
    }
}

function fillCustomerForm(customer) {
    elements.customerId.value = customer.id || "";
    elements.customerName.value = customer.name || "";
    elements.customerDob.value = normalizeDateInput(customer.dob);
    elements.customerNic.value = customer.nic || "";
    elements.mobileNumbers.value = (customer.mobileNumbers || []).join(", ");

    elements.addressRows.innerHTML = "";
    if ((customer.addresses || []).length) {
        customer.addresses.forEach((address) => addAddressRow(address));
    } else {
        addAddressRow();
    }

    const selected = new Set((customer.familyMembers || []).map((item) => item.id));
    Array.from(elements.familyMembers.options).forEach((option) => {
        option.selected = selected.has(Number(option.value));
    });

    elements.deleteCustomer.disabled = false;
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetCustomerForm() {
    elements.customerForm.reset();
    elements.customerId.value = "";
    elements.addressRows.innerHTML = "";
    addAddressRow();
    Array.from(elements.familyMembers.options).forEach((option) => {
        option.selected = false;
    });
    elements.deleteCustomer.disabled = true;
}

async function uploadWorkbook(event) {
    event.preventDefault();

    const file = elements.uploadFile.files[0];
    if (!file) {
        showToast("Choose a file first", true);
        return;
    }

    try {
        const message = await api.uploadCustomers(file);
        showToast(typeof message === "string" ? message : "Upload successful");
        elements.uploadForm.reset();
        await refreshAll();
    } catch (error) {
        showToast(error.message, true);
    }
}

async function saveCountry(event) {
    event.preventDefault();

    try {
        await api.createCountry(elements.countryName.value.trim());
        elements.countryForm.reset();
        showToast("Country saved");
        await refreshAll();
    } catch (error) {
        showToast(error.message, true);
    }
}

async function saveCity(event) {
    event.preventDefault();

    try {
        await api.createCity(elements.cityName.value.trim());
        elements.cityForm.reset();
        showToast("City saved");
        await refreshAll();
    } catch (error) {
        showToast(error.message, true);
    }
}

async function deleteCountry(id) {
    try {
        await api.deleteCountry(id);
        showToast("Country deleted");
        await refreshAll();
    } catch (error) {
        showToast(error.message, true);
    }
}

async function deleteCity(id) {
    try {
        await api.deleteCity(id);
        showToast("City deleted");
        await refreshAll();
    } catch (error) {
        showToast(error.message, true);
    }
}

function readAddressRows() {
    return Array.from(elements.addressRows.querySelectorAll(".address-row"))
        .map((row) => ({
            line1: row.querySelector('[data-field="line1"]').value.trim(),
            line2: row.querySelector('[data-field="line2"]').value.trim(),
            city: row.querySelector('[data-field="city"]').value.trim(),
            country: row.querySelector('[data-field="country"]').value.trim()
        }))
        .filter((address) => address.line1 || address.line2 || address.city || address.country);
}

function getSelectedFamilyMemberIds() {
    return Array.from(elements.familyMembers.selectedOptions)
        .map((option) => Number(option.value))
        .filter((value) => !Number.isNaN(value));
}

function splitCsv(value) {
    return value
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);
}

function formatDate(value) {
    if (!value) {
        return "-";
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return escapeHtml(String(value));
    }

    return date.toLocaleDateString();
}

function normalizeDateInput(value) {
    if (!value) {
        return "";
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return "";
    }

    return date.toISOString().slice(0, 10);
}

function escapeHtml(value) {
    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

let toastTimer;
function showToast(message, isError = false) {
    elements.toast.textContent = message;
    elements.toast.hidden = false;
    elements.toast.classList.toggle("is-error", isError);
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => {
        elements.toast.hidden = true;
    }, 3200);
}

init();

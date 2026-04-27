const JSON_HEADERS = {
    "Content-Type": "application/json"
};

async function request(path, options = {}) {
    const response = await fetch(path, options);

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Request failed");
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }

    return response.text();
}

export const api = {
    getCustomers() {
        return request("/customers");
    },
    createCustomer(payload) {
        return request("/customers", {
            method: "POST",
            headers: JSON_HEADERS,
            body: JSON.stringify(payload)
        });
    },
    updateCustomer(id, payload) {
        return request(`/customers/${id}`, {
            method: "PUT",
            headers: JSON_HEADERS,
            body: JSON.stringify(payload)
        });
    },
    deleteCustomer(id) {
        return request(`/customers/${id}`, {
            method: "DELETE"
        });
    },
    uploadCustomers(file) {
        const formData = new FormData();
        formData.append("file", file);

        return request("/customers/upload", {
            method: "POST",
            body: formData
        });
    },
    getCities() {
        return request("/cities");
    },
    createCity(name, countryName) {
        return request("/cities", {
            method: "POST",
            headers: JSON_HEADERS,
            body: JSON.stringify({ name, countryName })
        });
    },
    deleteCity(id) {
        return request(`/cities/${id}`, {
            method: "DELETE"
        });
    },
    getCountries() {
        return request("/countries");
    },
    createCountry(name) {
        return request("/countries", {
            method: "POST",
            headers: JSON_HEADERS,
            body: JSON.stringify({ name })
        });
    },
    deleteCountry(id) {
        return request(`/countries/${id}`, {
            method: "DELETE"
        });
    }
};

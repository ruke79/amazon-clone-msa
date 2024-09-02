class TokenUtil {

    getToken() {
        return localStorage.getItem("access_token");
    }

    updateToken(token) {
        localStorage.setItem("access_token", token);
    }

    removeToken() {
        localStorage.removeItem("access_token");
    }

    isAdmin() {
        return localStorage.getItem("IS_ADMIN");
    }

    setAdmin(isAdmin) {
        localStorage.setItem("IS_ADMIN", JSON.stringify(isAdmin));
    }
    removeAdmin() {
        localStorage.removeItem("IS_ADMIN");
    }

    getUser() {
        return JSON.parse(localStorage.getItem("USER"));
    }

    setUser(user) {
        localStorage.setItem("USER", JSON.stringify(user));
    }

    remove() {
        localStorage.clear();    
    }
}

export default new TokenUtil();
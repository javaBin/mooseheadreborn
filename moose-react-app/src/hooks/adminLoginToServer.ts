import {AdminLoginInput, UserLogin} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const adminLoginToServer = (adminLoginInput:AdminLoginInput):Promise<UserLogin> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/adminlogin", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(adminLoginInput)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            })
            .then(json => {
                resolve(json);
            })
            .catch(error => {
                reject(error.message);
            });
    });

};

export default adminLoginToServer;
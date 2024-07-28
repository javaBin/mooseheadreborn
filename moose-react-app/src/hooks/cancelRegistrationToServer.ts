import {CancelRegistrationInput, CancelRegistrationOutput} from "../ServerTypes";
import ServerConfig from "../ServerConfig";


const cancelRegistrationToServer = (cancelRegistrationInput:CancelRegistrationInput):Promise<CancelRegistrationOutput> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/cancelRegistration", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(cancelRegistrationInput)
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

export default cancelRegistrationToServer;
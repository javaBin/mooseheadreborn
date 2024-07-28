import {AddRegistrationInput, AddRegistrationOutput} from "../ServerTypes";
import ServerConfig from "../ServerConfig";



const addRegistrationToServer = (addRegistrationInput:AddRegistrationInput):Promise<AddRegistrationOutput> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/addRegistration", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(addRegistrationInput)
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
                resolve(json)
            })
            .catch(error => {
                reject(error.message);
            });
    })
};


export default addRegistrationToServer;
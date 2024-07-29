import ServerConfig from "../ServerConfig";
import {UserLogin} from "../ServerTypes";



const activateUserOnServer = (registerKey:String):Promise<UserLogin> => {
    return new Promise((resolve, reject) => {
        const activateInput = {
            registerKey: registerKey
        };
        fetch(ServerConfig.address + "/api/activateParticipant", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(activateInput)
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
    })
}

export default activateUserOnServer;
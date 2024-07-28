import ServerConfig from "../ServerConfig";
import {json} from "node:stream/consumers";



const activateUserOnServer = (registerKey:String):Promise<string> => {
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
                resolve(json.accessToken);
            })
            .catch(error => {
                reject(error.message);
            });
    })
}

export default activateUserOnServer;
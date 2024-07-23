import ServerConfig from "../ServerConfig";
import {json} from "node:stream/consumers";

interface ActivateUserOnServerResult {
    accessKey: string|null;
    errormessage: string|null;
}

const activateUserOnServer = (registerKey:String):Promise<ActivateUserOnServerResult> => {
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
                console.log("fromserveractivate",json)
                resolve({accessKey : json.accessToken,errormessage:null})
            })
            .catch(error => {
                resolve({accessKey : null,errormessage:error.message});
            });
    })
}

export default activateUserOnServer;
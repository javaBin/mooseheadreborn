import {CancelRegistrationInput, CancelRegistrationOutput} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

interface CancelRegistratioToServerResult {
    cancelRegistrationOutput:CancelRegistrationOutput|null;
    errormessage:string|null;
}

const cancelRegistrationToServer = (cancelRegistrationInput:CancelRegistrationInput):Promise<CancelRegistratioToServerResult> => {
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
                resolve({cancelRegistrationOutput:json,errormessage:null})
            })
            .catch(error => {
                resolve({cancelRegistrationOutput : null,errormessage:error.message});
            });
    })
};

export default cancelRegistrationToServer;
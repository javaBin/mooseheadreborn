import {AddRegistrationInput, AddRegistrationOutput} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

interface AddRegistratioToServerResult {
    addRegistrationOutput:AddRegistrationOutput|null;
    errormessage:string|null;
}

const addRegistrationToServer = (addRegistrationInput:AddRegistrationInput):Promise<AddRegistratioToServerResult> => {
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
                resolve({addRegistrationOutput:json,errormessage:null})
            })
            .catch(error => {
                resolve({addRegistrationOutput : null,errormessage:error.message});
            });
    })
};


export default addRegistrationToServer;